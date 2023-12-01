package com.messismo.bar.Services;

import com.messismo.bar.DTOs.DeleteReservationRequestDTO;
import com.messismo.bar.DTOs.NewReservationRequestDTO;
import com.messismo.bar.Entities.Reservation;
import com.messismo.bar.Exceptions.BarCapacityExceededException;
import com.messismo.bar.Exceptions.ReservationNotFoundException;
import com.messismo.bar.Exceptions.ReservationStartingDateMustBeBeforeFinishinDateException;
import com.messismo.bar.Repositories.BarRepository;
import com.messismo.bar.Repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final BarRepository barRepository;

    public String addReservation(NewReservationRequestDTO newReservationRequestDTO) throws Exception {
        try {
            Integer maxCapacity = barRepository.findAll().get(0).getCapacity();
            if (maxCapacity < newReservationRequestDTO.getCapacity()) {
                throw new BarCapacityExceededException("The selected capacity for the reservation exceeds bar capacity");
            }
            LocalDateTime startingDate = LocalDateTime.of(newReservationRequestDTO.getStartingDate(), newReservationRequestDTO.getShift().getStartingHour());
            LocalDateTime finishingDate = LocalDateTime.of(newReservationRequestDTO.getFinishingDate(), newReservationRequestDTO.getShift().getFinishingHour());
            if (startingDate.isAfter(finishingDate)) {
                throw new ReservationStartingDateMustBeBeforeFinishinDateException("The selected starting date must be before the finishing date");
            }
            List<Reservation> allReservations = findBetweenStartingDateAndFinishingDate(startingDate, finishingDate);
            Integer currentCapacity = 0;
            for (Reservation reservation : allReservations) {
                currentCapacity += reservation.getCapacity();
            }
            if (currentCapacity + newReservationRequestDTO.getCapacity() > maxCapacity) {
                throw new BarCapacityExceededException("The selected capacity for the reservation exceeds bar capacity");
            }
            if (newReservationRequestDTO.getClientPhone() == null && !newReservationRequestDTO.getClientEmail().isEmpty()) {
                Reservation newReservation = new Reservation(newReservationRequestDTO.getShift(), startingDate, finishingDate, newReservationRequestDTO.getClientEmail(), newReservationRequestDTO.getCapacity(), newReservationRequestDTO.getComment());
                reservationRepository.save(newReservation);
            } else if (!newReservationRequestDTO.getClientPhone().isEmpty() && newReservationRequestDTO.getClientEmail() == null) {
                Reservation newReservation = new Reservation(newReservationRequestDTO.getShift(), startingDate, finishingDate, Integer.parseInt(newReservationRequestDTO.getClientPhone()), newReservationRequestDTO.getCapacity(), newReservationRequestDTO.getComment());
                reservationRepository.save(newReservation);
            } else if (!newReservationRequestDTO.getClientPhone().isEmpty() && !newReservationRequestDTO.getClientEmail().isEmpty()) {
                Reservation newReservation = new Reservation(newReservationRequestDTO.getShift(), startingDate, finishingDate, newReservationRequestDTO.getClientEmail(), Integer.parseInt(newReservationRequestDTO.getClientPhone()), newReservationRequestDTO.getCapacity(), newReservationRequestDTO.getComment());
                reservationRepository.save(newReservation);
            }
            return "Reservation added successfully";
        } catch (BarCapacityExceededException | ReservationStartingDateMustBeBeforeFinishinDateException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("CANNOT create a reservation at the moment");
        }
    }

    public String deleteReservation(DeleteReservationRequestDTO deleteReservationRequestDTO) throws Exception {
        try {
            Reservation reservation = reservationRepository.findById(deleteReservationRequestDTO.getReservationId()).orElseThrow(() -> new ReservationNotFoundException("No reservation has that id"));
            reservationRepository.delete(reservation);
            return "Reservation deleted successfully";
        } catch (ReservationNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("CANNOT delete a reservation at the moment");
        }
    }

    public List<Reservation> findBetweenStartingDateAndFinishingDate(LocalDateTime startingDate, LocalDateTime finishingDate) {
        List<Reservation> allReservations = reservationRepository.findAll();
        List<Reservation> filteredReservations = new ArrayList<>();
        for (Reservation reservation : allReservations) {
            if ((reservation.getStartingDate().isAfter(startingDate) || reservation.getStartingDate().equals(startingDate)) && reservation.getStartingDate().isBefore(finishingDate) && (reservation.getFinishingDate().isBefore(finishingDate) || reservation.getFinishingDate().equals(finishingDate)) && reservation.getFinishingDate().isAfter(startingDate)) {
                filteredReservations.add(reservation);
            }
        }
        return filteredReservations;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}
