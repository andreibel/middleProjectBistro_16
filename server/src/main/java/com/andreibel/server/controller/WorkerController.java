package com.andreibel.server.controller;

import com.andreibel.message.DTO.*;
import com.andreibel.message.Message;
import com.andreibel.server.services.OpenTimeService;
import com.andreibel.server.services.TableService;
import com.andreibel.server.services.WorkerService;

import java.util.List;

import static com.andreibel.message.APICallType.*;

public class WorkerController {
    private final WorkerService workerService;
    private static WorkerController instance;
    private final OpenTimeService openTimeService;
    private final TableService tableService;

    private WorkerController() {
        workerService = WorkerService.getInstance();
        openTimeService = OpenTimeService.getInstance();
        tableService = TableService.getInstance();

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


    public Message addSpecialDay(Message message) {
        openTimeService.addSpecialDay((SpecialDayRequest)message.getData());
        return new Message(ADD_SPECIAL_DAY_RESPONSE, null);
    }

    public Message editRegulaDay(Message message) {
        openTimeService.editRegulaDay((BistroTimeRequest)message.getData());
        return new Message(CHANGE_BISTRO_TIME_RESPONSE, null);
    }

    public Message getAllTables() {
        List<TableResponse> tables = tableService.getAllTables();
        if (tables == null || tables.isEmpty()) return new Message(GET_ALL_TABLES_ERROR, null);
        return new Message(GET_ALL_TABLES_RESPONSE, tables);
    }

    public Message updateTables(Message message) {
        if (!(message.getData() instanceof List)) return new Message(EDIT_BISTRO_LAYOUT_ERROR, null);
        tableService.editTables((List<TableRequest>)message.getData());
        return new Message(EDIT_BISTRO_LAYOUT_RESPONSE, null);
    }
}
