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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
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

        ShiftNotFoundException exception = assertThrows(ShiftNotFoundException.class, () -> {
            shiftService.deleteShift(requestDTO);
        });
        Assertions.assertEquals("Provided shift id DOES NOT match any shift id", exception.getMessage());

    }

    @Test
    public void testDeleteShiftWithReservationsException() {

        Shift existingShift = new Shift(1L, LocalTime.of(15, 0), LocalTime.of(16, 0));
        when(shiftRepository.findById(existingShift.getShiftId())).thenReturn(Optional.of(existingShift));
        List<Reservation> mockReservations = List.of(new Reservation(existingShift, LocalDate.of(2023, 1, 1), "martin@mail.com", null, 2, "Birthday"), new Reservation(existingShift, LocalDate.of(2023, 2, 1), "martin2@mail.com", null, 2, "Birthday2"), new Reservation(new Shift(LocalTime.of(14, 0), LocalTime.of(15, 0)), LocalDate.of(2023, 1, 1), "martin3@mail.com", null, 2, "Birthday3"));
        when(reservationService.getAllReservations()).thenReturn(mockReservations);
        DeleteShiftRequestDTO requestDTO = new DeleteShiftRequestDTO(existingShift.getShiftId());

        CannotDeleteAShiftWithReservationsException exception = assertThrows(CannotDeleteAShiftWithReservationsException.class, () -> {
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

        Exception exception = assertThrows(Exception.class, () -> {
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

        CannotCreateShiftInBetweenOtherShiftException exception = assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }

    @Test
    public void testAddShiftWithCannotCreateShiftWithSameShift() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 10)).finishingHour(LocalTime.of(18, 15)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }

    @Test
    public void testAddShiftWithCannotCreateShiftWithEqualsStartingHourAndFinishingHourAfter() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 10)).finishingHour(LocalTime.of(18, 16)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }

    @Test
    public void testAddShiftWithCannotCreateShiftWithEqualsStartingHourAndFinishingHourBefore() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 10)).finishingHour(LocalTime.of(18, 14)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }

    @Test
    public void testAddShiftWithCannotCreateShiftWithStartingHourAfterAndEqualsFinishingHour() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 11)).finishingHour(LocalTime.of(18, 15)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }

    @Test
    public void testAddShiftWithCannotCreateShiftWithStartingHourBeforeAndEqualsFinishingHour() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 9)).finishingHour(LocalTime.of(18, 15)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }


    @Test
    public void testAddShiftWithCannotCreateShiftContainingAShift() {
        List<Shift> existingShifts = Arrays.asList(new Shift(LocalTime.of(18, 10), LocalTime.of(18, 15)), new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        when(shiftRepository.findAll()).thenReturn(existingShifts);
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(18, 9)).finishingHour(LocalTime.of(18, 16)).build();

        CannotCreateShiftInBetweenOtherShiftException exception = assertThrows(CannotCreateShiftInBetweenOtherShiftException.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", exception.getMessage());

    }


    @Test
    public void testAddShiftWithGenericException() {

        when(shiftRepository.findAll()).thenThrow(new RuntimeException("CANNOT create a shift at the moment"));
        NewShiftRequestDTO requestDTO = NewShiftRequestDTO.builder().startingHour(LocalTime.of(10, 0)).finishingHour(LocalTime.of(11, 0)).build();

        Exception exception = assertThrows(Exception.class, () -> {
            shiftService.addShift(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a shift at the moment", exception.getMessage());

    }

    @Test
    public void testGetMostSelectedShifts() throws Exception {

        Shift shift1 = new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0));
        Shift shift2 = new Shift(LocalTime.of(12, 0), LocalTime.of(14, 0));
        Shift shift3 = new Shift(LocalTime.of(14, 0), LocalTime.of(16, 0));
        List<Shift> allShifts = Arrays.asList(shift1, shift2,shift3);
        when(shiftRepository.findAll()).thenReturn(allShifts);
        when(reservationService.getReservationsForAShift(shift1)).thenReturn(3);
        when(reservationService.getReservationsForAShift(shift2)).thenReturn(10);
        when(reservationService.getReservationsForAShift(shift3)).thenReturn(5);

        LinkedHashMap<Shift,Integer> response = new LinkedHashMap<>();
        response.put(shift2,10);
        response.put(shift3,5);
        response.put(shift1,3);

        LinkedHashMap<Shift, Integer> result = shiftService.getMostSelectedShifts();
        assertEquals(3, result.size());
        assertEquals(response, result);
    }

    @Test
    public void testGetMostSelectedShiftsWithNoShifts() throws Exception {

        List<Shift> allShifts = new ArrayList<>();
        when(shiftRepository.findAll()).thenReturn(allShifts);

        LinkedHashMap<Shift,Integer> response = new LinkedHashMap<>();

        LinkedHashMap<Shift, Integer> result = shiftService.getMostSelectedShifts();
        assertEquals(0, result.size());
        assertEquals(response, result);
    }

    @Test
    public void testGetMostSelectedShiftsException() {

        Shift shift1 = new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0));
        Shift shift2 = new Shift(LocalTime.of(12, 0), LocalTime.of(14, 0));

        List<Shift> allShifts = Arrays.asList(shift1, shift2);
        when(shiftRepository.findAll()).thenReturn(allShifts);
        when(reservationService.getReservationsForAShift(any())).thenThrow(new RuntimeException("Simulated exception"));
        Exception exception = assertThrows(Exception.class, () -> {
            shiftService.getMostSelectedShifts();
        });

        assertEquals("CANNOT get shifts information at the moment", exception.getMessage());
    }

    @Test
    public void testGetBusiestShifts() throws Exception {

        Shift shift1 = new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0));
        Shift shift2 = new Shift(LocalTime.of(12, 0), LocalTime.of(14, 0));
        Shift shift3 = new Shift(LocalTime.of(14, 0), LocalTime.of(16, 0));
        List<Shift> allShifts = Arrays.asList(shift1, shift2,shift3);
        when(shiftRepository.findAll()).thenReturn(allShifts);
        when(reservationService.getQuantityForAShift(shift1)).thenReturn(3);
        when(reservationService.getQuantityForAShift(shift2)).thenReturn(10);
        when(reservationService.getQuantityForAShift(shift3)).thenReturn(5);

        LinkedHashMap<Shift,Integer> response = new LinkedHashMap<>();
        response.put(shift2,10);
        response.put(shift3,5);
        response.put(shift1,3);

        LinkedHashMap<Shift, Integer> result = shiftService.getBusiestShifts();
        assertEquals(3, result.size());
        assertEquals(response, result);
    }

    @Test
    public void testGetBusiestShiftsWithNoShifts() throws Exception {

        List<Shift> allShifts = new ArrayList<>();
        when(shiftRepository.findAll()).thenReturn(allShifts);

        LinkedHashMap<Shift,Integer> response = new LinkedHashMap<>();

        LinkedHashMap<Shift, Integer> result = shiftService.getBusiestShifts();
        assertEquals(0, result.size());
        assertEquals(response, result);
    }

    @Test
    public void testGetBusiestShiftsException() {

        Shift shift1 = new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0));
        Shift shift2 = new Shift(LocalTime.of(12, 0), LocalTime.of(14, 0));

        List<Shift> allShifts = Arrays.asList(shift1, shift2);
        when(shiftRepository.findAll()).thenReturn(allShifts);
        when(reservationService.getQuantityForAShift(any())).thenThrow(new RuntimeException("Simulated exception"));
        Exception exception = assertThrows(Exception.class, () -> {
            shiftService.getBusiestShifts();
        });

        assertEquals("CANNOT get shifts information at the moment", exception.getMessage());
    }

    @Test
    public void testSortShiftsByValues() {

        Shift shift1 = new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0));
        Shift shift2 = new Shift(LocalTime.of(12, 0), LocalTime.of(14, 0));
        Shift shift3 = new Shift(LocalTime.of(14, 0), LocalTime.of(16, 0));

        HashMap<Shift, Integer> unsortedMap = new HashMap<>();
        unsortedMap.put(shift1, 5);
        unsortedMap.put(shift2, 2);
        unsortedMap.put(shift3, 8);
        LinkedHashMap<Shift, Integer> sortedMap = shiftService.sortShiftsByValues(unsortedMap);
        assertEquals(3, sortedMap.size());
        assertTrue(isMapSortedByValues(sortedMap));
    }

    private boolean isMapSortedByValues(LinkedHashMap<Shift, Integer> map) {
        Integer previousValue = null;
        for (Map.Entry<Shift, Integer> entry : map.entrySet()) {
            if (previousValue != null && entry.getValue() < previousValue) {
                return false;
            }
            previousValue = entry.getValue();
        }
        return true;
    }


}
