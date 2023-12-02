package com.messismo.bar.ServicesTests;

import com.messismo.bar.DTOs.ModifyBarCapacityRequestDTO;
import com.messismo.bar.DTOs.NewBarRequestDTO;
import com.messismo.bar.Entities.Bar;
import com.messismo.bar.Entities.Reservation;
import com.messismo.bar.Entities.Shift;
import com.messismo.bar.Exceptions.AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException;
import com.messismo.bar.Exceptions.BarNotFoundException;
import com.messismo.bar.Repositories.BarRepository;
import com.messismo.bar.Services.BarService;
import com.messismo.bar.Services.ReservationService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BarServiceTests {

    @Mock
    private BarRepository barRepository;
    @Mock
    private ReservationService reservationService;
    @InjectMocks
    private BarService barService;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddBarConfiguration() throws Exception {

        NewBarRequestDTO requestDTO = new NewBarRequestDTO(15);
        assertEquals("Bar configuration added successfully", barService.addBarConfiguration(requestDTO));

    }

    @Test
    public void testAddBarConfigurationWithException() {

        doThrow(new RuntimeException("CANNOT add bar configuration")).when(barRepository).save(any(Bar.class));

        NewBarRequestDTO requestDTO = new NewBarRequestDTO(5);
        Exception exception = Assert.assertThrows(Exception.class, () -> {
            barService.addBarConfiguration(requestDTO);
        });
        Assertions.assertEquals("CANNOT add bar configuration", exception.getMessage());

    }

    @Test
    public void testAddBarConfigurationWithZeroCapacityException() {

        NewBarRequestDTO requestDTO = new NewBarRequestDTO(0);
        Exception exception = Assert.assertThrows(Exception.class, () -> {
            barService.addBarConfiguration(requestDTO);
        });
        Assertions.assertEquals("CANNOT add bar configuration", exception.getMessage());

    }

    @Test
    public void testAddBarConfigurationWithBelowZeroCapacityException() {

        NewBarRequestDTO requestDTO = new NewBarRequestDTO(-10);
        Exception exception = Assert.assertThrows(Exception.class, () -> {
            barService.addBarConfiguration(requestDTO);
        });
        Assertions.assertEquals("CANNOT add bar configuration", exception.getMessage());

    }

    @Test
    public void testGetBarConfiguration() {

        Bar mockBar = new Bar(1L, 50);
        Bar mockBar2 = new Bar(2L, 25);
        List<Bar> mockBars = List.of(mockBar, mockBar2);
        when(barRepository.findAll()).thenReturn(mockBars);
        Bar result = barService.getBarConfiguration();

        assertEquals(mockBar, result);

    }

    @Test
    public void testModifyBarCapacitySuccessful() throws Exception {

        Bar mockBar = new Bar(1L, 50);
        when(barRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockBar));
        when(reservationService.getAllReservations()).thenReturn(Collections.emptyList());
        ModifyBarCapacityRequestDTO requestDTO = new ModifyBarCapacityRequestDTO(1L, 55);

        String result = barService.modifyBarCapacity(requestDTO);
        assertEquals("Bar capacity updated successfully", result);

    }

    @Test
    public void testModifyBarCapacityWithBarNotFoundException() {

        when(barRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        ModifyBarCapacityRequestDTO requestDTO = new ModifyBarCapacityRequestDTO(25L, 100);
        BarNotFoundException exception = Assert.assertThrows(BarNotFoundException.class, () -> {
            barService.modifyBarCapacity(requestDTO);
        });
        Assertions.assertEquals("Provided bar id DOES NOT match any bar id", exception.getMessage());

    }

    @Test
    public void testModifyBarCapacityWithReservationException() {

        Bar bar = new Bar(1L,50);
        List<Reservation> mockReservations = List.of(
                new Reservation(new Shift(LocalTime.of(14,0),LocalTime.of(15,0)),LocalDateTime.of(2023,1,1,14,0), LocalDateTime.of(2023,1,1,15,0),"martin@mail.com", 2,"Birthday"),
                new Reservation(new Shift(LocalTime.of(14,0),LocalTime.of(15,0)),LocalDateTime.of(2023,2,1,14,0), LocalDateTime.of(2023,2,1,15,0),"martin2@mail.com", 2,"Birthday2"),
                new Reservation(new Shift(LocalTime.of(14,0),LocalTime.of(15,0)),LocalDateTime.of(2023,1,1,15,0), LocalDateTime.of(2023,1,1,16,0),"martin3@mail.com", 2,"Birthday3")
        );
        when(reservationService.getAllReservations()).thenReturn(mockReservations);
        when(barRepository.findById(1L)).thenReturn(Optional.of(bar));

        ModifyBarCapacityRequestDTO requestDTO = new ModifyBarCapacityRequestDTO(1L, 3);
        AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException exception = Assert.assertThrows(AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException.class, () -> {
            barService.modifyBarCapacity(requestDTO);
        });
        Assertions.assertEquals("There is a shift with a higher capacity than the requested", exception.getMessage());

    }

    @Test
    public void testModifyBarCapacityWithGeneralException() {

        when(barRepository.findById(anyLong())).thenThrow(new RuntimeException("CANNOT modify bar capacity at the moment"));

        ModifyBarCapacityRequestDTO requestDTO = new ModifyBarCapacityRequestDTO(1L, 50);
        assertThrows(Exception.class, () -> barService.modifyBarCapacity(requestDTO));
    }


}
