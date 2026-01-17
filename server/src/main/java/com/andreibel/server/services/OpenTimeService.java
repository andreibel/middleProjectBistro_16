package com.andreibel.server.services;

import com.andreibel.message.DTO.BistroTimeDTO;
import com.andreibel.message.DTO.SpecialDayRequest;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OpenTimeRepository;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.entity.OpenTime;
import com.andreibel.server.entity.Order;
import com.andreibel.server.utils.OpenTimeMapper;
import com.andreibel.server.utils.TUI;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    private final OrderRepository orderRepository;

    private OpenTimeService() {
        openTimeRepository = OpenTimeRepository.getInstance();
        tx = TransactionManager.getInstance();
        orderRepository = OrderRepository.getInstance();
    }

    /**
     * Returns the singleton instance of {@link OpenTimeService}.
     *
     * @return the singleton OpenTimeService instance
     */
    public static OpenTimeService getInstance() {
        if (instance == null) instance = new OpenTimeService();
        return instance;
    }


    /**
     * Adds a special day opening hours configuration.
     * <p>
     * Special days override the regular opening hours for a specific date.
     *
     * @param data the special day request containing date, title, and hours
     */
    public void addSpecialDay(SpecialDayRequest data) {
        List<Order> deletedOrders  = tx.inTransaction(() -> {
            openTimeRepository.addNewSpecial(
                    data.getDate(),
                    data.getTitle(),
                    data.getStartTime(),
                    data.getEndTime(),
                    data.getInterval()
            );
            return orderRepository.deleteOrdersInDateNotInRange(data);
        });
        deletedOrders.forEach(TUI::deletedOrders);
    }

    /**
     * Updates the regular (default) opening hours configuration.
     *
     * @param data the new regular opening hours to apply
     */
    public void editRegulaDay(BistroTimeDTO data) {
        List<Order> deletedOrders  = tx.inTransaction(() -> {
            openTimeRepository.updateRegular(
                    data.getStartTime(),
                    data.getEndTime(),
                    data.getInterval()
            );
            return orderRepository.deleteOrdersNotInRange(data);
        });
        deletedOrders.forEach(TUI::deletedOrders);

    }

    /**
     * Retrieves the regular (default) opening hours configuration.
     *
     * @return the regular opening hours as a DTO
     */
    public BistroTimeDTO getRegular() {
        return tx.inTransaction(() -> {
            OpenTime regular = openTimeRepository.findRegular();
            return OpenTimeMapper.mapOpenTimeToDTO(regular);
        });
    }

    public BistroTimeDTO getSpecial() {
        return tx.inTransaction(() -> {
            LocalDate localDate = LocalDate.now();
            Date sqlDate = Date.valueOf(localDate);
            OpenTime special = openTimeRepository.findSpecial(sqlDate);
            if (special == null) return null;
            return OpenTimeMapper.mapOpenTimeToDTO(special);
        });
    }

    public boolean isInOpeningHours() {
        BistroTimeDTO day = tx.inTransaction(() -> {
            BistroTimeDTO isSpecial = getSpecial();
            if (isSpecial != null) return isSpecial;
            return getRegular();
        });
        return day.getEndTime().isAfter(LocalTime.now()) && day.getStartTime().isBefore(LocalTime.now());
    }

}