package com.andreibel.server.controller;

import com.andreibel.message.DTO.*;
import com.andreibel.message.Message;
import com.andreibel.server.services.OpenTimeService;
import com.andreibel.server.services.TableService;
import com.andreibel.server.services.WorkerService;

import java.util.List;

import static com.andreibel.message.APICallType.*;

/**
 * Handles worker/staff-facing API calls coming from the networking layer.
 *
 * <p>This controller is a thin routing layer between incoming {@link Message} objects
 * and the worker-related service classes:</p>
 * <ul>
 *   <li>{@link WorkerService} - authentication and worker account creation</li>
 *   <li>{@link OpenTimeService} - restaurant opening hours (regular + special days)</li>
 *   <li>{@link TableService} - table layout (capacities/quantities) management</li>
 * </ul>
 *
 * <p>Each method extracts and casts the request payload from {@link Message#getData()},
 * delegates to the relevant service, and wraps the result into a response {@link Message}
 * using the appropriate {@code APICallType} value.</p>
 *
 * <p><b>Payload contracts</b> (what {@code message.getData()} must contain):</p>
 * <ul>
 *   <li>{@link #login(Message)}: {@link WorkerAuth}</li>
 *   <li>{@link #createWorker(Message)}: {@link WorkerNewRequest}</li>
 *   <li>{@link #addSpecialDay(Message)}: {@link SpecialDayRequest}</li>
 *   <li>{@link #editRegulaDay(Message)}: {@link BistroTimeDTO}</li>
 *   <li>{@link #updateTables(Message)}: {@code List<TableRequest>}</li>
 * </ul>
 *
 * <p><b>Singleton:</b> implemented as a singleton so the server uses one controller instance.</p>
 *
 * @author Andrei Beloziyorove
 */
public class WorkerController {

    private static WorkerController instance;

    /** Service responsible for worker authentication and worker CRUD operations. */
    private final WorkerService workerService;

    /** Service responsible for regular/special opening hours. */
    private final OpenTimeService openTimeService;

    /** Service responsible for restaurant tables layout management. */
    private final TableService tableService;

    /**
     * Creates the controller and initializes dependencies.
     * Private to enforce singleton usage via {@link #getInstance()}.
     */
    private WorkerController() {
        workerService = WorkerService.getInstance();
        openTimeService = OpenTimeService.getInstance();
        tableService = TableService.getInstance();
    }

    /**
     * Returns the singleton instance of {@link WorkerController}.
     *
     * @return singleton controller instance
     */
    public static WorkerController getInstance() {
        if (instance == null) {
            instance = new WorkerController();
        }
        return instance;
    }

    /**
     * Authenticates a worker (staff login).
     *
     * <p><b>Expected payload:</b> {@link WorkerAuth} (e.g., username/id + password).</p>
     *
     * <p><b>Response:</b></p>
     * <ul>
     *   <li>{@code WORKER_LOGIN_RESPONSE} with {@link WorkerResponse} on success</li>
     *   <li>{@code WORKER_LOGIN_ERROR} with {@code null} payload on failure</li>
     * </ul>
     *
     * @param message request message containing {@link WorkerAuth}
     * @return response message with authentication result
     */
    public Message login(Message message) {
        WorkerResponse response = workerService.authWorker((WorkerAuth) message.getData());
        if (response == null) return new Message(WORKER_LOGIN_ERROR, null);
        if (response.getWorkerName() == null) return new Message(WORKER_LOGIN_ERROR, "Staff's name or password is " +
                "incorrect.");
        return new Message(WORKER_LOGIN_RESPONSE, response);
    }

    /**
     * Creates a new worker account.
     *
     * <p><b>Expected payload:</b> {@link WorkerNewRequest} describing the new worker.</p>
     *
     * <p><b>Response:</b></p>
     * <ul>
     *   <li>{@code WORKER_CREATE_RESPONSE} with {@link WorkerResponse} on success</li>
     *   <li>{@code WORKER_CREATE_ERROR} with {@code null} payload on failure</li>
     * </ul>
     *
     * @param message request message containing {@link WorkerNewRequest}
     * @return response message with created worker data
     */
    public Message createWorker(Message message) {
        WorkerResponse newWorker = workerService.createWorker((WorkerNewRequest) message.getData());
        if (newWorker == null) return new Message(WORKER_CREATE_ERROR, null);
        return new Message(WORKER_CREATE_RESPONSE, newWorker);
    }

