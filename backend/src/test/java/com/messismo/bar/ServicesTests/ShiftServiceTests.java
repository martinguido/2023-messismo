package com.messismo.bar.ServicesTests;

import com.messismo.bar.DTOs.DeleteShiftRequestDTO;
import com.messismo.bar.DTOs.NewShiftRequestDTO;
import com.messismo.bar.Entities.Reservation;
import com.messismo.bar.Entities.Shift;
import com.messismo.bar.Exceptions.CannotCreateShiftInBetweenOtherShiftException;
import com.messismo.bar.Exceptions.CannotDeleteAShiftWithReservationsException;
import com.messismo.bar.Exceptions.ShiftNotFoundException;
import com.messismo.bar.Repositories.ShiftRepository;
import com.messismo.bar.Services.ReservationService;
import com.messismo.bar.Services.ShiftService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ShiftServiceTests {

    @InjectMocks
    private ShiftService shiftService;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ReservationService reservationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetAllShifts() {

        List<Shift> mockShifts = Arrays.asList(new Shift(1L, LocalTime.of(15, 0), LocalTime.of(16, 0)), new Shift(2L, LocalTime.of(16, 0), LocalTime.of(17, 0)), new Shift(3L, LocalTime.of(17, 0), LocalTime.of(18, 0)));
        when(shiftRepository.findAll()).thenReturn(mockShifts);

        List<Shift> result = shiftService.getAllShifts();
        assertEquals(mockShifts, result);

    }

    @Test
    public void testDeleteShiftSuccessful() throws Exception {

        Shift existingShift = new Shift(1L, LocalTime.of(15, 0), LocalTime.of(16, 0));
        when(shiftRepository.findById(existingShift.getShiftId())).thenReturn(Optional.of(existingShift));
        when(reservationService.getAllReservations()).thenReturn(new ArrayList<>());

        DeleteShiftRequestDTO requestDTO = new DeleteShiftRequestDTO(existingShift.getShiftId());
        String result = shiftService.deleteShift(requestDTO);
        assertEquals("Shift deleted successfully", result);

    }

    @Test
    public void testDeleteShiftWithShiftNotFoundException() {

        long nonExistentShiftId = 999L;
        when(shiftRepository.findById(nonExistentShiftId)).thenReturn(Optional.empty());
        DeleteShiftRequestDTO requestDTO = new DeleteShiftRequestDTO(nonExistentShiftId);

        ShiftNotFoundException exception = Assert.assertThrows(ShiftNotFoundException.class, () -> {
            shiftService.deleteShift(requestDTO);
        });
        Assertions.assertEquals("Provided shift id DOES NOT match any shift id", exception.getMessage());

    }

    @Test
    public void testDeleteShiftWithReservationsException() {

        Shift existingShift = new Shift(1L, LocalTime.of(15, 0), LocalTime.of(16, 0));
        when(shiftRepository.findById(existingShift.getShiftId())).thenReturn(Optional.of(existingShift));
        List<Reservation> mockReservations = List.of(new Reservation(existingShift, LocalDateTime.of(2023, 1, 1, 14, 0), LocalDateTime.of(2023, 1, 1, 15, 0), "martin@mail.com", 2, "Birthday"), new Reservation(existingShift, LocalDateTime.of(2023, 2, 1, 14, 0), LocalDateTime.of(2023, 2, 1, 15, 0), "martin2@mail.com", 2, "Birthday2"), new Reservation(new Shift(LocalTime.of(14, 0), LocalTime.of(15, 0)), LocalDateTime.of(2023, 1, 1, 15, 0), LocalDateTime.of(2023, 1, 1, 16, 0), "martin3@mail.com", 2, "Birthday3"));
        when(reservationService.getAllReservations()).thenReturn(mockReservations);
        DeleteShiftRequestDTO requestDTO = new DeleteShiftRequestDTO(existingShift.getShiftId());

        CannotDeleteAShiftWithReservationsException exception = Assert.assertThrows(CannotDeleteAShiftWithReservationsException.class, () -> {
            shiftService.deleteShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT delete a shift with reservations using it", exception.getMessage());

    }

    @Test
    public void testDeleteShiftWithGenericException() {

        Shift existingShift = new Shift(1L, LocalTime.of(15, 0), LocalTime.of(16, 0));
        when(shiftRepository.findById(existingShift.getShiftId())).thenReturn(Optional.of(existingShift));
        when(reservationService.getAllReservations()).thenThrow(new RuntimeException("CANNOT delete a shift at the moment"));
        DeleteShiftRequestDTO requestDTO = new DeleteShiftRequestDTO(existingShift.getShiftId());

        Exception exception = Assert.assertThrows(Exception.class, () -> {
            shiftService.deleteShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT delete a shift at the moment", exception.getMessage());
    }

    @Test
    public void testAddShiftSuccessful() throws Exception {

        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(9, 0), LocalTime.of(10, 0)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        when(shiftRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(10, 0)).finishingHour(LocalTime.of(11, 0)).build();
        String result = shiftService.addShift(requestDTO);

        assertEquals("Shift added successfully", result);

    }

    @Test
    public void testAddShiftWithCannotCreateShiftInBetweenOtherShiftException() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(9, 0), LocalTime.of(10, 0)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(9, 30)).finishingHour(LocalTime.of(10, 30)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = Assert.assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }

    @Test
    public void testAddShiftWithCannotCreateShiftWithSameShift() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 10)).finishingHour(LocalTime.of(18, 15)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = Assert.assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }
    @Test
    public void testAddShiftWithCannotCreateShiftWithEqualsStartingHourAndFinishingHourAfter() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 10)).finishingHour(LocalTime.of(18, 16)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = Assert.assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }
    @Test
    public void testAddShiftWithCannotCreateShiftWithEqualsStartingHourAndFinishingHourBefore() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 10)).finishingHour(LocalTime.of(18, 14)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = Assert.assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }
    @Test
    public void testAddShiftWithCannotCreateShiftWithStartingHourAfterAndEqualsFinishingHour() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 11)).finishingHour(LocalTime.of(18, 15)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = Assert.assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }
    @Test
    public void testAddShiftWithCannotCreateShiftWithStartingHourBeforeAndEqualsFinishingHour() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 9)).finishingHour(LocalTime.of(18, 15)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = Assert.assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }


   @Test
    public void testAddShiftWithCannotCreateShiftContainingAShift() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 9)).finishingHour(LocalTime.of(18, 16)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = Assert.assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }



    @Test
    public void testAddShiftWithGenericException() {

        when(shiftRepository.findAll()).thenThrow(new RuntimeException("CANNOT create a shift at the moment"));
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(10, 0)).finishingHour(LocalTime.of(11, 0)).build();

        Exception exception = Assert.assertThrows(Exception.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift at the moment", exception.getMessage());

    }

}
