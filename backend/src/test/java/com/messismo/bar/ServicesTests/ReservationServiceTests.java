package com.messismo.bar.ServicesTests;

import com.messismo.bar.DTOs.*;
import com.messismo.bar.Entities.Bar;
import com.messismo.bar.Entities.Reservation;
import com.messismo.bar.Entities.Shift;
import com.messismo.bar.Exceptions.BarCapacityExceededException;
import com.messismo.bar.Exceptions.ReservationAlreadyUsedException;
import com.messismo.bar.Exceptions.ReservationNotFoundException;
import com.messismo.bar.Repositories.BarRepository;
import com.messismo.bar.Repositories.ReservationRepository;
import com.messismo.bar.Repositories.ShiftRepository;
import com.messismo.bar.Services.ReservationService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReservationServiceTests {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BarRepository barRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private ShiftRepository shiftRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllReservations() {

        Shift existingShift = new Shift(1L, LocalTime.of(15, 0), LocalTime.of(16, 0));
        List<Reservation> expectedReservations = List.of(new Reservation(existingShift, LocalDate.of(2023, 1, 1), "guidomartin7@gmail.com", 156683434, 2, "Birthday"), new Reservation(existingShift, LocalDate.of(2023, 2, 1), "martin2@mail.com", 1554209090, 2, "Birthday2"), new Reservation(new Shift(LocalTime.of(14, 0), LocalTime.of(15, 0)), LocalDate.of(2023, 1, 1), "martin3@mail.com", null, 2, "Birthday3"));
        when(reservationRepository.findAll()).thenReturn(expectedReservations);

        List<Reservation> result = reservationService.getAllReservations();
        assertEquals(expectedReservations, result);

    }


    @Test
    public void testDeleteReservationSuccessfully() throws Exception {

        Shift existingShift = new Shift(1L, LocalTime.of(15, 0), LocalTime.of(16, 0));
        Reservation existingReservation = new Reservation(existingShift, LocalDate.of(2023, 1, 1), null, 1566785465,2, "Birthday");
        when(reservationRepository.findById(any())).thenReturn(Optional.of(existingReservation));

        DeleteReservationRequestDTO requestDTO = new DeleteReservationRequestDTO(1L);
        String result = reservationService.deleteReservation(requestDTO);
        assertEquals("Reservation deleted successfully", result);

    }

    @Test
    public void testDeleteReservationWithReservationNotFoundException() {

        when(reservationRepository.findById(any())).thenReturn(Optional.empty());

        DeleteReservationRequestDTO requestDTO = new DeleteReservationRequestDTO(1L);
        assertThrows(ReservationNotFoundException.class, () -> reservationService.deleteReservation(requestDTO));
        ReservationNotFoundException exception = Assert.assertThrows(ReservationNotFoundException.class, () -> {
            reservationService.deleteReservation(requestDTO);
        });
        Assertions.assertEquals("No reservation has that id", exception.getMessage());

    }

    @Test
    public void testDeleteReservationWithGenericException() {
        when(reservationRepository.findById(any())).thenThrow(new RuntimeException("CANNOT delete a reservation at the moment"));

        DeleteReservationRequestDTO requestDTO = new DeleteReservationRequestDTO(100L);
        Exception exception = Assert.assertThrows(Exception.class, () -> {
            reservationService.deleteReservation(requestDTO);
        });
        Assertions.assertEquals("CANNOT delete a reservation at the moment", exception.getMessage());

    }

    @Test
    public void testAddReservationSuccessfully() throws Exception {

        Bar mockBar = new Bar(1L, 20);
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0));
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        when(reservationRepository.findAllByShiftAndDate(aShift, LocalDate.of(2024, 12, 1))).thenReturn(List.of());
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).shift(aShift).reservationDate(LocalDate.of(2024, 12, 1)).clientEmail("guidomartin7@gmail.com").build();
        String result = reservationService.addReservation(requestDTO);
        assertEquals("Reservation added successfully", result);
        // ADDED TO CHECK IF RESERVATION HAS SELECTED SHIFT
        assertEquals(aShift, requestDTO.getShift());

    }

    @Test
    public void testAddReservationSuccessfully_OnlyWithEmail() throws Exception {

        Bar mockBar = new Bar(1L, 20);
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0));
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        when(reservationRepository.findAllByShiftAndDate(aShift, LocalDate.of(2024, 12, 1))).thenReturn(List.of());
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).shift(aShift).reservationDate(LocalDate.of(2024, 12, 1)).clientEmail("guidomartin7@gmail.com").build();
        String result = reservationService.addReservation(requestDTO);
        assertEquals("Reservation added successfully", result);
        // ADDED TO CHECK IF RESERVATION HAS SELECTED SHIFT
        assertEquals(aShift, requestDTO.getShift());

    }

    @Test
    public void testAddReservationSuccessfully_OnlyWithPhone() throws Exception {

        Bar mockBar = new Bar(1L, 20);
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0));
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        when(reservationRepository.findAllByShiftAndDate(aShift, LocalDate.of(2024, 12, 1))).thenReturn(List.of());
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).shift(aShift).reservationDate(LocalDate.of(2024, 12, 1)).clientPhone("15665645").build();
        String result = reservationService.addReservation(requestDTO);
        assertEquals("Reservation added successfully", result);
        // ADDED TO CHECK IF RESERVATION HAS SELECTED SHIFT
        assertEquals(aShift, requestDTO.getShift());

    }

    @Test
    public void testAddReservationSuccessfully_WithPhoneAndEmail() throws Exception {

        Bar mockBar = new Bar(1L, 20);
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0));
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        when(reservationRepository.findAllByShiftAndDate(aShift, LocalDate.of(2024, 12, 1))).thenReturn(List.of());
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).shift(aShift).reservationDate(LocalDate.of(2024, 12, 1)).clientEmail("guidomartin7@gmail.com").clientPhone("15665645").build();
        String result = reservationService.addReservation(requestDTO);
        assertEquals("Reservation added successfully", result);
        // ADDED TO CHECK IF RESERVATION HAS SELECTED SHIFT
        assertEquals(aShift, requestDTO.getShift());

    }


    @Test
    public void testAddReservationWithBarCapacityExceededException() {

        Bar mockBar = new Bar(1L, 20);
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(30).shift(new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0))).reservationDate(LocalDate.of(2024, 12, 1)).clientEmail("test@example.com").build();
        assertThrows(BarCapacityExceededException.class, () -> reservationService.addReservation(requestDTO));

    }

    @Test
    public void testAddReservationWithNoPhoneOrEmailException() {

        Bar mockBar = new Bar(1L, 20);
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(30).shift(new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0))).reservationDate(LocalDate.of(2024, 12, 1)).build();
        assertThrows(Exception.class, () -> reservationService.addReservation(requestDTO));

    }

    @Test
    public void testAddReservationWithPhoneBelowZeroException() {

        Bar mockBar = new Bar(1L, 20);
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(30).shift(new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0))).reservationDate(LocalDate.of(2024, 12, 1)).clientPhone("-55").build();
        assertThrows(Exception.class, () -> reservationService.addReservation(requestDTO));

    }

    @Test
    public void testAddReservationWithWrongEmailFormatException() {

        Bar mockBar = new Bar(1L, 20);
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(30).shift(new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0))).reservationDate(LocalDate.of(2024, 12, 1)).clientEmail("martin.com").build();
        assertThrows(Exception.class, () -> reservationService.addReservation(requestDTO));

    }




    @Test
    public void testAddReservationWithGenericException() {

        when(barRepository.findAll()).thenThrow(new RuntimeException("Error fetching bar capacity"));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).shift(new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0))).reservationDate(LocalDate.of(2024, 12, 1)).clientEmail("test@example.com").build();
        Exception exception = Assert.assertThrows(Exception.class, () -> {
            reservationService.addReservation(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a reservation at the moment", exception.getMessage());

    }


    @Test
    public void testMarkAsUsed_Success() throws Exception {

        UseReservationDTO useReservationDTO = new UseReservationDTO(1L);
        Reservation reservation = new Reservation();
        reservation.setUsed(false);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        String result = reservationService.markAsUsed(useReservationDTO);

        assertEquals("Reservation mark as used successfully", result);
        Assertions.assertTrue(reservation.getUsed());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(reservation);

    }

    @Test
    public void testMarkAsUsed_ReservationAlreadyUsed() {

        UseReservationDTO useReservationDTO = new UseReservationDTO(1L);
        Reservation reservation = new Reservation();
        reservation.setUsed(true);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        ReservationAlreadyUsedException exception = assertThrows(ReservationAlreadyUsedException.class, () -> {
            reservationService.markAsUsed(useReservationDTO);
        });

        assertEquals("Reservation already used", exception.getMessage());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, never()).save(any(Reservation.class));

    }

    @Test
    public void testMarkAsUsed_ReservationNotFound() {

        UseReservationDTO useReservationDTO = new UseReservationDTO(1L);
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());
        ReservationNotFoundException exception = assertThrows(ReservationNotFoundException.class, () -> {
            reservationService.markAsUsed(useReservationDTO);
        });

        assertEquals("No reservation has that id", exception.getMessage());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, never()).save(any(Reservation.class));

    }

    @Test
    public void testMarkAsUsed_InternalServerError() {

        UseReservationDTO useReservationDTO = new UseReservationDTO();
        useReservationDTO.setReservationId(1L);
        when(reservationRepository.findById(1L)).thenThrow(new RuntimeException("Some unexpected exception"));
        Exception exception = assertThrows(Exception.class, () -> {
            reservationService.markAsUsed(useReservationDTO);
        });

        assertEquals("CANNOT use a reservation right now", exception.getMessage());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, never()).save(any(Reservation.class));

    }

    @Test
    public void testGetShiftsForADate_Success() throws Exception {
        LocalDate localDate = LocalDate.now();
        Bar bar = new Bar(1L,50);
        when(barRepository.findAll()).thenReturn(List.of(bar));
        Shift shift1 = new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0));
        Shift shift2 = new Shift(LocalTime.of(12, 0), LocalTime.of(14, 0));
        Shift shift3 = new Shift(LocalTime.of(17, 0), LocalTime.of(19, 0));
        List<Shift> allShifts = List.of(shift1, shift2,shift3);
        when(shiftRepository.findAll()).thenReturn(allShifts);
        Reservation reservation1 = new Reservation(shift1, localDate, null, 1566785465,25, "Birthday");
        Reservation reservation3 = new Reservation(shift1, localDate, null, 1566785465,20, "Birthday");
        Reservation reservation4 = new Reservation(shift1, localDate, null, 1566785465,5, "Birthday");
        Reservation reservation2 = new Reservation(shift2, LocalDate.of(2023, 12, 1), null, 1566785465,2, "Birthday");
        Reservation reservation5 = new Reservation(shift2, LocalDate.of(2023, 12, 1), null, 1566785465,20, "Birthday");
        Reservation reservation6 = new Reservation(shift2, LocalDate.of(2023, 12, 2), null, 1566785465,49, "Birthday");
        List<Reservation> allReservations = List.of(reservation1, reservation2,reservation3,reservation4,reservation5,reservation6);
        when(reservationRepository.findAll()).thenReturn(allReservations);

        List<Shift> result = reservationService.getShiftsForADate(localDate);
        assertEquals(2, result.size());
    }


    @Test
    public void testGetShiftsForADate_InternalServerError() {

        LocalDate localDate = LocalDate.now();
        when(reservationRepository.findAll()).thenThrow(new RuntimeException("Some unexpected exception"));
        Exception exception = assertThrows(Exception.class, () -> {
            reservationService.getShiftsForADate(localDate);
        });

        assertEquals("CANNOT get shifts for a date at the moment", exception.getMessage());
        verify(reservationRepository, times(1)).findAll();

    }

}