    /**
     * Adds a "special day" opening-hours record (date-specific override).
     *
     * <p><b>Expected payload:</b> {@link SpecialDayRequest} containing the date and opening-hours details.</p>
     *
     * <p><b>Response:</b> returns {@code ADD_SPECIAL_DAY_RESPONSE} with {@code null} payload.
     * If you want error reporting, consider catching service exceptions and returning
     * {@code ADD_SPECIAL_DAY_ERROR}.</p>
     *
     * @param message request message containing {@link SpecialDayRequest}
     * @return response message acknowledging the operation
     */
    public Message addSpecialDay(Message message) {
        openTimeService.addSpecialDay((SpecialDayRequest) message.getData());
        return new Message(ADD_SPECIAL_DAY_RESPONSE, "Added");
    }

    /**
     * Updates the regular (default) restaurant opening hours.
     *
     * <p><b>Expected payload:</b> {@link BistroTimeDTO} describing regular open/close times and interval.</p>
     *
     * <p><b>Response:</b> returns {@code CHANGE_BISTRO_TIME_RESPONSE} with {@code null} payload.</p>
     *
     * @param message request message containing {@link BistroTimeDTO}
     * @return response message acknowledging the operation
     */
    public Message editRegulaDay(Message message) {
        openTimeService.editRegulaDay((BistroTimeDTO) message.getData());
        return new Message(CHANGE_BISTRO_TIME_RESPONSE, "Edited");
    }

    /**
     * Retrieves the current table layout configuration.
     *
     * <p><b>Response:</b></p>
     * <ul>
     *   <li>{@code GET_ALL_TABLES_RESPONSE} with {@code List<TableResponse>} when data exists</li>
     *   <li>{@code GET_ALL_TABLES_ERROR} with {@code null} payload if no tables are found</li>
     * </ul>
     *
     * @return response message containing table layout data or an error type
     */
    public Message getAllTables() {
        List<TableResponse> tables = tableService.getAllTables();
        if (tables == null || tables.isEmpty()) return new Message(GET_ALL_TABLES_ERROR, null);
        return new Message(GET_ALL_TABLES_RESPONSE, tables);
    }

    /**
     * Updates the table layout configuration.
     *
     * <p><b>Expected payload:</b> {@code List<TableRequest>} describing the desired layout.</p>
     *
     * <p>This method validates that {@link Message#getData()} is a {@link List} before casting.
     * If the payload is not a list, {@code EDIT_BISTRO_LAYOUT_ERROR} is returned.</p>
     *
     * <p><b>Response:</b> returns {@code EDIT_BISTRO_LAYOUT_RESPONSE} with {@code null} payload on success.</p>
     *
     * @param message request message containing {@code List<TableRequest>}
     * @return response message acknowledging the update or an error type
     */
    @SuppressWarnings("unchecked")
    public Message updateTables(Message message) {
        if (!(message.getData() instanceof List)) return new Message(EDIT_BISTRO_LAYOUT_ERROR, null);
        tableService.editTables((List<TableRequest>) message.getData());
        return new Message(EDIT_BISTRO_LAYOUT_RESPONSE, "successfully edited");
    }

    /**
     * Retrieves the regular (default) opening-hours configuration.
     *
     * <p><b>Response:</b> {@code GET_REGULAR_OPEN_TIME_RESPONSE} with {@link BistroTimeDTO} payload.
     * If you want an explicit error when missing, add a {@code null} check and return
     * {@code GET_REGULAR_OPEN_TIME_ERROR}.</p>
     *
     * @return response message containing the regular opening-hours configuration
     */
    public Message getRegularDate() {
        BistroTimeDTO regular = openTimeService.getRegular();
        return new Message(GET_REGULAR_OPEN_TIME_RESPONSE, regular);
    }
}