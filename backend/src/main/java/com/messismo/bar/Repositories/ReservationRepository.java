package com.messismo.bar.Repositories;

import com.messismo.bar.Entities.Reservation;
import com.messismo.bar.Entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.shift = :shift AND r.reservationDate = :reservationDate")
    List<Reservation> findAllByShiftAndDate(@Param("shift") Shift shift, @Param("reservationDate") LocalDate reservationDate);

    List<Reservation> findAllByShift(Shift shift);

    List<Reservation> findAllByState(String state);
}
