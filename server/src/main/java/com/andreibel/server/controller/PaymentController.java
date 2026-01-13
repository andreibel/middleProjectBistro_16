package com.andreibel.server.controller;

import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
import com.andreibel.server.services.OrderService;
import com.andreibel.server.services.WaitingListService;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.andreibel.message.APICallType.COMPLETE_ORDER_ERROR;
import static com.andreibel.message.APICallType.COMPLETE_ORDER_RESPONSE;
import static com.andreibel.server.utils.TUI.printPrice;

public class PaymentController {
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
     * <p><b>Response:</b> always returns {@code COMPLETE_ORDER_RESPONSE}. If you want to report failure,
     * update this method to check service result and return {@code COMPLETE_ORDER_ERROR} accordingly.</p>
     *
     * @param message request message containing a {@link UUID}
     * @return completion response message
     */
    public Message payByConformationCode(Message message) {
        OrderResponse closedOrder = orderService.completeOrder((UUID) message.getData());
        WaitingListResponse closedWaiting = waitingListService.completeWaiting((UUID) message.getData());
        if (closedOrder == null && closedWaiting == null) return new Message(COMPLETE_ORDER_ERROR, null);
        int numberOfGuests = 0;
        boolean isSub = false;
        UUID conformationCode;
        if (closedOrder != null) {
            if(closedOrder.getConformationCode() == null) return new Message(COMPLETE_ORDER_ERROR, "can't close order" +
                    " that not arrived or candled or was closed before");
            numberOfGuests = closedOrder.getNumberOfGuests();
            isSub = closedOrder.getSubscriberId() != null;
            conformationCode = closedOrder.getConformationCode();
        } else {
            if(closedWaiting.getConformationCode() == null) return new Message(COMPLETE_ORDER_ERROR, "can't close order" +
                    " that not arrived or candled or was closed before");
            numberOfGuests = closedWaiting.getNumberOfGuests();
            isSub = closedWaiting.getSubscriberId() != null;
            conformationCode = closedWaiting.getConformationCode();
        }


        double price = numberOfGuests * 100 * (isSub? 0.9 : 1);
        printPrice(conformationCode, numberOfGuests, isSub);
        return new Message(COMPLETE_ORDER_RESPONSE, "the order is closed. pay this price: $" + price);
    }
}
