package com.andreibel.server.services;

import com.andreibel.message.DTO.BistroTimeRequest;
import com.andreibel.message.DTO.SpecialDayRequest;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OpenTimeRepository;

import java.sql.SQLException;

public class OpenTimeService {
    private static OpenTimeService instance;
    private final OpenTimeRepository openTimeRepository;
    private final TransactionManager tx;
    private OpenTimeService() {
        openTimeRepository = OpenTimeRepository.getInstance();
        tx = TransactionManager.getInstance();
    }
    public static OpenTimeService getInstance() {
        if (instance == null) {
            instance = new OpenTimeService();
        }
        return instance;
    }


    public void addSpecialDay(SpecialDayRequest data)  {
        tx.inTransaction(() -> {
            openTimeRepository.addNewSpecial(
                    data.getDate(),
                    data.getStartTime(),
                    data.getEndTime(),
                    data.getInterval()
            );
            return null;
        });
    }

    public void editRegulaDay(BistroTimeRequest data) {
        tx.inTransaction(() -> {
            openTimeRepository.updateRegular(
                    data.getStartTime(),
                    data.getEndTime(),
                    data.getInterval()
            );
            return null;
        });

    }
}
