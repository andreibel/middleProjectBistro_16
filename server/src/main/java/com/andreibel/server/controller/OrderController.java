package com.andreibel.server.controller;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.DTO.TimeGetterRequest;
import com.andreibel.message.Message;
import com.andreibel.server.services.OrderService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.andreibel.message.APICallType.*;
import static com.andreibel.server.utils.TUI.UUID_sent;
import static com.andreibel.server.utils.TUI.printPrice;

/**
 * Handles order-related API calls coming from the network layer.
 *
 * <p>This controller is a thin routing/adaptation layer between incoming {@link Message} objects
 * and the {@link OrderService} business logic. Each handler method:</p>
 * <ul>
 *   <li>extracts and validates the expected payload type from {@link Message#getData()}</li>
 *   <li>delegates the operation to {@link OrderService}</li>
 *   <li>wraps the result into a response {@link Message} with the appropriate API call type</li>
 * </ul>
 *
 * <p><b>Payload contract:</b> each method expects a specific payload type in {@code message.getData()}
 * (e.g., {@link OrderRequest}, {@link UUID}, {@link TimeGetterRequest}). Sending a different type may
 * cause {@link ClassCastException}. If you want safer behavior, add runtime type checks and return
 * an error {@link Message} for invalid payloads.</p>
 *
 * <p><b>Singleton:</b> implemented as a singleton to keep one controller instance and one shared
 * {@link OrderService} instance during the server runtime.</p>
 *
 * @author Andrei Beloziyorove
 */
public class OrderController {

    private static OrderController instance;
    private final OrderService orderService;

    /**
     * Creates the controller and initializes the service dependency.
     * Private to enforce the singleton pattern.
     */
    private OrderController() {
        orderService = OrderService.getInstance();
    }

    /**
     * Returns the singleton instance of {@link OrderController}.
     *
     * @return singleton controller instance
     */
    public static OrderController getInstance() {
        if (instance == null) instance = new OrderController();
        return instance;
    }

    /**
     * Creates a new order.
     *
     * <p><b>Expected payload:</b> {@link OrderRequest}. The relevant fields are typically:</p>
     * <ul>
     *   <li>{@code numberOfGuests}</li>
     *   <li>{@code orderDateTime}</li>
     *   <li>{@code subscriberId} (optional)</li>
     *   <li>{@code email} (optional)</li>
     *   <li>{@code phoneNumber} (optional)</li>
     * </ul>
     *
     * <p><b>Response:</b> {@code CREATE_ORDER_RESPONSE} with {@link OrderResponse} on success,
     * or {@code CREATE_ORDER_ERROR} if creation failed.</p>
     *
     * @param message request message containing an {@link OrderRequest}
     * @return response message with the created order or an error type
     */
    public Message createOrder(Message message) {
        OrderResponse newOrder = orderService.createOrder((OrderRequest) message.getData());
        if (newOrder == null) return new Message(CREATE_ORDER_ERROR, null);
        return new Message(CREATE_ORDER_RESPONSE, newOrder);
    }

    /**
     * Retrieves an order by confirmation code.
     *
     * <p><b>Expected payload:</b> {@link UUID} (confirmation code).</p>
     *
     * <p><b>Response:</b> {@code GET_ONE_ORDER_RESPONSE} with {@link OrderResponse} on success,
     * or {@code GET_ONE_ORDER_ERROR} if not found.</p>
     *
     * @param message request message containing a {@link UUID}
     * @return response message containing the requested order or an error type
     */
    public Message getOrder(Message message) {
        OrderResponse orderResponse = orderService.getOrderByConformationCode((UUID) message.getData());
        if (orderResponse == null) return new Message(GET_ONE_ORDER_ERROR, null);
        return new Message(GET_ONE_ORDER_RESPONSE, orderResponse);
    }

    /**
     * Cancels (soft-deletes) an order by confirmation code.
     *
     * <p><b>Expected payload:</b> {@link UUID} (confirmation code).</p>
     *
     * <p><b>Response:</b> {@code DELETE_ORDER_RESPONSE} with {@link OrderResponse} on success,
     * or {@code DELETE_ORDER_ERROR} if cancellation failed or the order was not found.</p>
     *
     * @param message request message containing a {@link UUID}
     * @return response message containing the cancelled order or an error type
     */
    public Message deleteOrder(Message message) {
        OrderResponse orderResponse = orderService.deleteOrder((UUID) message.getData());
        if (orderResponse == null) return new Message(DELETE_ORDER_ERROR, null);
        return new Message(DELETE_ORDER_RESPONSE, orderResponse);
    }

