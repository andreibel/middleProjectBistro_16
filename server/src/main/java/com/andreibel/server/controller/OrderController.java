package com.andreibel.server.controller;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.Message;
import com.andreibel.server.services.OrderService;

import static com.andreibel.message.APICallType.*;

/**
 * Controller responsible for handling order-related requests.
 *
 * <p>
 * This class acts as the entry point between incoming {@link Message} objects
 * and the {@link OrderService}. It converts request messages into service calls
 * and wraps service responses back into response messages.
 * </p>
 *
 * <p>
 * Implemented as a Singleton to ensure a single controller instance
 * throughout the application lifecycle.
 * </p>
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
     * Returns the single instance of {@code OrderController}.
     *
     * @return singleton instance of OrderController
     */
    public static OrderController getInstance() {
        if (instance == null) {
            instance = new OrderController();
        }
        return instance;
    }

    /**
     * Creates a new order.
     *
     * @param message request message containing {@link OrderRequest} data
     * @return response message containing the created order
     */
    public Message createOrder(Message message) {
        return new Message(
                CREATE_ORDER_RESPONSE,
                orderService.createOrder(
                        (OrderRequest) message.getData()
                )
        );
    }

    /**
     * Retrieves a single order by its confirmation code.
     *
     * @param message request message containing {@link OrderRequest}
     *                with the confirmation code
     * @return response message containing the requested order
     */
    public Message getOrder(Message message) {
        return new Message(
                GET_ONE_ORDER_RESPONSE,
                orderService.getOrderByConformationCode(
                        (OrderRequest) message.getData()
                )
        );
    }

    /**
     * Retrieves all orders in the system.
     *
     * @return response message containing a list of all orders
     */
    public Message getAllOrders() {
        return new Message(
                GET_ALL_ORDERS_RESPONSE,
                orderService.getAllOrders()
        );
    }

    /**
     * Deletes an order by its confirmation code.
     *
     * @param message request message containing {@link OrderRequest}
     *                with the confirmation code
     * @return response message indicating successful deletion
     */
    public Message deleteOrder(Message message) {
        return new Message(
                DELETE_ORDER_RESPONSE,
                orderService.deleteOrder(
                        ((OrderRequest) message.getData()).getConformationCode()
                )
        );
    }

    public Message updateArrives(Message message) {
        return new Message(
                ORDER_ARRIVED_RESPONSE,
                orderService.orderArrives(
                        ((OrderRequest)message.getData()).getConformationCode()
                )
        );
    }

    public Message closeOrder(Message message) {
        return new Message(
                COMPLETE_ORDER_RESPONSE,
                orderService.completeOrder(
                        ((OrderRequest)message.getData()).getConformationCode()
                )
        );
    }
}