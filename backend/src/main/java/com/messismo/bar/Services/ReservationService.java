package com.messismo.bar.Services;

import com.messismo.bar.DTOs.DeleteReservationRequestDTO;
import com.messismo.bar.DTOs.NewReservationRequestDTO;
import com.messismo.bar.DTOs.UseReservationDTO;
import com.messismo.bar.Entities.Reservation;
import com.messismo.bar.Entities.Shift;
import com.messismo.bar.Exceptions.BarCapacityExceededException;
import com.messismo.bar.Exceptions.ReservationAlreadyUsedException;
import com.messismo.bar.Exceptions.ReservationNotFoundException;
import com.messismo.bar.Repositories.BarRepository;
import com.messismo.bar.Repositories.ReservationRepository;
import com.messismo.bar.Repositories.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final BarRepository barRepository;

    private final JavaMailSender javaMailSender;

    private final ShiftRepository shiftRepository;

    public String addReservation(NewReservationRequestDTO newReservationRequestDTO) throws Exception {
        try {
            Integer maxCapacity = barRepository.findAll().get(0).getCapacity();
            if (maxCapacity < newReservationRequestDTO.getCapacity()) {
                throw new BarCapacityExceededException("The selected capacity for the reservation exceeds bar capacity");
            }
            System.out.println("CHECK1");
            List<Reservation> allReservations = reservationRepository.findAllByShiftAndDate(newReservationRequestDTO.getShift(), newReservationRequestDTO.getReservationDate());
            System.out.println(allReservations);
            System.out.println("CHECK2");
            Integer currentCapacity = 0;
            for (Reservation reservation : allReservations) {
                currentCapacity += reservation.getCapacity();
            }
            System.out.println("CHECK2");
            if (currentCapacity + newReservationRequestDTO.getCapacity() > maxCapacity) {
                throw new BarCapacityExceededException("The selected capacity for the reservation exceeds bar capacity");
            }
            System.out.println("CHECK2");
            String clientPhoneCorrected = null;
            if (newReservationRequestDTO.getClientPhone() != null && !newReservationRequestDTO.getClientPhone().isEmpty()) {
                System.out.println("ENTRA ACA");
                clientPhoneCorrected = newReservationRequestDTO.getClientPhone();
                System.out.println("NO SE ROMPE");
            }
            System.out.println("LLEGA HASTA CREAR LA RESERVA");
            Reservation newReservation = new Reservation(newReservationRequestDTO.getShift(), newReservationRequestDTO.getReservationDate(), newReservationRequestDTO.getClientEmail(), clientPhoneCorrected, newReservationRequestDTO.getCapacity(), newReservationRequestDTO.getComment());
            System.out.println("LA CREA");
            reservationRepository.save(newReservation);
            System.out.println("LA GUARDO");
            if (newReservationRequestDTO.getClientEmail() != null && !newReservationRequestDTO.getClientEmail().isEmpty()) {
                System.out.println("ENTRA ACA Y EL MAIL ES: " + "-" + newReservationRequestDTO.getClientEmail() + "-");
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setTo(newReservationRequestDTO.getClientEmail());
                simpleMailMessage.setFrom("automaticmoebar@hotmail.com");
                simpleMailMessage.setSubject("Reservation created successfully for Moe's Bar");
                String text = "Your reservation:" + newReservationRequestDTO.getReservationDate() + " " + newReservationRequestDTO.getShift().getStartingHour() + " " + newReservationRequestDTO.getShift().getFinishingHour() + " . Hope to see you soon!";
                simpleMailMessage.setText(text);
                javaMailSender.send(simpleMailMessage);
            }
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
        return updateReservationsStates(allReservations);
    }

    private List<Reservation> updateReservationsStates(List<Reservation> allReservations) {
        LocalDateTime today = LocalDateTime.now();
        for (Reservation reservation : allReservations) {
            LocalDateTime startingDate = reservation.getReservationDate().atTime(reservation.getShift().getStartingHour());
            LocalDateTime finishingDate = reservation.getReservationDate().atTime(reservation.getShift().getFinishingHour());
            if (Objects.equals(reservation.getState(), "Upcoming") && startingDate.isBefore(today) && finishingDate.isAfter(today)) {
                reservation.updateToInProcessState();
                reservationRepository.save(reservation);
            }
            if (startingDate.isBefore(today) && finishingDate.isBefore(today)) {
                reservation.updateToExpiredState();
                reservationRepository.save(reservation);
            }
            if (startingDate.isAfter(today) && finishingDate.isAfter(today)) {
                reservation.updateToUpcoming();
                reservationRepository.save(reservation);
            }
        }
        return reservationRepository.findAll();
    }

    public String markAsUsed(UseReservationDTO useReservationDTO) throws Exception {
        try {
            Reservation reservation = reservationRepository.findById(useReservationDTO.getReservationId()).orElseThrow(() -> new ReservationNotFoundException("No reservation has that id"));
            if (reservation.getUsed()) {
                throw new ReservationAlreadyUsedException("Reservation already used");
            } else {
                reservation.setAsUsed();
                reservationRepository.save(reservation);
                return "Reservation mark as used successfully";
            }
        } catch (ReservationNotFoundException | ReservationAlreadyUsedException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("CANNOT use a reservation right now");
        }

    }

    public List<Shift> getShiftsForADate(LocalDate localDate) throws Exception {
        try {
            List<Reservation> allReservations = getAllReservations();
            List<Shift> allShifts = shiftRepository.findAll();
            Integer barCapacity = barRepository.findAll().get(0).getCapacity();
            HashMap<Shift, Integer> countPerShiftPerDay = new HashMap<>();
            for (Shift shift : allShifts) {
                countPerShiftPerDay.put(shift, 0);
            }
            for (Reservation reservation : allReservations) {
                if (reservation.getReservationDate().equals(localDate)) {
                    countPerShiftPerDay.put(reservation.getShift(), countPerShiftPerDay.get(reservation.getShift()) + reservation.getCapacity());
                }
            }
            List<Shift> responseShifts = new ArrayList<>();
            for (Map.Entry<Shift, Integer> entry : countPerShiftPerDay.entrySet()) {
                if (!(entry.getValue() >= barCapacity)) {
                    responseShifts.add(entry.getKey());
                }
            }
            return responseShifts;
        } catch (Exception e) {
            throw new Exception("CANNOT get shifts for a date at the moment");
        }
    }


    public Integer getReservationsForAShift(Shift shift) {
        return reservationRepository.findAllByShift(shift).size();
    }

    public Integer getQuantityForAShift(Shift shift) {
        List<Reservation> reservations = reservationRepository.findAllByShift(shift);
        Integer count = 0;
        for (Reservation reservation : reservations) {
            count = count + reservation.getCapacity();
        }
        return count;
    }

    public HashMap<String, Object> getReservationsMetric() throws Exception {
        try{
            List<Reservation> inProcessReservations = reservationRepository.findAllByState("In Process");
            Integer inProcessCount = inProcessReservations.size();
            List<Reservation> expiredReservations = reservationRepository.findAllByState("Expired");
            Integer expiredCount = expiredReservations.size();
            List<Reservation> upcomingReservations = reservationRepository.findAllByState("Upcoming");
            Integer upcomingCount = upcomingReservations.size();
            Integer totalCount = inProcessCount + expiredCount;
            List<Reservation> mergedReservations = new ArrayList<>(inProcessReservations);
            mergedReservations.addAll(expiredReservations);
            Integer usedCount =0;
            for (Reservation reservation : mergedReservations){
                if( reservation.getUsed()==Boolean.TRUE){
                    usedCount = usedCount +1;
                }
            }
            Double percentage = 0.00;
            if(usedCount!=0 && totalCount!=0){
                percentage = ((double)usedCount/totalCount)*100;
            }
            HashMap<String,Object> response = new HashMap<>();
            response.put("usedReservationsPercentage",percentage);
            response.put("expiredReservations", expiredCount);
            response.put("inProcessReservations", inProcessCount);
            response.put("upcomingReservations", upcomingCount);
            response.put("totalReservations", upcomingCount+inProcessCount+expiredCount);
            return response;
        }catch (Exception e){
            throw new Exception("CANNOT get reservations metric at the moment");
        }
    }

}
