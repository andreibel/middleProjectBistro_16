package com.andreibel.server.controller;

import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.WorkerAuth;
import com.andreibel.message.DTO.WorkerNewRequest;
import com.andreibel.message.DTO.WorkerResponse;
import com.andreibel.message.Message;
import com.andreibel.server.services.WorkerService;

public class WorkerController {
    private final WorkerService workerService;
    private static WorkerController instance;

    private WorkerController() {
        workerService = WorkerService.getInstance();
    }

    public static WorkerController getInstance() {
        if (instance == null) {
            instance = new WorkerController();
        }
        return instance;
    }


    public Message login(Message message) {
        WorkerResponse response = workerService.authWorker((WorkerAuth) message.getData());
        if (response != null) return new Message(APICallType.LOGIN_WORKER_RESPONSE, response);
        return new Message(APICallType.ERROR, null);
    }


    public Message createWorker(Message message) {
        return new Message(
                APICallType.WORKER_CREATE_RESPONSE,
                workerService.createWorker((WorkerNewRequest) message.getData())
        );
    }
}
