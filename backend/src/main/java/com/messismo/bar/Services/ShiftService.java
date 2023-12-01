package com.messismo.bar.Services;

import com.messismo.bar.DTOs.DeleteShiftRequestDTO;
import com.messismo.bar.DTOs.NewShiftRequestDTO;
import com.messismo.bar.Entities.Reservation;
import com.messismo.bar.Entities.Shift;
import com.messismo.bar.Exceptions.CannotCreateShiftInBetweenOtherShiftException;
import com.messismo.bar.Exceptions.CannotDeleteAShiftWithReservationsException;
import com.messismo.bar.Exceptions.ShiftNotFoundException;
import com.messismo.bar.Repositories.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;

    private final ReservationService reservationService;

    public String addShift(NewShiftRequestDTO newShiftRequestDTO) throws Exception {
        try {
            List<Shift> allShifts = shiftRepository.findAll();
            for (Shift shift : allShifts) {
                if ((newShiftRequestDTO.getStartingHour().isBefore(shift.getFinishingHour()) && newShiftRequestDTO.getStartingHour().isAfter(shift.getStartingHour())) || (newShiftRequestDTO.getFinishingHour().isBefore(shift.getFinishingHour()) && newShiftRequestDTO.getFinishingHour().isAfter(shift.getStartingHour()))) {
                    throw new CannotCreateShiftInBetweenOtherShiftException("CANNOT create a shift with an starting hour or finishing hour in between another shift");
                }
            }
            Shift newShift = new Shift(newShiftRequestDTO.getStartingHour(), newShiftRequestDTO.getFinishingHour());
            shiftRepository.save(newShift);
            return "Shift added successfully";
        } catch (CannotCreateShiftInBetweenOtherShiftException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("CANNOT create a shift at the moment");
        }
    }

    public String deleteShift(DeleteShiftRequestDTO deleteShiftRequestDTO) throws Exception {
        try {
            Shift shift = shiftRepository.findById(deleteShiftRequestDTO.getShiftId()).orElseThrow(() -> new ShiftNotFoundException("Provided shift id DOES NOT match any shift id"));
            List<Reservation> allReservations = reservationService.getAllReservations();
            for (Reservation reservation : allReservations) {
                if (reservation.getShift().getStartingHour().equals(shift.getStartingHour()) && reservation.getShift().getFinishingHour().equals(shift.getFinishingHour())) {
                    throw new CannotDeleteAShiftWithReservationsException("CANNOT delete a shift with reservations using it");
                }
            }
            shiftRepository.delete(shift);
            return "Shift deleted successfully";
        } catch (ShiftNotFoundException | CannotDeleteAShiftWithReservationsException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("CANNOT delete a shift at the moment");
        }
    }

    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }
}
