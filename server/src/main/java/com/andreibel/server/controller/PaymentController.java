package com.andreibel.server.controller;

import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
import com.andreibel.server.services.OrderService;
import com.andreibel.server.services.WaitingListService;

import java.util.UUID;

import static com.andreibel.message.APICallType.COMPLETE_ORDER_ERROR;
import static com.andreibel.message.APICallType.COMPLETE_ORDER_RESPONSE;
import static com.andreibel.server.utils.TUI.printPrice;

public class PaymentController {
    private static final double PRICE_PER_GUEST = 100.0;
    private static final double SUBSCRIBER_DISCOUNT = 0.9;
    private static final String INVALID_ORDER_ERROR = "can't close order that not arrived or cancelled or was closed before";

    private static PaymentController instance;
    private final OrderService orderService;
    private final WaitingListService waitingListService;

    private PaymentController() {
        orderService = OrderService.getInstance();
        waitingListService = WaitingListService.getInstance();
    }

    public static PaymentController getInstance() {
        if (instance == null) instance = new PaymentController();
        return instance;
    }

    /**
     * Completes (closes) an order by confirmation code.
     *
     * <p><b>Expected payload:</b> {@link UUID} (confirmation code).</p>
     *
     * <p><b>Response:</b> returns {@code COMPLETE_ORDER_RESPONSE} on success,
     * or {@code COMPLETE_ORDER_ERROR} if the order cannot be closed.</p>
     *
     * @param message request message containing a {@link UUID}
     * @return completion response message
     */
    public Message payByConformationCode(Message message) {
        UUID confirmationCode = (UUID) message.getData();
        OrderResponse closedOrder = orderService.completeOrder(confirmationCode);
        WaitingListResponse closedWaiting = waitingListService.completeWaiting(confirmationCode);

        PaymentDetails details = extractPaymentDetails(closedOrder, closedWaiting);
        if (details == null) {
            return new Message(COMPLETE_ORDER_ERROR, INVALID_ORDER_ERROR);
        }

        double price = calculatePrice(details.numberOfGuests, details.isSubscriber);
        printPrice(details.confirmationCode, details.numberOfGuests, details.isSubscriber);
        return new Message(COMPLETE_ORDER_RESPONSE, "the order is closed. pay this price: $" + price);
    }

    private PaymentDetails extractPaymentDetails(OrderResponse order, WaitingListResponse waiting) {
        if (order != null && order.getConformationCode() != null) {
            return new PaymentDetails(
                    order.getConformationCode(),
                    order.getNumberOfGuests(),
                    order.getSubscriberId() != null
            );
        }
        if (waiting != null && waiting.getConformationCode() != null) {
            return new PaymentDetails(
                    waiting.getConformationCode(),
                    waiting.getNumberOfGuests(),
                    waiting.getSubscriberId() != null
            );
        }
        return null;
    }

    private double calculatePrice(int numberOfGuests, boolean isSubscriber) {
        double basePrice = numberOfGuests * PRICE_PER_GUEST;
        return isSubscriber ? basePrice * SUBSCRIBER_DISCOUNT : basePrice;
    }

    private record PaymentDetails(UUID confirmationCode, int numberOfGuests, boolean isSubscriber) {}
}
