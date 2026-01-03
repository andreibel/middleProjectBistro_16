package com.andreibel.server.controller;

import com.andreibel.message.DTO.WorkerAuth;
import com.andreibel.message.DTO.WorkerNewRequest;
import com.andreibel.message.DTO.WorkerResponse;
import com.andreibel.message.Message;
import com.andreibel.server.services.WorkerService;

import static com.andreibel.message.APICallType.*;

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
        if (response == null) return new Message(WORKER_LOGIN_ERROR, null);
        return new Message(WORKER_LOGIN_RESPONSE, response);
    }


    public Message createWorker(Message message) {
        WorkerResponse newWorker = workerService.createWorker((WorkerNewRequest) message.getData());
        if (newWorker == null) return new Message(WORKER_CREATE_ERROR, null);
        return new Message(WORKER_CREATE_RESPONSE, newWorker);
    }
}
