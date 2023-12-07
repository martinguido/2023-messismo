package com.messismo.bar.Services;

import com.messismo.bar.DTOs.ModifyBarCapacityRequestDTO;
import com.messismo.bar.DTOs.NewBarRequestDTO;
import com.messismo.bar.Entities.Bar;
import com.messismo.bar.Entities.Reservation;
import com.messismo.bar.Exceptions.AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException;
import com.messismo.bar.Exceptions.BarNotFoundException;
import com.messismo.bar.Repositories.BarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BarService {

    private final BarRepository barRepository;

    private final ReservationService reservationService;

    public String addBarConfiguration(NewBarRequestDTO newBarRequestDTO) throws Exception {
        try {
            Bar bar = new Bar(newBarRequestDTO.getCapacity());
            barRepository.save(bar);
            return "Bar configuration added successfully";
        } catch (Exception e) {
            throw new Exception("CANNOT add bar configuration");
        }
    }

    public String modifyBarCapacity(ModifyBarCapacityRequestDTO modifyBarCapacityRequestDTO) throws Exception {
        try {
            Bar bar = barRepository.findById(modifyBarCapacityRequestDTO.getBarId())
                    .orElseThrow(() -> new BarNotFoundException("Provided bar id DOES NOT match any bar id"));
            List<Reservation> allReservations = reservationService.getAllReservations();
            HashMap<List<LocalDateTime>, Integer> reservationsByDate = new HashMap<>();
            for (Reservation reservation : allReservations) {
                List<LocalDateTime> dateRange = List.of(reservation.getStartingDate(),
                        reservation.getFinishingDate());
                if (!reservationsByDate.containsKey(dateRange)) {
                    reservationsByDate.put(dateRange, reservation.getCapacity());
                } else {
                    reservationsByDate.put(dateRange, reservationsByDate.get(dateRange) + reservation.getCapacity());
                }
            }
            for (Map.Entry<List<LocalDateTime>, Integer> entry : reservationsByDate.entrySet()) {
                if (entry.getValue() > modifyBarCapacityRequestDTO.getNewCapacity()) {
                    throw new AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException(
                            "There is a shift with a higher capacity than the requested");
                }
            }
            System.out.println(reservationsByDate);
            bar.updateCapacity(modifyBarCapacityRequestDTO.getNewCapacity());
            barRepository.save(bar);
            return "Bar capacity updated successfully";
        } catch (BarNotFoundException | AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("CANNOT modify bar capacity at the moment");
        }
    }

    public Bar getBarConfiguration() {
        return barRepository.findAll().get(0);
    }
}
