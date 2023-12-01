package com.messismo.bar.ControllersTests;

import com.messismo.bar.Controllers.AdminController;
import com.messismo.bar.DTOs.DeleteShiftRequestDTO;
import com.messismo.bar.DTOs.ModifyBarCapacityRequestDTO;
import com.messismo.bar.DTOs.NewShiftRequestDTO;
import com.messismo.bar.DTOs.UserIdDTO;
import com.messismo.bar.Entities.Bar;
import com.messismo.bar.Entities.Shift;
import com.messismo.bar.Exceptions.*;
import com.messismo.bar.Services.BarService;
import com.messismo.bar.Services.ShiftService;
import com.messismo.bar.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AdminControllerTests {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;

    @Mock
    private ShiftService shiftService;

    @Mock
    private BarService barService;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testValidateAdmin_Success() throws Exception {

        UserIdDTO userIdDTO = new UserIdDTO(1L);
        when(userService.validateEmployee(any(UserIdDTO.class))).thenReturn("Validation successful");
        ResponseEntity<?> response = adminController.validateAdmin(userIdDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Validation successful", response.getBody());

    }

    @Test
    public void testValidateAdmin_ConflictMissingUserId() {

        UserIdDTO userIdDTO = new UserIdDTO(null);
        ResponseEntity<?> response = adminController.validateAdmin(userIdDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing userId to upgrade to manager", response.getBody());

    }

    @Test
    public void testValidateAdmin_ConflictUsernameNotFoundException() throws Exception {
        UserIdDTO userIdDTO = new UserIdDTO(1L);
        when(userService.validateEmployee(any(UserIdDTO.class))).thenThrow(new UsernameNotFoundException("User not found"));
        ResponseEntity<?> response = adminController.validateAdmin(userIdDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User not found", response.getBody());

    }

    @Test
    public void testValidateAdmin_ConflictCannotUpgradeToManagerException() throws Exception {

        UserIdDTO userIdDTO = new UserIdDTO(1L);
        when(userService.validateEmployee(any(UserIdDTO.class))).thenThrow(new CannotUpgradeToManager("Cannot upgrade to manager"));
        ResponseEntity<?> response = adminController.validateAdmin(userIdDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Cannot upgrade to manager", response.getBody());

    }

    @Test
    public void testValidateAdmin_InternalServerError() throws Exception {

        UserIdDTO userIdDTO = new UserIdDTO(1L);
        when(userService.validateEmployee(any(UserIdDTO.class))).thenThrow(new Exception("Some internal error"));
        ResponseEntity<?> response = adminController.validateAdmin(userIdDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Some internal error", response.getBody());

    }

    @Test
    public void testModifyBarCapacitySuccessful() throws Exception {

        when(barService.modifyBarCapacity(any())).thenReturn("Bar capacity updated successfully");
        ModifyBarCapacityRequestDTO requestDTO = new ModifyBarCapacityRequestDTO(1L, 25);
        ResponseEntity<String> response = adminController.modifyBarCapacity(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bar capacity updated successfully", response.getBody());

    }

    @Test
    public void testModifyBarCapacityWithMissingInformation() {

        ModifyBarCapacityRequestDTO requestDTO = new ModifyBarCapacityRequestDTO();
        ResponseEntity<String> response = adminController.modifyBarCapacity(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing information to modify bar capacity", response.getBody());

    }

    @Test
    public void testModifyBarCapacityWithBarNotFoundException() throws Exception {

        when(barService.modifyBarCapacity(any())).thenThrow(new BarNotFoundException("Provided bar id DOES NOT match any bar id"));
        ModifyBarCapacityRequestDTO requestDTO = new ModifyBarCapacityRequestDTO(2L, 25);
        ResponseEntity<String> response = adminController.modifyBarCapacity(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Provided bar id DOES NOT match any bar id", response.getBody());

    }

    @Test
    public void testModifyBarCapacityWithAlreadyHaveAReservationWithACapacityHigherThanSpecifiedException() throws Exception {

        when(barService.modifyBarCapacity(any())).thenThrow(new AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException("There is a shift with a higher capacity than the requested"));
        ModifyBarCapacityRequestDTO requestDTO = new ModifyBarCapacityRequestDTO(2L, 25);
        ResponseEntity<String> response = adminController.modifyBarCapacity(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("There is a shift with a higher capacity than the requested", response.getBody());

    }

    @Test
    public void testModifyBarCapacityWithException() throws Exception {

        when(barService.modifyBarCapacity(any())).thenThrow(new RuntimeException("CANNOT modify bar capacity at the moment"));
        ModifyBarCapacityRequestDTO requestDTO = new ModifyBarCapacityRequestDTO(2L, 25);
        ResponseEntity<String> response = adminController.modifyBarCapacity(requestDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("CANNOT modify bar capacity at the moment", response.getBody());

    }


    @Test
    public void testGetBarConfigurationSuccessful() {

        Bar mockBarConfiguration = new Bar(1L, 15);
        when(barService.getBarConfiguration()).thenReturn(mockBarConfiguration);
        ResponseEntity<?> response = adminController.getBarConfiguration();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBarConfiguration, response.getBody());

    }

    @Test
    public void testGetBarConfigurationWithException() {

        when(barService.getBarConfiguration()).thenThrow(new RuntimeException("ErrorMessage"));
        ResponseEntity<?> response = adminController.getBarConfiguration();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ErrorMessage", response.getBody());

    }

    @Test
    public void testGetAllShiftsSuccessful() {

        List<Shift> mockShifts = List.of(new Shift(1L, LocalTime.of(14, 0), LocalTime.of(15, 0)), new Shift(2L, LocalTime.of(15, 0), LocalTime.of(16, 0)));
        when(shiftService.getAllShifts()).thenReturn(mockShifts);
        ResponseEntity<?> response = adminController.getAllShifts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockShifts, response.getBody());

    }

    @Test
    public void testGetAllShiftsWithException() {

        when(shiftService.getAllShifts()).thenThrow(new RuntimeException("ErrorMessage"));
        ResponseEntity<?> response = adminController.getAllShifts();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ErrorMessage", response.getBody());

    }

    @Test
    public void testAddShiftSuccessful() throws Exception {

        when(shiftService.addShift(any())).thenReturn("Shift added successfully");
        NewShiftRequestDTO requestDTO = new NewShiftRequestDTO(LocalTime.of(14, 0), LocalTime.of(15, 0));
        ResponseEntity<String> response = adminController.addShift(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Shift added successfully", response.getBody());

    }

    @Test
    public void testAddShiftWithMissingInformation() {

        NewShiftRequestDTO requestDTO = new NewShiftRequestDTO();
        ResponseEntity<String> response = adminController.addShift(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing information to create a shift", response.getBody());

    }

    @Test
    public void testAddShiftWithInvalidTimeRange() {

        NewShiftRequestDTO requestDTO = new NewShiftRequestDTO(LocalTime.of(16, 0), LocalTime.of(15, 0));
        ResponseEntity<String> response = adminController.addShift(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Starting time must be before finishing time", response.getBody());

    }

    @Test
    public void testAddShiftWithCannotCreateShiftInBetweenOtherShiftException() throws Exception {

        when(shiftService.addShift(any())).thenThrow(new CannotCreateShiftInBetweenOtherShiftException("CANNOT create a shift with an starting hour or finishing hour in between another shift"));

        NewShiftRequestDTO requestDTO = new NewShiftRequestDTO(LocalTime.of(14, 0), LocalTime.of(15, 0));
        ResponseEntity<String> response = adminController.addShift(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CANNOT create a shift with an starting hour or finishing hour in between another shift", response.getBody());

    }

    @Test
    public void testAddShiftWithException() throws Exception {

        when(shiftService.addShift(any())).thenThrow(new RuntimeException("CANNOT create a shift at the moment"));
        NewShiftRequestDTO requestDTO = new NewShiftRequestDTO(LocalTime.of(14, 0), LocalTime.of(15, 0));
        ResponseEntity<String> response = adminController.addShift(requestDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("CANNOT create a shift at the moment", response.getBody());

    }


    @Test
    public void testDeleteShiftSuccessful() throws Exception {

        when(shiftService.deleteShift(any())).thenReturn("Shift deleted successfully");
        DeleteShiftRequestDTO requestDTO = new DeleteShiftRequestDTO(1L);
        ResponseEntity<String> response = adminController.deleteShift(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Shift deleted successfully", response.getBody());

    }

    @Test
    public void testDeleteShiftWithMissingInformation() {

        DeleteShiftRequestDTO requestDTO = new DeleteShiftRequestDTO();
        ResponseEntity<String> response = adminController.deleteShift(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing information to delete a shift", response.getBody());

    }

    @Test
    public void testDeleteShiftWithShiftNotFoundException() throws Exception {


        when(shiftService.deleteShift(any())).thenThrow(new ShiftNotFoundException("Provided shift id DOES NOT match any shift id"));
        DeleteShiftRequestDTO requestDTO = new DeleteShiftRequestDTO(2L);
        ResponseEntity<String> response = adminController.deleteShift(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Provided shift id DOES NOT match any shift id", response.getBody());

    }

    @Test
    public void testDeleteShiftWithCannotDeleteShiftWithReservationsException() throws Exception {

        when(shiftService.deleteShift(any())).thenThrow(new CannotDeleteAShiftWithReservationsException("CANNOT delete a shift with reservations using it"));
        DeleteShiftRequestDTO requestDTO = new DeleteShiftRequestDTO(3L);
        ResponseEntity<String> response = adminController.deleteShift(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CANNOT delete a shift with reservations using it", response.getBody());

    }

    @Test
    public void testDeleteShiftWithException() throws Exception {

        when(shiftService.deleteShift(any())).thenThrow(new RuntimeException("CANNOT delete a shift at the moment"));
        DeleteShiftRequestDTO requestDTO = new DeleteShiftRequestDTO(3L);
        ResponseEntity<String> response = adminController.deleteShift(requestDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("CANNOT delete a shift at the moment", response.getBody());

    }

}
