package com.messismo.bar.ServicesTests;

import com.messismo.bar.DTOs.DeleteReservationRequestDTO;
import com.messismo.bar.DTOs.NewReservationRequestDTO;
import com.messismo.bar.Entities.Bar;
import com.messismo.bar.Entities.Reservation;
import com.messismo.bar.Entities.Shift;
import com.messismo.bar.Exceptions.BarCapacityExceededException;
import com.messismo.bar.Exceptions.ReservationNotFoundException;
import com.messismo.bar.Exceptions.ReservationStartingDateMustBeBeforeFinishinDateException;
import com.messismo.bar.Repositories.BarRepository;
import com.messismo.bar.Repositories.ReservationRepository;
import com.messismo.bar.Services.ReservationService;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ReservationServiceTests {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BarRepository barRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllReservations() {

        Shift existingShift = new Shift(1L, LocalTime.of(15, 0), LocalTime.of(16, 0));
        List<Reservation> expectedReservations = List.of(
                new Reservation(existingShift, LocalDateTime.of(2023, 1, 1, 14, 0), LocalDateTime.of(2023, 1, 1, 15, 0),
                        "martin@mail.com", 2, "Birthday"),
                new Reservation(existingShift, LocalDateTime.of(2023, 2, 1, 14, 0), LocalDateTime.of(2023, 2, 1, 15, 0),
                        "martin2@mail.com", 2, "Birthday2"),
                new Reservation(new Shift(LocalTime.of(14, 0), LocalTime.of(15, 0)),
                        LocalDateTime.of(2023, 1, 1, 15, 0), LocalDateTime.of(2023, 1, 1, 16, 0), "martin3@mail.com", 2,
                        "Birthday3"));
        when(reservationRepository.findAll()).thenReturn(expectedReservations);

        List<Reservation> result = reservationService.getAllReservations();
        assertEquals(expectedReservations, result);

    }

    @Test
    public void testFindBetweenStartingDateAndFinishingDate() {

        Shift existingShift = new Shift(1L, LocalTime.of(15, 0), LocalTime.of(16, 0));
        List<Reservation> allReservations = List.of(
                new Reservation(existingShift, LocalDateTime.of(2023, 1, 1, 14, 0), LocalDateTime.of(2023, 1, 1, 15, 0),
                        "martin@mail.com", 2, "Birthday"),
                new Reservation(existingShift, LocalDateTime.of(2023, 2, 1, 14, 0), LocalDateTime.of(2023, 2, 1, 15, 0),
                        "martin2@mail.com", 2, "Birthday2"),
                new Reservation(new Shift(LocalTime.of(14, 0), LocalTime.of(15, 0)),
                        LocalDateTime.of(2023, 1, 1, 15, 0), LocalDateTime.of(2023, 1, 1, 16, 0), "martin3@mail.com", 2,
                        "Birthday3"));
        List<Reservation> filteredReservations = List.of(
                new Reservation(existingShift, LocalDateTime.of(2023, 1, 1, 14, 0), LocalDateTime.of(2023, 1, 1, 15, 0),
                        "martin@mail.com", 2, "Birthday"),
                new Reservation(new Shift(LocalTime.of(14, 0), LocalTime.of(15, 0)),
                        LocalDateTime.of(2023, 1, 1, 15, 0), LocalDateTime.of(2023, 1, 1, 16, 0), "martin3@mail.com", 2,
                        "Birthday3"));

        when(reservationRepository.findAll()).thenReturn(allReservations);

        LocalDateTime startingDate = LocalDateTime.of(2023, 1, 1, 13, 0);
        LocalDateTime finishingDate = LocalDateTime.of(2023, 1, 1, 17, 0);
        List<Reservation> result = reservationService.findBetweenStartingDateAndFinishingDate(startingDate,
                finishingDate);

        assertEquals(2, result.size());
        assertEquals(filteredReservations, result);
    }

    @Test
    public void testDeleteReservationSuccessfully() throws Exception {

        Shift existingShift = new Shift(1L, LocalTime.of(15, 0), LocalTime.of(16, 0));
        Reservation existingReservation = new Reservation(existingShift, LocalDateTime.of(2023, 1, 1, 14, 0),
                LocalDateTime.of(2023, 1, 1, 15, 0), "martin@mail.com", 2, "Birthday");
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
        when(reservationRepository.findById(any()))
                .thenThrow(new RuntimeException("CANNOT delete a reservation at the moment"));

        DeleteReservationRequestDTO requestDTO = new DeleteReservationRequestDTO(100L);
        Exception exception = Assert.assertThrows(Exception.class, () -> {
            reservationService.deleteReservation(requestDTO);
        });
        Assertions.assertEquals("CANNOT delete a reservation at the moment", exception.getMessage());

    }

    @Test
    public void testAddReservationSuccessfully() throws Exception {

        Bar mockBar = new Bar(1L, 20);
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        when(reservationService.findBetweenStartingDateAndFinishingDate(LocalDateTime.of(2023, 12, 1, 10, 0),
                LocalDateTime.of(2023, 12, 1, 12, 0))).thenReturn(List.of());
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder()
                .capacity(5)
                .shift(new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .startingDate(LocalDate.of(2023, 12, 1))
                .finishingDate(LocalDate.of(2023, 12, 1))
                .clientEmail("test@example.com")
                .build();
        String result = reservationService.addReservation(requestDTO);
        assertEquals("Reservation added successfully", result);

    }

    @Test
    public void testAddReservationWithBarCapacityExceededException() {

        Bar mockBar = new Bar(1L, 20);
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder()
                .capacity(30)
                .shift(new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .startingDate(LocalDate.of(2023, 12, 1))
                .finishingDate(LocalDate.of(2023, 12, 1))
                .clientEmail("test@example.com")
                .build();
        assertThrows(BarCapacityExceededException.class, () -> reservationService.addReservation(requestDTO));

    }

    @Test
    public void testAddReservationWithReservationStartingDateMustBeBeforeFinishingDateException() {

        Bar mockBar = new Bar(1L, 20);
        when(barRepository.findAll()).thenReturn(List.of(mockBar));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder()
                .capacity(5)
                .shift(new Shift(LocalTime.of(14, 0), LocalTime.of(15, 0)))
                .startingDate(LocalDate.now().plusDays(2))
                .finishingDate(LocalDate.now())
                .clientEmail("test@example.com")
                .build();

        ReservationStartingDateMustBeBeforeFinishinDateException exception = Assert
                .assertThrows(ReservationStartingDateMustBeBeforeFinishinDateException.class, () -> {
                    reservationService.addReservation(requestDTO);
                });
        Assertions.assertEquals("The selected starting date must be before the finishing date", exception.getMessage());

    }

    @Test
    public void testAddReservationWithGenericException() {

        when(barRepository.findAll()).thenThrow(new RuntimeException("Error fetching bar capacity"));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder()
                .capacity(5)
                .shift(new Shift(LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .startingDate(LocalDate.of(2023, 12, 1))
                .finishingDate(LocalDate.of(2023, 12, 1))
                .clientEmail("test@example.com")
                .build();
        Exception exception = Assert.assertThrows(Exception.class, () -> {
            reservationService.addReservation(requestDTO);
        });
        Assertions.assertEquals("CANNOT create a reservation at the moment", exception.getMessage());

    }

}
