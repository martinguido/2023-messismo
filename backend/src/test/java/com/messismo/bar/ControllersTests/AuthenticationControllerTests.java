package com.messismo.bar.ControllersTests;

import com.messismo.bar.Controllers.AuthenticationController;
import com.messismo.bar.DTOs.*;
import com.messismo.bar.Entities.Bar;
import com.messismo.bar.Entities.Role;
import com.messismo.bar.Entities.Shift;
import com.messismo.bar.Exceptions.*;
import com.messismo.bar.Services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthenticationControllerTests {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private BarService barService;


    @Mock
    private ShiftService shiftService;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testRegister_Success() throws Exception {

        RegisterRequestDTO request = new RegisterRequestDTO("test@example.com", "email", "password");
        AuthenticationResponseDTO mockResponse = new AuthenticationResponseDTO("mockJwtToken", "mockRefreshToken", "mockEmail", Role.EMPLOYEE);
        when(authenticationService.register(any(RegisterRequestDTO.class))).thenReturn(mockResponse);
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());

    }

    @Test
    public void testRegister_ConflictMissingData() {

        RegisterRequestDTO request = new RegisterRequestDTO(null, null, null);
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing data for user registration", response.getBody());

    }

    @Test
    public void testRegister_ConflictUserAlreadyExistsException() throws Exception {

        RegisterRequestDTO request = new RegisterRequestDTO("existing@example.com", "password", "username");
        when(authenticationService.register(any(RegisterRequestDTO.class))).thenThrow(new UserAlreadyExistsException("User already exists"));
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());

    }

    @Test
    public void testRegister_InternalServerError() throws Exception {

        RegisterRequestDTO requestDTO = new RegisterRequestDTO("test@example.com", "password", "username");
        when(authenticationService.register(requestDTO)).thenThrow(new Exception("Internal error"));
        ResponseEntity<?> response = authenticationController.register(requestDTO);

        verify(authenticationService).register(requestDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal error", response.getBody());

    }

    @Test
    public void testAuthenticateSuccess() throws Exception {

        AuthenticationRequestDTO mockRequest = new AuthenticationRequestDTO("test@example.com", "testPassword");
        AuthenticationResponseDTO mockResponse = new AuthenticationResponseDTO("mockJwtToken", "mockRefreshToken", "test@example.com", Role.EMPLOYEE);
        when(authenticationService.authenticate(any(AuthenticationRequestDTO.class))).thenReturn(mockResponse);
        ResponseEntity<?> response = authenticationController.authenticate(mockRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), mockResponse);

    }

    @Test
    public void testAuthenticateFailure() throws Exception {

        AuthenticationRequestDTO mockRequest = new AuthenticationRequestDTO("test@example.com", "testPassword");
        when(authenticationService.authenticate(any(AuthenticationRequestDTO.class))).thenThrow(new Exception("Invalid user credentials"));
        ResponseEntity<?> response = authenticationController.authenticate(mockRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid user credentials", response.getBody());

    }

    @Test
    public void testAuthenticateMissingData() {

        AuthenticationRequestDTO mockRequest = new AuthenticationRequestDTO(null, "testPassword");
        ResponseEntity<?> response = authenticationController.authenticate(mockRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);
        assertEquals("Missing data for user login", response.getBody());

    }

    @Test
    public void testForgotPasswordSuccess() throws Exception {

        String mockEmail = "test@example.com";
        when(passwordRecoveryService.forgotPassword(any(String.class))).thenReturn("Email sent!");
        ResponseEntity<String> response = authenticationController.forgotPassword(mockEmail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email sent!", response.getBody());

    }

    @Test
    public void testForgotPasswordUserNotFound() throws Exception {

        String mockEmail = "nonexistent@example.com";
        when(passwordRecoveryService.forgotPassword(any(String.class))).thenThrow(new UserNotFoundException("No user has that email"));
        ResponseEntity<String> response = authenticationController.forgotPassword(mockEmail);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("No user has that email", response.getBody());

    }

    @Test
    public void testForgotPasswordInternalServerError() throws Exception {

        String mockEmail = "test@example.com";
        when(passwordRecoveryService.forgotPassword(any(String.class))).thenThrow(new Exception("Internal error"));
        ResponseEntity<String> response = authenticationController.forgotPassword(mockEmail);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal error", response.getBody());

    }

    @Test
    public void testChangeForgottenPasswordSuccess() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("test@example.com", "1234", "newPassword");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenReturn("Password changed successfully");
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());

    }

    @Test
    public void testChangeForgottenPasswordUserNotFound() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("nonexistent@example.com", "1234", "newPassword");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new UserNotFoundException("No user has that email"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("No user has that email", response.getBody());

    }

    @Test
    public void testChangeForgottenPasswordNoPinCreated() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("test@example.com", "1234", "newPassword");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new NoPinCreatedForUserException("The user has no PINs created"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("The user has no PINs created", response.getBody());

    }

    @Test
    public void testChangeForgottenPasswordPinExpired() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("test@example.com", "1234", "newPassword");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new PinExpiredException("PIN expired!"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("PIN expired!", response.getBody());

    }

    @Test
    public void testChangeForgottenPasswordInternalServerError() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("test@example.com", "1234", "newPassword");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new Exception("Internal error"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal error", response.getBody());

    }

    @Test
    public void testHealth() {

        ResponseEntity<String> response = authenticationController.health();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Server is up!", response.getBody());

    }

    @Test
    public void testAddReservationWithInvalidCapacity() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(15));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(25).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).startingDate(LocalDate.of(2023, 12, 1)).finishingDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CANNOT have the capacity under 1 or higher than maximum capacity", response.getBody());

    }

    @Test
    public void testAddReservationWithInvalidCapacityZero() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(15));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(0).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).startingDate(LocalDate.of(2023, 12, 1)).finishingDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CANNOT have the capacity under 1 or higher than maximum capacity", response.getBody());

    }

    @Test
    public void testAddReservationWithMissingInformation() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(5));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(1).comment("My birthday").shift(aShift).startingDate(LocalDate.of(2023, 12, 1)).finishingDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing information to add a reservation", response.getBody());

    }

    @Test
    public void testAddReservationSuccessful() throws Exception {

        when(barService.getBarConfiguration()).thenReturn(new Bar(15));
        when(reservationService.addReservation(any())).thenReturn("Reservation added successfully");
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).startingDate(LocalDate.of(2023, 12, 1)).finishingDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Reservation added successfully", response.getBody());

    }

    @Test
    public void testAddReservationWithMissingCapacity() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(5));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().clientEmail("client0@gmail.com").comment("My birthday").shift(aShift).startingDate(LocalDate.of(2023, 12, 1)).finishingDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing capacity to create a reservation", response.getBody());

    }

    @Test
    public void testAddReservationWithBarCapacityExceededException() throws Exception {

        when(barService.getBarConfiguration()).thenReturn(new Bar(15));
        when(reservationService.addReservation(any())).thenThrow(new BarCapacityExceededException("The selected capacity for the reservation exceeds bar capacity"));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).startingDate(LocalDate.of(2023, 12, 1)).finishingDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("The selected capacity for the reservation exceeds bar capacity", response.getBody());
    }

    @Test
    public void testAddReservationWithReservationStartingDateMustBeBeforeFinishinDateException() throws Exception {

        when(barService.getBarConfiguration()).thenReturn(new Bar(15));
        when(reservationService.addReservation(any())).thenThrow(new ReservationStartingDateMustBeBeforeFinishinDateException("The selected starting date must be before the finishing date"));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).startingDate(LocalDate.of(2024, 12, 1)).finishingDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("The selected starting date must be before the finishing date", response.getBody());
    }

    @Test
    public void testAddReservationWithException() throws Exception {

        when(barService.getBarConfiguration()).thenReturn(new Bar(15));
        when(reservationService.addReservation(any())).thenThrow(new Exception("INTERNAL_ERROR"));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).startingDate(LocalDate.of(2023, 12, 1)).finishingDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_ERROR", response.getBody());
    }


    @Test
    public void testGetAllShiftsSuccessful() {

        List<Shift> mockShifts = List.of(new Shift(1L, LocalTime.of(14, 0), LocalTime.of(15, 0)), new Shift(2L, LocalTime.of(15, 0), LocalTime.of(16, 0)));
        when(shiftService.getAllShifts()).thenReturn(mockShifts);
        ResponseEntity<?> response = authenticationController.getAllShifts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockShifts, response.getBody());

    }

    @Test
    public void testGetAllShiftsWithException() {

        when(shiftService.getAllShifts()).thenThrow(new RuntimeException("ErrorMessage"));
        ResponseEntity<?> response = authenticationController.getAllShifts();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ErrorMessage", response.getBody());

    }
}
