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
            Order newOrder = OrderMapper.mapOrderRequestToOrder(request);
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
            OpenTime ot = openTimeRepository.findSpecial(Date.valueOf(date));
            if (ot == null) ot = openTimeRepository.findRegular();
            int intervalMin = ot.getInterval();
            LocalTime open = ot.getOpenTime().toLocalTime();
            LocalTime close = ot.getCloseTime().toLocalTime();
            LocalDateTime openDT = date.atTime(open);
            LocalDateTime closeDT = date.atTime(close);
            LocalDateTime lastStart = closeDT.minusMinutes(DURATION_MIN);
            List<Table> types = tableRepository.findAll();
            TreeMap<Integer, Integer> total = new TreeMap<>();
            for (Table t : types) total.put(t.getCapacity(), t.getQuantity());
            List<Order> dayOrders = orderRepository.findAllDateOrders(date);
            List<LocalTime> result = new ArrayList<>();
            for (LocalDateTime slot = openDT; !slot.isAfter(lastStart); slot = slot.plusMinutes(intervalMin)) {
                List<Order> collide = new ArrayList<>();
                LocalDateTime slotEnd = slot.plusMinutes(DURATION_MIN);
                for (Order o : dayOrders) {
                    LocalDateTime start = o.getOrderDateTime();
                    LocalDateTime end = start.plusMinutes(DURATION_MIN);
                    if (start.isBefore(slotEnd) && slot.isBefore(end)) collide.add(o);
                }
                if (isSlotAvailable(slot, numberOfGuests, dayOrders, total, intervalMin, DURATION_MIN))
                    result.add(slot.toLocalTime());
            }
            return result;
        });
    }

    private boolean isSlotAvailable(LocalDateTime slot, int guests, List<Order> dayOrders, TreeMap<Integer, Integer> total, int intervalMin, int durationMin) {
        LocalDateTime slotEnd = slot.plusMinutes(durationMin);
        for (LocalDateTime t = slot; t.isBefore(slotEnd); t = t.plusMinutes(intervalMin)) {
            List<Order> active = new ArrayList<>();
            for (Order o : dayOrders) {
                LocalDateTime start = o.getOrderDateTime();
                LocalDateTime end = start.plusMinutes(durationMin);
                if (!start.isAfter(t) && t.isBefore(end)) active.add(o);
            }
            if (!canFitExtraGroup(total, active, guests)) return false;
        }
        return true;
    }

    private boolean canFitExtraGroup(TreeMap<Integer, Integer> total, List<Order> collide, int extraGuests) {
        TreeMap<Integer, Integer> available = new TreeMap<>(total);
        List<Integer> groups = new ArrayList<>();
        for (Order o : collide) groups.add(o.getNumberOfGuests());
        groups.add(extraGuests);
        groups.sort(Comparator.reverseOrder());
        for (int g : groups) {
            Integer cap = available.ceilingKey(g);
            while (cap != null && available.get(cap) == 0) cap = available.higherKey(cap);
            if (cap == null) return false;
            available.put(cap, available.get(cap) - 1);
        }
        return true;
    }
}