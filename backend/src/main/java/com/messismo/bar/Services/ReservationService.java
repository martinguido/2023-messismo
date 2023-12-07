package com.messismo.bar.Services;

import com.messismo.bar.DTOs.DeleteReservationRequestDTO;
import com.messismo.bar.DTOs.NewReservationRequestDTO;
import com.messismo.bar.Entities.Reservation;
import com.messismo.bar.Exceptions.BarCapacityExceededException;
import com.messismo.bar.Exceptions.ReservationNotFoundException;
import com.messismo.bar.Repositories.BarRepository;
import com.messismo.bar.Repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
            List<Reservation> allReservations = reservationRepository.findAllByShiftAndDate(newReservationRequestDTO.getShift(), newReservationRequestDTO.getReservationDate());
            Integer currentCapacity = 0;
            for (Reservation reservation : allReservations) {
                currentCapacity += reservation.getCapacity();
            }
            if (currentCapacity + newReservationRequestDTO.getCapacity() > maxCapacity) {
                throw new BarCapacityExceededException("The selected capacity for the reservation exceeds bar capacity");
            }
            Integer clientPhoneCorrected=null;
            if (newReservationRequestDTO.getClientPhone() != null) {
                clientPhoneCorrected = Integer.parseInt(newReservationRequestDTO.getClientPhone());
            }
            Reservation newReservation = new Reservation(newReservationRequestDTO.getShift(), newReservationRequestDTO.getReservationDate(), newReservationRequestDTO.getClientEmail(),clientPhoneCorrected, newReservationRequestDTO.getCapacity(), newReservationRequestDTO.getComment());
            reservationRepository.save(newReservation);
            return "Reservation added successfully";
        } catch (BarCapacityExceededException e) {
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

    public List<Reservation> getAllReservations() {
        List<Reservation> allReservations = reservationRepository.findAll();
        List<Reservation> updateReservationsStates = updateReservationsStates(allReservations);
        return updateReservationsStates;
    }

    private List<Reservation> updateReservationsStates(List<Reservation> allReservations) {
        LocalDateTime today = LocalDateTime.now();
        for(Reservation reservation : allReservations){
            LocalDateTime startingDate = reservation.getReservationDate().atTime(reservation.getShift().getStartingHour());
            LocalDateTime finishingDate = reservation.getReservationDate().atTime(reservation.getShift().getFinishingHour());
            if(Objects.equals(reservation.getState(), "Upcoming") &&  startingDate.isBefore(today) && finishingDate.isAfter(today)){
                reservation.updateToInProcessState();
                reservationRepository.save(reservation);
            }
            if(startingDate.isBefore(today) && finishingDate.isBefore(today)){
                reservation.updateToExpiredState();
                reservationRepository.save(reservation);
            }
            if(startingDate.isAfter(today) && finishingDate.isAfter(today)){
                reservation.updateToUpcoming();
                reservationRepository.save(reservation);
            }
        }
        return reservationRepository.findAll();
    }
}
