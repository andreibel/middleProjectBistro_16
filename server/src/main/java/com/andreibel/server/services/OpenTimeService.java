package com.andreibel.server.services;

import com.andreibel.message.DTO.BistroTimeDTO;
import com.andreibel.message.DTO.SpecialDayRequest;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OpenTimeRepository;
import com.andreibel.server.entity.OpenTime;
import com.andreibel.server.utils.OpenTimeMapper;

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
                    data.getTitle(),
                    data.getStartTime(),
                    data.getEndTime(),
                    data.getInterval()
            );
            return null;
        });
    }

    public void editRegulaDay(BistroTimeDTO data) {
        tx.inTransaction(() -> {
            openTimeRepository.updateRegular(
                    data.getStartTime(),
                    data.getEndTime(),
                    data.getInterval()
            );
            return null;
        });

    }

    public BistroTimeDTO getRegular() {
        return tx.inTransaction(() -> {
            OpenTime regular =  openTimeRepository.findRegular();
            return OpenTimeMapper.mapOpenTimeToDTO(regular);
        });
    }
}
