package com.andreibel.server.services;

import com.andreibel.message.DTO.BistroTimeDTO;
import com.andreibel.message.DTO.SpecialDayRequest;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OpenTimeRepository;
import com.andreibel.server.entity.OpenTime;
import com.andreibel.server.utils.OpenTimeMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service responsible for managing restaurant opening hours configuration.
 * <p>
 * Handles both regular (default) opening hours and special day overrides.
 * Implemented as a singleton to ensure consistent configuration access.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class OpenTimeService {
    private static OpenTimeService instance;
    private final OpenTimeRepository openTimeRepository;
    private final TransactionManager tx;
    private OpenTimeService() {
        openTimeRepository = OpenTimeRepository.getInstance();
        tx = TransactionManager.getInstance();
    }
    /**
     * Returns the singleton instance of {@link OpenTimeService}.
     *
     * @return the singleton OpenTimeService instance
     */
    public static OpenTimeService getInstance() {
        if (instance == null) {
            instance = new OpenTimeService();
        }
        return instance;
    }


    /**
     * Adds a special day opening hours configuration.
     * <p>
     * Special days override the regular opening hours for a specific date.
     *
     * @param data the special day request containing date, title, and hours
     */
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

    /**
     * Updates the regular (default) opening hours configuration.
     *
     * @param data the new regular opening hours to apply
     */
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

    /**
     * Retrieves the regular (default) opening hours configuration.
     *
     * @return the regular opening hours as a DTO
     */
    public BistroTimeDTO getRegular() {
        return tx.inTransaction(() -> {
            OpenTime regular =  openTimeRepository.findRegular();
            return OpenTimeMapper.mapOpenTimeToDTO(regular);
        });
    }
    public BistroTimeDTO getSpecial() {
        return tx.inTransaction(() -> {
            LocalDate localDate = LocalDate.now();
            Date sqlDate = Date.valueOf(localDate);
            OpenTime regular =  openTimeRepository.findSpecial(sqlDate);
            return OpenTimeMapper.mapOpenTimeToDTO(regular);
        });
    }
    public boolean isCurDaySpecial(){
        return tx.inTransaction(() -> {
            LocalDate localDate = LocalDate.now();
            Date sqlDate = Date.valueOf(localDate);
            OpenTime Special =  openTimeRepository.findSpecial(sqlDate);
            if(Special==null){
                return false;
            }else{
                return true;
            }
        });
    }
}