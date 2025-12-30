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
import com.andreibel.server.utils.SimpleInvoicePdf;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
 /**
 * Application service responsible for managing restaurant orders and computing availability.
 *
 * <p>
 * This service is the transactional boundary for all order-related operations. It delegates
 * persistence to repositories ({@link OrderRepository}, {@link TableRepository}, {@link OpenTimeRepository})
 * block to ensure a valid JDBC {@link java.sql.Connection} is available.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Create and query orders (CRUD-like operations).</li>
 *   <li>Update order state: arrived (check-in), completed, cancelled.</li>
 *   <li>Compute available reservation start times for a given date and party size.</li>
 * </ul>
 *
 * <h2>Availability model</h2>
 * <ul>
 *   <li>Reservation duration is fixed to <b>120 minutes</b> (2 hours).</li>
 *   <li>Reservations can start only on discrete time slots of size {@code intervalMin}
 *       (configured in {@link OpenTime}).</li>
 *   <li>Restaurant opening and closing times are determined per date:
 *       first by a special-day record ({@link OpenTimeRepository#findSpecial(Date)}),
 *       otherwise by a regular/default record ({@link OpenTimeRepository#findRegular()}).</li>
 *   <li>Table inventory is modeled as a mapping: {@code capacity -> quantity}.</li>
 *   <li>A slot is considered available if the system can allocate tables for
 *       all currently active orders at each "change point" inside the reservation window
 *       <b>plus</b> the requested new party.</li>
 * </ul>
 *
 * <h2>Allocation strategy</h2>
 * <p>
 * Table allocation uses a greedy strategy:
 * sort groups by size descending, then for each group pick the smallest table capacity
 * that can fit the group (via {@link java.util.TreeMap#ceilingKey(Object)}), skipping capacities
 * whose remaining quantity is zero.
 * </p>
 *
 * <h2>Thread-safety</h2>
 * <p>
 * The class is implemented as a simple lazy Singleton without synchronization.
 * If multiple threads may call {@link #getInstance()} concurrently, consider synchronizing
 * the initialization or using an enum/safe singleton pattern.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class OrderService {

    private static OrderService instance;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final OpenTimeRepository openTimeRepository;
    private final TransactionManager tx;

    /**
     * Private constructor for Singleton pattern.
     *
     * <p>
     * Initializes repository dependencies and the transaction manager.
     * </p>
     */
    private OrderService() {
        this.orderRepository = OrderRepository.getInstance();
        this.tableRepository = TableRepository.getInstance();
        this.openTimeRepository = OpenTimeRepository.getInstance();
        this.tx = TransactionManager.getInstance();
    }

    /**
     * Returns the singleton instance of {@link OrderService}.
     *
     * @return singleton instance
     */
    public static OrderService getInstance() {
        if (instance == null) instance = new OrderService();
        return instance;
    }

    /**
     * Retrieves all orders from the database.
     *
     * <p>
     * Transactional read operation:
     * calls {@link OrderRepository#findAll()} and maps each {@link Order} to {@link OrderResponse}
     * using {@link OrderMapper#mapOrderToOrderResponse(Order)}.
     * </p>
     *
     * @return list of all orders as DTOs
     */
    public List<OrderResponse> getAllOrders() {
        return tx.inTransaction(orderRepository::findAll)
                .stream()
                .map(OrderMapper::mapOrderToOrderResponse)
                .toList();
    }

    /**
     * Creates a new order in the database.
     *
     * <p>
     * Transactional write operation:
     * <ol>
     *   <li>Maps {@link OrderRequest} to a new {@link Order} entity.</li>
     *   <li>Persists the entity via {@link OrderRepository#save(Order)}.</li>
     *   <li>Maps persisted entity to {@link OrderResponse}.</li>
     * </ol>
     * </p>
     *
     * @param request order request DTO
     * @return created order as DTO (persisted state)
     */
    public OrderResponse createOrder(OrderRequest request) {
        return tx.inTransaction(() -> {
            Order newOrder = OrderMapper.mapNewOrderRequestToOrder(request);
            return OrderMapper.mapOrderToOrderResponse(orderRepository.save(newOrder));
        });
    }

    /**
     * Retrieves a single order by confirmation code (provided inside the request).
     *
     * <p>
     * Transactional read operation:
     * calls {@link OrderRepository#findByConformationCode(java.util.UUID)} and maps the result
     * to {@link OrderResponse}.
     * </p>
     *
     * @param conformationCode request containing {@code conformationCode}
     * @return matching order as DTO (may represent {@code null} if mapping permits)
     */
    public OrderResponse getOrderByConformationCode(UUID conformationCode) {
        return tx.inTransaction(() ->
                OrderMapper.mapOrderToOrderResponse(
                        orderRepository.findByConformationCode(conformationCode)
                )
        );
    }

    /**
     * Cancels (soft-deletes) an order by its confirmation code.
     *
     * <p>
     * Transactional write operation:
     * <ol>
     *   <li>Updates the row via {@link OrderRepository#deleteByConformationCode(java.util.UUID)}
     *       (typically sets {@code orderCancelled = 1}).</li>
     *   <li>Re-reads the updated order via {@link OrderRepository#findByConformationCode(java.util.UUID)}.</li>
     *   <li>Maps the updated entity to {@link OrderResponse}.</li>
     * </ol>
     * </p>
     *
     * @param conformationCode confirmation code identifying the order to cancel
     * @return updated (cancelled) order as DTO
     */
    public OrderResponse deleteOrder(UUID conformationCode) {
        return tx.inTransaction(() -> {
            orderRepository.deleteByConformationCode(conformationCode);
            Order order = orderRepository.findByConformationCode(conformationCode);
            return OrderMapper.mapOrderToOrderResponse(order);
        });
    }

    /**
     * Marks an order as "arrived" (checked-in) by its confirmation code.
     *
     * <p>
     * Transactional write operation:
     * <ol>
     *   <li>Sets arrived flag via {@link OrderRepository#setArrived(java.util.UUID)}.</li>
     *   <li>Re-reads order by confirmation code.</li>
     *   <li>Maps to {@link OrderResponse}.</li>
     * </ol>
     * </p>
     *
     * @param conformationCode confirmation code identifying the order
     * @return updated order as DTO (arrived state)
     */
    public OrderResponse orderArrives(UUID conformationCode) {
        //TODO: add logic of arriving to bistro but not palace now available,
        return tx.inTransaction(() -> {
            orderRepository.setArrived(conformationCode);
            Order order = orderRepository.findByConformationCode(conformationCode);
            return OrderMapper.mapOrderToOrderResponse(order);
        });
    }

    /**
     * Marks an order as completed/closed by its confirmation code.
     *
     * <p>
     * Transactional write operation:
     * calls {@link OrderRepository#completeOrder(java.util.UUID)} and returns the updated order.
     * </p>
     *
     * <p><b>Note:</b> verify that {@code completeOrder} updates {@code orderCompleted} rather than
     * {@code orderCancelled}. The current repository implementation may be using cancellation semantics.</p>
     *
     * @param conformationCode confirmation code identifying the order
     * @return updated order as DTO (completed state)
     */
    public OrderResponse completeOrder(UUID conformationCode) {
        OrderResponse orderResponse = tx.inTransaction(() -> {
            orderRepository.completeOrder(conformationCode);
            return OrderMapper.mapOrderToOrderResponse(orderRepository.findByConformationCode(conformationCode));
        });
        var data = new SimpleInvoicePdf.InvoiceData(
                "INV-2025-0007",
                orderResponse.getConformationCode().toString(),
                "Bistro",
                "Haifa, Israel",
                "Friend",
                orderResponse.getPhoneNumber(),
                orderResponse.getEmail(),
                orderResponse.getNumberOfGuests(),
                new BigDecimal("120.00"),
                orderResponse.getSubscriberId() != null
        );
        try {
            Path pdf = SimpleInvoicePdf.generate(
                    Paths.get("templates/invoice.html"),
                    Paths.get(System.getProperty("user.home"), "Documents", "BistroInvoices", "INV-2025-0007.pdf"),
                    Paths.get("fonts/NotoSansHebrew-Regular.ttf"),
                    data
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return orderResponse;
    }

    /**
     * Computes all available reservation start times for a given date and party size.
     *
     * <p>
     * The returned list includes only start times {@code t} such that:
     * </p>
     * <ul>
     *   <li>{@code t} is within the restaurant working hours for that date,</li>
     *   <li>{@code t + 2 hours} does not exceed closing time,</li>
     *   <li>for each "change point" within the reservation window (every {@code intervalMin} minutes),
     *       the existing active orders plus the new party can be allocated to the available tables.</li>
     * </ul>
     *
     * <h3>Data sources</h3>
     * <ul>
     *   <li>Opening hours and interval from {@link OpenTimeRepository} (special-day first, otherwise regular).</li>
     *   <li>Table inventory from {@link TableRepository#findAll()}.</li>
     *   <li>All orders starting on the requested date from {@link OrderRepository#findAllDateOrders(LocalDate)}.</li>
     * </ul>
     *
     * <h3>Algorithm (high level)</h3>
     * <ol>
     *   <li>Resolve working hours and slot interval for the given date.</li>
     *   <li>Build a capacity inventory map: {@code capacity -> quantity}.</li>
     *   <li>Generate candidate start slots from open time up to the last possible start time.</li>
     *   <li>For each slot, validate availability using {@link #isSlotAvailable(LocalDateTime, int, List, TreeMap, int, int)}.</li>
     * </ol>
     *
     * @param date calendar date to compute availability for
     * @param numberOfGuests requested party size
     * @return list of available start times as {@link LocalTime} (can be empty)
     */
    public List<LocalTime> getAllAvailableTimeInDate(LocalDate date, int numberOfGuests) {
        final int DURATION_MIN = 120;

        return tx.inTransaction(() -> {
            // Resolve opening hours for this date: special-day record overrides regular record.
            OpenTime ot = openTimeRepository.findSpecial(Date.valueOf(date));
            if (ot == null) ot = openTimeRepository.findRegular();

            // Assumes OpenTime exists and has non-null open/close times.
            int intervalMin = ot.getInterval();
            LocalTime open = ot.getOpenTime().toLocalTime();
            LocalTime close = ot.getCloseTime().toLocalTime();

            // Build the candidate slot range [openDT, lastStart].
            LocalDateTime openDT = date.atTime(open);
            LocalDateTime closeDT = date.atTime(close);
            LocalDateTime lastStart = closeDT.minusMinutes(DURATION_MIN);

            // Build table inventory: capacity -> quantity
            List<Table> types = tableRepository.findAll();
            TreeMap<Integer, Integer> total = new TreeMap<>();
            for (Table t : types) total.put(t.getCapacity(), t.getQuantity());

            // Fetch all orders that start on the requested date (single DB read).
            List<Order> dayOrders = orderRepository.findAllDateOrders(date);

            // Evaluate each slot and collect available start times.
            List<LocalTime> result = new ArrayList<>();
            for (LocalDateTime slot = openDT; !slot.isAfter(lastStart); slot = slot.plusMinutes(intervalMin)) {
                if (isSlotAvailable(slot, numberOfGuests, dayOrders, total, intervalMin, DURATION_MIN)) {
                    result.add(slot.toLocalTime());
                }
            }
            return result;
        });
    }

    /**
     * Determines whether a specific start slot is available for a requested party size.
     *
     * <p>
     * This method checks availability across the entire reservation window {@code [slot, slot + durationMin)}.
     * Instead of treating the whole window as a single moment, it evaluates the system state at each
     * "change point" separated by {@code intervalMin} minutes (e.g., each hour), because orders can start
     * or end only at these boundaries in this model.
     * </p>
     *
     * <h3>Active-order definition</h3>
     * <p>
     * An order is considered active at time {@code t} if it occupies the half-open interval:
     * {@code [start, start + durationMin)}, i.e. {@code start <= t < end}.
     * </p>
     *
     * <h3>Decision rule</h3>
     * <p>
     * For every change point {@code t} in the reservation window:
     * allocate tables for all active orders at time {@code t} plus one additional group ({@code guests}).
     * If allocation fails at any point, the slot is unavailable.
     * </p>
     *
     * @param slot reservation candidate start time
     * @param guests requested party size
     * @param dayOrders all orders that start on the given date
     * @param total table inventory map (capacity -> quantity)
     * @param intervalMin slot/step size in minutes (e.g., 60)
     * @param durationMin reservation duration in minutes (fixed: 120)
     * @return {@code true} if the slot is available across the entire duration, otherwise {@code false}
     */
    private boolean isSlotAvailable(LocalDateTime slot,
                                    int guests,
                                    List<Order> dayOrders,
                                    TreeMap<Integer, Integer> total,
                                    int intervalMin,
                                    int durationMin) {

        LocalDateTime slotEnd = slot.plusMinutes(durationMin);

        // Evaluate each "change point" in the reservation window.
        for (LocalDateTime t = slot; t.isBefore(slotEnd); t = t.plusMinutes(intervalMin)) {

            // Collect orders active at time t.
            List<Order> active = new ArrayList<>();
            for (Order o : dayOrders) {
                LocalDateTime start = o.getOrderDateTime();
                LocalDateTime end = start.plusMinutes(durationMin);

                // Active if start <= t < end
                if (!start.isAfter(t) && t.isBefore(end)) {
                    active.add(o);
                }
            }

            // If the system cannot allocate tables at this change point, the slot is not available.
            if (!canFitExtraGroup(total, active, guests)) return false;
        }

        return true;
    }

    /**
     * Checks whether the restaurant can fit all colliding/active groups plus an additional group
     * of size {@code extraGuests}, given a table inventory map.
     *
     * <p>
     * This method does not mutate the {@code total} inventory. It works on a local copy.
     * </p>
     *
     * <h3>Algorithm</h3>
     * <ol>
     *   <li>Copy the inventory map to a mutable {@code available} map.</li>
     *   <li>Build a list of group sizes: all active orders' {@code numberOfGuests} plus {@code extraGuests}.</li>
     *   <li>Sort groups descending (largest first).</li>
     *   <li>For each group size {@code g}:
     *     <ul>
     *       <li>Find the smallest capacity {@code cap} such that {@code cap >= g} (via {@code ceilingKey}).</li>
     *       <li>If {@code cap} exists but its quantity is 0, move to the next larger capacity.</li>
     *       <li>If no capacity exists, allocation fails.</li>
     *       <li>Otherwise decrement {@code available[cap]} by 1 to reserve a table.</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param total immutable view of total inventory (capacity -> quantity)
     * @param collide list of active/colliding orders at a specific time point
     * @param extraGuests requested additional group size
     * @return {@code true} if all groups can be allocated; {@code false} otherwise
     */
    private boolean canFitExtraGroup(TreeMap<Integer, Integer> total, List<Order> collide, int extraGuests) {
        TreeMap<Integer, Integer> available = new TreeMap<>(total);

        List<Integer> groups = new ArrayList<>();
        for (Order o : collide) groups.add(o.getNumberOfGuests());
        groups.add(extraGuests);

        groups.sort(Comparator.reverseOrder());

        for (int g : groups) {
            Integer cap = available.ceilingKey(g);

            // Skip capacities with no remaining tables.
            while (cap != null && available.get(cap) == 0) cap = available.higherKey(cap);

            if (cap == null) return false;

            available.put(cap, available.get(cap) - 1);
        }
        return true;
    }
}