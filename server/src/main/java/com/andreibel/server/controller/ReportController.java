package com.andreibel.server.controller;

import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.SchedulesReportResponse;
import com.andreibel.message.DTO.SubscriberReportResponse;
import com.andreibel.message.Message;
import com.andreibel.server.services.ReportService;

public class ReportController {

    private static ReportController instance;
    private final ReportService reportService;


    private ReportController() {
        reportService = ReportService.getInstance();
    }

    public static ReportController getInstance() {
        if (instance == null) {
            instance = new ReportController();
        }
        return instance;
    }


    /**
     * Handles a request for the schedules report.
     *
     * <p>This method retrieves aggregated scheduling data from the service
     * layer and returns it to the client.</p>
     *
     * @param message the incoming request message (data not used)
     * @return a {@link Message} containing either
     * {@link APICallType#SCHEDULES_REPORT_RESPONSE} with report data
     * or {@link APICallType#SCHEDULES_REPORT_ERROR} if retrieval fails
     */
    public Message scheduleReport(Message message) {
        SchedulesReportResponse schedulesReportResponse =
                reportService.getSchedulesReport();

        if (schedulesReportResponse == null) {
            return new Message(APICallType.SCHEDULES_REPORT_ERROR, null);
        }

        return new Message(
                APICallType.SCHEDULES_REPORT_RESPONSE,
                schedulesReportResponse
        );
    }

    /**
     * Handles a request for the subscriber report.
     *
     * <p>This report provides aggregated data about restaurant subscribers.</p>
     *
     * @param message the incoming request message (data not used)
     * @return a {@link Message} containing either
     * {@link APICallType#SUBSCRIBER_REPORT_RESPONSE} with report data
     * or {@link APICallType#SUBSCRIBER_REPORT_ERROR} if retrieval fails
     */
    public Message subscriberReport(Message message) {
        SubscriberReportResponse subscriberReportResponse =
                reportService.getSubscriberReport();

        if (subscriberReportResponse == null) {
            return new Message(APICallType.SUBSCRIBER_REPORT_ERROR, null);
        }

        return new Message(
                APICallType.SUBSCRIBER_REPORT_RESPONSE,
                subscriberReportResponse
        );
    }
}