    /**
     * Marks an order as arrived (check-in) by confirmation code.
     *
     * <p><b>Expected payload:</b> {@link UUID} (confirmation code).</p>
     *
     * <p><b>Response:</b> {@code ORDER_ARRIVED_RESPONSE} with {@link OrderResponse} on success,
     * or {@code ORDER_ARRIVED_ERROR} if the operation failed.</p>
     *
     * @param message request message containing a {@link UUID}
     * @return response message containing the updated order or an error type
     */
    public Message updateArrives(Message message) {
        OrderResponse orderResponse = orderService.orderArrives((UUID) message.getData());
        if (orderResponse == null) return new Message(ORDER_ARRIVED_ERROR, null);
        if (orderResponse.getConformationCode() == null)
            return new Message(ORDER_ARRIVED_ERROR, "can't give you table because all of them are Occupied!!");
        return new Message(ORDER_ARRIVED_RESPONSE, orderResponse);
    }



    /**
     * Retrieves all available reservation start times for a specific date and party size.
     *
     * <p><b>Expected payload:</b> {@link TimeGetterRequest} containing:</p>
     * <ul>
     *   <li>{@code date} - the requested date</li>
     *   <li>{@code capacity} - number of guests (party size)</li>
     * </ul>
     *
     * <p><b>Response:</b> {@code GET_ALL_TIMES_IN_DATE_RESPONSE} with {@code List<LocalTime>} on success,
     * or {@code GET_ALL_TIMES_IN_DATE_ERROR} if there are no available times.</p>
     *
     * @param message request message containing {@link TimeGetterRequest}
     * @return response message containing the available times or an error type
     */
    public Message getAllAvailableTime(Message message) {
        TimeGetterRequest data = (TimeGetterRequest) message.getData();
        List<LocalTime> allAvailableTimeInDate = orderService.getAllAvailableTimeInDate(
                data.getDate(),
                data.getCapacity()
        );
        if (allAvailableTimeInDate == null || allAvailableTimeInDate.isEmpty()) {
            return new Message(GET_ALL_TIMES_IN_DATE_ERROR, null);
        }
        return new Message(GET_ALL_TIMES_IN_DATE_RESPONSE, allAvailableTimeInDate);
    }

    /**
     * Handles the "lost confirmation code" flow.
     *
     * <p>This method supports two different payload modes:</p>
     * <ul>
     *   <li>If {@code message.getData()} is a {@link String}, the value is forwarded to
     *       {@link com.andreibel.server.utils.TUI#UUID_sent(String)} and the method returns {@code null}.
     *       (This looks like a debug/CLI hook.)</li>
     *   <li>Otherwise, it expects an {@link OrderRequest} and returns matching orders via
     *       {@link OrderService#lostConformCode(OrderRequest)}.</li>
     * </ul>
     *
     * <p><b>Response:</b> {@code ORDER_LOST_CONFORMATION_CODE_RESPONSE} with a {@code List<OrderResponse>}
     * when matches exist, or {@code ORDER_LOST_CONFORMATION_CODE_ERROR} when none found.</p>
     *
     * @param message request message containing either {@link String} or {@link OrderRequest}
     * @return response message with matching orders, an error message, or {@code null} (string debug mode)
     */
    public Message lostCode(Message message) {
        if (message.getData() instanceof String) {
            UUID_sent((String) message.getData());
            return null;
        }

        List<OrderResponse> data = orderService.lostConformCode((OrderRequest) message.getData());
        if (data == null) return new Message(ORDER_LOST_CONFORMATION_CODE_ERROR, null);
        return new Message(ORDER_LOST_CONFORMATION_CODE_RESPONSE, data);
    }

    /**
     * Retrieves all active orders (typically: not cancelled and not completed).
     *
     * <p><b>Response:</b> {@code GET_ALL_ACTIVE_RESPONSE} with {@code List<OrderResponse>} on success,
     * or {@code GET_ALL_ACTIVE_ERROR} if the service returns {@code null}.</p>
     *
     * @return response message containing active orders or an error type
     */
    public Message getAllActiveOrder() {
        List<OrderResponse> activeOrders = orderService.getAllActiveOrders();
        if (activeOrders == null) return new Message(GET_ALL_ACTIVE_ERROR, null);
        return new Message(GET_ALL_ACTIVE_RESPONSE, activeOrders);
    }

    /**
     * Retrieves orders for customers currently eating.
     *
     * <p>Typically represents orders that are arrived but not completed (and not cancelled),
     * depending on the {@link OrderService#getCurrentEat()} implementation.</p>
     *
     * <p><b>Response:</b> {@code GET_ALL_ARRIVED_AND_NOT_COMPLETE_RESPONSE} with {@code List<OrderResponse>}
     * on success, or {@code GET_ALL_ARRIVED_AND_NOT_COMPLETE_ERROR} if the service returns {@code null}.</p>
     *
     * @return response message containing current eating orders or an error type
     */
    public Message getNowEating() {
        List<OrderResponse> activeEat = orderService.getCurrentEat();
        if (activeEat == null) return new Message(GET_ALL_ARRIVED_AND_NOT_COMPLETE_ERROR, null);
        return new Message(GET_ALL_ARRIVED_AND_NOT_COMPLETE_RESPONSE, activeEat);
    }
}