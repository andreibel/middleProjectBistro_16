package com.andreibel.server.controller;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.DTO.TimeGetterRequest;
import com.andreibel.message.Message;
import com.andreibel.server.services.OrderService;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.andreibel.message.APICallType.*;
import static com.andreibel.server.utils.TUI.UUID_sent;

/**
 * Controller responsible for handling order-related API requests.
 *
 * <p>
 * Acts as a thin routing layer between incoming {@link Message} objects
 * and the {@link OrderService}. Each method extracts request data,
 * delegates the business logic to the service layer, and wraps the result
 * in a response {@link Message}.
 * </p>
 *
 * <p>
 * Implemented as a Singleton to ensure a single controller instance.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class OrderController {

    private static OrderController instance;
    private final OrderService orderService;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private OrderController() {
        orderService = OrderService.getInstance();
    }

    /**
     * @return singleton instance of {@link OrderController}
     */
    public static OrderController getInstance() {
        if (instance == null) instance = new OrderController();
        return instance;
    }

    /**
     * Handles a request to create a new order.
     * <p>the relevant fields in the {@link OrderRequest} object are:
     * <li>{@code numberOfGuests}</li>
     * <li>{@code orderDateTime}</li>
     * <li>{@code subscriberId Optional}</li>
     * <li>{@code email Optional}</li>
     * <li>{@code phoneNumber Optional}</li>
     * all other fields are null or ignored
     * </p>
     *
     * @param message request message containing {@link OrderRequest}
     * @return response message containing the created order
     */
    public Message createOrder(Message message) {
        OrderResponse newOrder = orderService.createOrder((OrderRequest) message.getData());
        if (newOrder == null) return new Message(CREATE_ORDER_ERROR, null);
        return new Message(CREATE_ORDER_RESPONSE, newOrder);
    }

    /**
     * Handles a request to retrieve a single order by confirmation code.
     * <p>the relevant fields in the {@link OrderRequest} object are:
     * <li>{@code conformationCode}</li>
     * all other fields are null or ignored
     * </p>
     *
     * @param message request message containing {@link OrderRequest}
     * @return response message containing the requested order
     */
    public Message getOrder(Message message) {
        OrderResponse orderResponse = orderService.getOrderByConformationCode((UUID) message.getData());
        if (orderResponse == null) return new Message(GET_ONE_ORDER_ERROR, null);
        return new Message(GET_ONE_ORDER_RESPONSE, orderResponse);
    }


    /**
     * Handles a request to cancel (delete) an order by confirmation code.
     * <p>the relevant fields in the {@link OrderRequest} object are:
     * <li>{@code conformationCode}</li>
     * all other fields are null or ignored
     * </p>
     *
     * @param message request message containing {@link OrderRequest}
     * @return response message containing the cancelled order
     */
    public Message deleteOrder(Message message) {
        OrderResponse orderResponse = orderService.deleteOrder((UUID)message.getData());
        if (orderResponse == null) return new Message(DELETE_ORDER_ERROR, null);
        return new Message(DELETE_ORDER_RESPONSE, orderResponse);
    }

    /**
     * Handles a request to mark an order as arrived (check-in).
     * <p>the relevant fields in the {@link OrderRequest} object are:
     * <li>{@code conformationCode}</li>
     * all other fields are null or ignored
     * </p>
     *
     * @param message request message containing {@link OrderRequest}
     * @return response message containing the updated order
     */
    public Message updateArrives(Message message) {
        OrderResponse orderResponse = orderService.orderArrives((UUID)message.getData());
        if (orderResponse == null) return new Message(ORDER_ARRIVED_ERROR, null);
        return new Message(ORDER_ARRIVED_RESPONSE, orderResponse);
    }

    /**
     * Handles a request to complete (close) an order.
     * <p>the relevant fields in the {@link OrderRequest} object are:
     * <li>{@code conformationCode}</li>
     * all other fields are null or ignored
     * </p>
     *
     * @param message request message containing {@link OrderRequest}
     * @return response message containing the updated order
     */
    public Message closeOrder(Message message) {
        orderService.completeOrder((UUID) message.getData());
        return new Message(
                COMPLETE_ORDER_RESPONSE,
                null
        );
    }

    /**
     * Handles a request to retrieve all available reservation start times
     * for a given date and number of guests.
     * <p>the relevant fields in the {@link OrderRequest} object are:
     * <li>{@code localDateTime} ant start of the day</li>
     * <li>{@code numberOfGuests}</li>
     * all other fields are null or ignored
     * </p>
     *
     * @param message request message containing {@link OrderRequest}
     * @return response message containing a list of available times
     */
    public Message getAllAvailableTime(Message message) {
        TimeGetterRequest data = (TimeGetterRequest) message.getData();
        List<LocalTime> allAvailableTimeInDate = orderService.getAllAvailableTimeInDate(
                data.getDate(),
                data.getCapacity()
        );
        if (allAvailableTimeInDate == null || allAvailableTimeInDate.isEmpty())
            return new Message(GET_ALL_TIMES_IN_DATE_ERROR, null);
        return new Message(GET_ALL_TIMES_IN_DATE_RESPONSE, allAvailableTimeInDate);
    }

    public Message lostCode(Message message) {
        if (message.getData() instanceof String){
            UUID_sent((String)message.getData());
            return null;
        }

        List<OrderResponse> data = orderService.lostConformCode(((OrderRequest) message.getData()));
        if (data == null || data.isEmpty()) return new Message(ORDER_LOST_CONFORMATION_CODE_ERROR, null);
        return new Message(ORDER_LOST_CONFORMATION_CODE_RESPONSE, data);
    }

    public Message getAllActiveOrder() {
        List<OrderResponse> activeOrders = orderService.getAllActiveOrders();
        if (activeOrders == null) return new Message(GET_ALL_ACTIVE_ERROR, null);
        return new Message(GET_ALL_ACTIVE_RESPONSE, activeOrders);
    }

    public Message getNowEating() {
        List<OrderResponse> activeEat = orderService.getCurrentEat();
        if (activeEat == null)  return new Message(GET_ALL_ARRIVED_AND_NOT_COMPLETE_ERROR, null);
        return new Message(GET_ALL_ARRIVED_AND_NOT_COMPLETE_RESPONSE, activeEat);
    }
}