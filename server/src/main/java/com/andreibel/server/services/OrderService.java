package com.andreibel.server.services;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OpenTimeRepository;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.TableRepository;
import com.andreibel.server.entity.OpenTime;
import com.andreibel.server.entity.Order;
import com.andreibel.server.entity.Table;
import com.andreibel.server.utils.OrderMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class OrderService {

    private static OrderService instance;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final OpenTimeRepository openTimeRepository;
    private final TransactionManager tx;

    private OrderService() {
        this.orderRepository = OrderRepository.getInstance();
        this.tableRepository = TableRepository.getInstance();
        this.openTimeRepository = OpenTimeRepository.getInstance();
        this.tx = TransactionManager.getInstance();
    }

    public static OrderService getInstance() {
        if (instance == null) instance = new OrderService();
        return instance;
    }

    public List<OrderResponse> getAllOrders() {
        return tx.inTransaction(orderRepository::findAll).stream().map(OrderMapper::mapOrderToOrderResponse).toList();
    }

    public OrderResponse createOrder(OrderRequest request) {
        return tx.inTransaction(() -> {
            List<Table> types = tableRepository.findAll();
            List<Order> collide = orderRepository.findOrdersCollideByDateTime(request.getOrderDateTime());
            Order newOrder = OrderMapper.mapNewOrderRequestToOrder(request);
            TreeMap<Integer, Integer> available = new TreeMap<>();
            for (Table t : types) {
                available.merge(t.getCapacity(), t.getQuantity(), Integer::sum);
            }

            Map<LocalDateTime, List<Order>> starts = new HashMap<>();
            for (Order o : collide) {
                starts.computeIfAbsent(o.getOrderDateTime(), k -> new ArrayList<>()).add(o);
            }
            Map<Integer, Integer> assignedCap = new HashMap<>();
            LocalDateTime from = request.getOrderDateTime().minusHours(2);
            LocalDateTime to = request.getOrderDateTime().plusHours(2);
            for (LocalDateTime t = from; t.isBefore(to); t.plusMinutes(30)) {
                // Free orders that ended now (started exactly 2h ago)
                LocalDateTime endedStart = t.minusHours(2);
                for (Order o : starts.getOrDefault(endedStart, List.of())) {
                    Integer cap = assignedCap.remove(o.getOrderNumber());
                    if (cap != null) available.put(cap, available.get(cap) + 1);
                }

                // Assign tables to orders starting now
                List<Order> startingNow = new ArrayList<>(starts.getOrDefault(t, List.of()));
                startingNow.sort(Comparator.comparingInt(Order::getNumberOfGuests).reversed());

                for (Order o : startingNow) {
                    int g = o.getNumberOfGuests();

                    Integer cap = available.ceilingKey(g);
                    while (cap != null && available.get(cap) == 0) {
                        cap = available.higherKey(cap);
                    }
                    if (cap == null) return null;

                    available.put(cap, available.get(cap) - 1);
                    assignedCap.put(o.getOrderNumber(), cap);
                }
            }
            return OrderMapper.mapOrderToOrderResponse(orderRepository.save(newOrder));
        });
    }


    public OrderResponse getOrderByConformationCode(OrderRequest data) {
        return tx.inTransaction(() -> OrderMapper.mapOrderToOrderResponse(orderRepository.findByConformationCode(data.getConformationCode())));
    }

    public OrderResponse deleteOrder(UUID conformationCode) {
        return tx.inTransaction(() -> {
            orderRepository.deleteByConformationCode(conformationCode);
            Order order = orderRepository.findByConformationCode(conformationCode);
            return OrderMapper.mapOrderToOrderResponse(order);
        });
    }

    public OrderResponse orderArrives(UUID conformationCode) {
        return tx.inTransaction(() -> {
            orderRepository.setArrived(conformationCode);
            Order order = orderRepository.findByConformationCode(conformationCode);
            return OrderMapper.mapOrderToOrderResponse(order);
        });
    }

    public OrderResponse completeOrder(UUID conformationCode) {
        return tx.inTransaction(() -> {
            orderRepository.completeOrder(conformationCode);
            return OrderMapper.mapOrderToOrderResponse(orderRepository.findByConformationCode(conformationCode));
        });
    }

    public List<LocalTime> getAllAvailableTimeInDate(LocalDate date, int numberOfGuests) {
        final int DURATION_MIN = 120;
        return tx.inTransaction(() -> {
            // find the open time for the date
            OpenTime ot = openTimeRepository.findSpecial(Date.valueOf(date));
            if (ot == null) ot = openTimeRepository.findRegular();
            int intervalMin = ot.getInterval();
            LocalTime open = ot.getOpenTime().toLocalTime();
            LocalTime close = ot.getCloseTime().toLocalTime();

            // reservation duration in minutes (your collide query assumes 2 hours)
            LocalDateTime openDT = date.atTime(open);
            LocalDateTime closeDT = date.atTime(close);
            LocalDateTime lastStart = closeDT.minusMinutes(DURATION_MIN);

            // Table stock capacity -> quantity
            List<Table> types = tableRepository.findAll();
            TreeMap<Integer, Integer> total = new TreeMap<>();
            for (Table t : types) total.put(t.getCapacity(), t.getQuantity());
            System.out.println(total);
            List<Order> dayOrders = orderRepository.findAllDateOrders(date);
            List<LocalTime> result = new ArrayList<>();
            for (LocalDateTime slot = openDT; !slot.isAfter(lastStart); slot = slot.plusMinutes(intervalMin)) {
                // orders that overlap reservation window [slot, slot + 2h)
                List<Order> collide = new ArrayList<>();
                LocalDateTime slotEnd = slot.plusMinutes(DURATION_MIN);

                for (Order o : dayOrders) {
                    LocalDateTime start = o.getOrderDateTime();
                    LocalDateTime end = start.plusMinutes(DURATION_MIN);

                    // overlap check: start < slotEnd && slot < end
                    if (start.isBefore(slotEnd) && slot.isBefore(end)) {
                        collide.add(o);
                    }
                }
                System.out.println("time: " + slot);
                collide.forEach(System.out::println);
                if (isSlotAvailable(slot, numberOfGuests, dayOrders, total, intervalMin, DURATION_MIN)) {
                    result.add(slot.toLocalTime());
                }
            }


            return result;
        });
    }

    private boolean isSlotAvailable(LocalDateTime slot, int guests, List<Order> dayOrders, TreeMap<Integer, Integer> total, int intervalMin, int durationMin) {

        LocalDateTime slotEnd = slot.plusMinutes(durationMin);

        // נבדוק רק נקודות שינוי בתוך החלון: slot, slot+interval, ...
        for (LocalDateTime t = slot; t.isBefore(slotEnd); t = t.plusMinutes(intervalMin)) {

            // הזמנות פעילות בזמן t (חלון פעיל של כל הזמנה הוא [start, start+duration))
            List<Order> active = new ArrayList<>();
            for (Order o : dayOrders) {
                LocalDateTime start = o.getOrderDateTime();
                LocalDateTime end = start.plusMinutes(durationMin);

                // חופף לנקודת זמן t אם start <= t < end
                // (נכון עם isAfter/isBefore בלי <=: start.isAfter(t) לא מאפשר equality, לכן נשתמש !start.isAfter(t))
                if (!start.isAfter(t) && t.isBefore(end)) {
                    active.add(o);
                }
            }

            // עכשיו צריך להיות אפשר לשבץ את כל הפעילים + ההזמנה החדשה
            if (!canFitExtraGroup(total, active, guests)) {
                return false;
            }
        }
        return true;
    }

    private boolean canFitExtraGroup(TreeMap<Integer, Integer> total, List<Order> collide, int extraGuests) {

        TreeMap<Integer, Integer> available = new TreeMap<>(total);

        List<Integer> groups = new ArrayList<>();
        for (Order o : collide) groups.add(o.getNumberOfGuests());
        groups.add(extraGuests);

        groups.sort(Comparator.reverseOrder()); // גדול -> קטן

        for (int g : groups) {
            Integer cap = available.ceilingKey(g);
            while (cap != null && available.get(cap) == 0) cap = available.higherKey(cap);
            if (cap == null) return false;
            available.put(cap, available.get(cap) - 1);
        }
        return true;
    }
}