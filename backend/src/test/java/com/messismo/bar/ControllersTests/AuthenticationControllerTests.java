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

        RegisterRequestDTO request = new RegisterRequestDTO("username","existing@example.com", "Password1");
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
    public void testRegister_ConflictEmptyMail() {

        RegisterRequestDTO request = new RegisterRequestDTO("null", "", "Password1");
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Wrong email format", response.getBody());

    }

    @Test
    public void testRegister_ConflictEmptyUsername() {

        RegisterRequestDTO request = new RegisterRequestDTO("", "null@gmail.com", "Password1");
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing data for user registration", response.getBody());

    }

    @Test
    public void testRegister_ConflictEmptyPassword() {

        RegisterRequestDTO request = new RegisterRequestDTO("username1", "null@gmail.com", "");
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Password has to be at least 8 characters long, with an uppercase,lowercase and a number", response.getBody());

    }


    @Test
    public void testRegister_ConflictInvalidPasswordFormatExceptionTooShort() {

        RegisterRequestDTO request = new RegisterRequestDTO("username","existing@example.com", "pA1");
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Password has to be at least 8 characters long, with an uppercase,lowercase and a number", response.getBody());

    }

    @Test
    public void testRegister_ConflictInvalidPasswordFormatExceptionNoNumber() {

        RegisterRequestDTO request = new RegisterRequestDTO("username","existing@example.com", "PasswordsP");
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Password has to be at least 8 characters long, with an uppercase,lowercase and a number", response.getBody());

    }

    @Test
    public void testRegister_ConflictInvalidPasswordFormatExceptionNoUpperCase() {

        RegisterRequestDTO request = new RegisterRequestDTO("username","existing@example.com", "apasswords2");
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Password has to be at least 8 characters long, with an uppercase,lowercase and a number", response.getBody());

    }

    @Test
    public void testRegister_ConflictInvalidPasswordFormatExceptionNoLowerCase() {

        RegisterRequestDTO request = new RegisterRequestDTO("username","existing@example.com", "PASSWORDS23");
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Password has to be at least 8 characters long, with an uppercase,lowercase and a number", response.getBody());

    }

    @Test
    public void testRegister_ConflictInvalidPasswordFormatExceptionEmpty() {

        RegisterRequestDTO request = new RegisterRequestDTO("username","existing@example.com", "");
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Password has to be at least 8 characters long, with an uppercase,lowercase and a number", response.getBody());

    }

    @Test
    public void testRegister_ConflictInvalidEmailFormatException() throws Exception {

        RegisterRequestDTO request = new RegisterRequestDTO( "username","existingexample.com", "Password1");
        when(authenticationService.register(any(RegisterRequestDTO.class))).thenThrow(new UserAlreadyExistsException("User already exists"));
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Wrong email format", response.getBody());

    }

    @Test
    public void testRegister_ConflictUserAlreadyExistsException() throws Exception {

        RegisterRequestDTO request = new RegisterRequestDTO( "username","existing@example.com", "Password1");
        when(authenticationService.register(any(RegisterRequestDTO.class))).thenThrow(new UserAlreadyExistsException("User already exists"));
        ResponseEntity<?> response = authenticationController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());

    }

    @Test
    public void testRegister_InternalServerError() throws Exception {

        RegisterRequestDTO requestDTO = new RegisterRequestDTO("username","existing@example.com", "Password1");
        when(authenticationService.register(requestDTO)).thenThrow(new Exception("Internal error"));
        ResponseEntity<?> response = authenticationController.register(requestDTO);

        verify(authenticationService).register(requestDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal error", response.getBody());

    }

    @Test
    public void testAuthenticateSuccess() throws Exception {

        AuthenticationRequestDTO mockRequest = new AuthenticationRequestDTO("test@example.com", "testPassword1");
        AuthenticationResponseDTO mockResponse = new AuthenticationResponseDTO("mockJwtToken", "mockRefreshToken", "test@example.com", Role.EMPLOYEE);
        when(authenticationService.authenticate(any(AuthenticationRequestDTO.class))).thenReturn(mockResponse);
        ResponseEntity<?> response = authenticationController.authenticate(mockRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), mockResponse);

    }

    @Test
    public void testAuthenticate_ConflictEmptyEmail() {

        AuthenticationRequestDTO mockRequest = new AuthenticationRequestDTO("", "testPassword1");
        ResponseEntity<?> response = authenticationController.authenticate(mockRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(response.getBody(), "Wrong email format");

    }

    @Test
    public void testAuthenticate_ConflictEmptyPassword()  {

        AuthenticationRequestDTO mockRequest = new AuthenticationRequestDTO("test@example.com", "");
        ResponseEntity<?> response = authenticationController.authenticate(mockRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(response.getBody(),"Password has to be at least 8 characters long, with an uppercase,lowercase and a number" );

    }

    @Test
    public void testAuthenticate_ConflictWrongEmailFormat() {

        AuthenticationRequestDTO mockRequest = new AuthenticationRequestDTO("testexample.com", "testPassword1");
        ResponseEntity<?> response = authenticationController.authenticate(mockRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(response.getBody(), "Wrong email format");

    }

    @Test
    public void testAuthenticate_ConflictWrongPasswordFormat() {

        AuthenticationRequestDTO mockRequest = new AuthenticationRequestDTO("test@example.com", "testPassw");
        AuthenticationResponseDTO mockResponse = new AuthenticationResponseDTO("mockJwtToken", "mockRefreshToken", "test@example.com", Role.EMPLOYEE);
        ResponseEntity<?> response = authenticationController.authenticate(mockRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(response.getBody(), "Password has to be at least 8 characters long, with an uppercase,lowercase and a number");

    }

    @Test
    public void testAuthenticateFailure() throws Exception {

        AuthenticationRequestDTO mockRequest = new AuthenticationRequestDTO("test@example.com", "testPassword1");
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
    public void testForgotPassword_ConflictWithNullEmail() {

        String mockEmail = null;
        ResponseEntity<String> response = authenticationController.forgotPassword(mockEmail);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing data for password recovery", response.getBody());

    }

    @Test
    public void testForgotPassword_ConflictWithEmptyEmail() {

        String mockEmail = "";
        ResponseEntity<String> response = authenticationController.forgotPassword(mockEmail);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Wrong email format", response.getBody());

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

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("test@example.com", "1234", "newPasswogg4rd");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenReturn("Password changed successfully");
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());

    }

    @Test
    public void testChangeForgottenPasswordUserNotFound() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("nonexistent@example.com", "1234", "newPassword34");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new UserNotFoundException("No user has that email"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("No user has that email", response.getBody());

    }

    @Test
    public void testChangeForgottenPassword_ConflictNullEmailAndPassword() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO(null, "1234", null);
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new UserNotFoundException("No user has that email"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing data for changing password", response.getBody());

    }

    @Test
    public void testChangeForgottenPassword_ConflictEmptyPing() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("nonexistent@example.com", "", "newPassword");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new UserNotFoundException("No user has that email"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing data for changing password", response.getBody());

    }

    @Test
    public void testChangeForgottenPassword_ConflictWrongEmailFormat() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("nonexistentexample.com", "123445", "newPassword22");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new UserNotFoundException("No user has that email"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Wrong email format", response.getBody());

    }

    @Test
    public void testChangeForgottenPassword_ConflictWrongPasswordFormat() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("nonexiste@ntexample.com", "123445", "newPassword");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new UserNotFoundException("No user has that email"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Password has to be at least 8 characters long, with an uppercase,lowercase and a number", response.getBody());

    }

    @Test
    public void testChangeForgottenPasswordNoPinCreated() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("test@example.com", "1234", "newPassword23");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new NoPinCreatedForUserException("The user has no PINs created"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("The user has no PINs created", response.getBody());

    }

    @Test
    public void testChangeForgottenPasswordPinExpired() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("test@example.com", "1234", "newPassword2");
        when(passwordRecoveryService.changeForgottenPassword(any(PasswordRecoveryDTO.class))).thenThrow(new PinExpiredException("PIN expired!"));
        ResponseEntity<String> response = authenticationController.changeForgottenPassword(mockDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("PIN expired!", response.getBody());

    }

    @Test
    public void testChangeForgottenPasswordInternalServerError() throws Exception {

        PasswordRecoveryDTO mockDTO = new PasswordRecoveryDTO("test@example.com", "1234", "newPassword3");
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
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(25).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).reservationDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CANNOT have the capacity under 1 or higher than maximum capacity", response.getBody());

    }

    @Test
    public void testAddReservationWithInvalidCapacityZero() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(15));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(0).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).reservationDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CANNOT have the capacity under 1 or higher than maximum capacity", response.getBody());

    }

    @Test
    public void testAddReservationWithMissingInformation() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(5));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(1).comment("My birthday").shift(aShift).reservationDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing information to add a reservation", response.getBody());

    }

    @Test
    public void testAddReservationSuccessful() throws Exception {

        when(barService.getBarConfiguration()).thenReturn(new Bar(15));
        when(reservationService.addReservation(any())).thenReturn("Reservation added successfully");
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).reservationDate(LocalDate.of(2024, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Reservation added successfully", response.getBody());

    }

    @Test
    public void testAddReservationWithMissingCapacity() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(5));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().clientEmail("client0@gmail.com").comment("My birthday").shift(aShift).reservationDate(LocalDate.of(2023, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Missing capacity to create a reservation", response.getBody());

    }

    @Test
    public void testAddReservationWithDateBeforeActual() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(5));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(3).clientEmail("client0@gmail.com").comment("My birthday").shift(aShift).reservationDate(LocalDate.of(2020, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CANNOT use a date from the past nor today", response.getBody());

    }

    @Test
    public void testAddReservation_ConflictWrongEmailFormat() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(5));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(3).clientEmail("client0gmail.com").comment("My birthday").shift(aShift).reservationDate(LocalDate.of(2020, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Wrong email format", response.getBody());

    }

    @Test
    public void testAddReservation_ConflictWrongCommentFormat() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(5));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(3).clientEmail("client0@gmail.com").comment("My birthdavlkñxncjvklxjclkvjxclkvjxcklvjxcklvjxclkvjxclkvjxcklvjxclvkjxclvkxjcwr8ofudoifuse8oru89w24r7249045iu235y829457u4829543534terkltgñjerlñkgdkrlñgkdrñlgkmdlfñmgdñlfkmgldkfñgdfgdfgdfgdfgdfgdfdy").shift(aShift).reservationDate(LocalDate.of(2020, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Wrong comment format", response.getBody());

    }


    @Test
    public void testAddReservation_ConflictWrongPhoneFormat() {

        when(barService.getBarConfiguration()).thenReturn(new Bar(5));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(3).clientEmail("client0@gmail.com").clientPhone("-5").comment("My birthday").shift(aShift).reservationDate(LocalDate.of(2020, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Wrong phone format", response.getBody());

    }

    @Test
    public void testAddReservationWithBarCapacityExceededException() throws Exception {

        when(barService.getBarConfiguration()).thenReturn(new Bar(15));
        when(reservationService.addReservation(any())).thenThrow(new BarCapacityExceededException("The selected capacity for the reservation exceeds bar capacity"));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).reservationDate(LocalDate.of(2025, 12, 1)).build();
        ResponseEntity<String> response = authenticationController.addReservation(requestDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("The selected capacity for the reservation exceeds bar capacity", response.getBody());
    }


    @Test
    public void testAddReservationWithException() throws Exception {

        when(barService.getBarConfiguration()).thenReturn(new Bar(15));
        when(reservationService.addReservation(any())).thenThrow(new Exception("INTERNAL_ERROR"));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        NewReservationRequestDTO requestDTO = NewReservationRequestDTO.builder().capacity(5).clientEmail("client0@gmail.com").clientPhone("1568837531").comment("My birthday").shift(aShift).reservationDate(LocalDate.of(2025, 12, 1)).build();
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

    @Test
    public void testGetShiftsForADate_Success() throws Exception {

        LocalDateDTO localDateDTO = new LocalDateDTO(LocalDate.now().plusDays(1));
        Shift aShift = new Shift(LocalTime.of(10, 0), LocalTime.of(11, 0));
        Shift aShift1 = new Shift(LocalTime.of(11, 0), LocalTime.of(12, 0));
        List<Shift> filteredShifts = List.of(aShift,aShift1);
        when(reservationService.getShiftsForADate(any(LocalDate.class)))
                .thenReturn(filteredShifts);
        ResponseEntity<?> responseEntity = authenticationController.getShiftsForADate(localDateDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(filteredShifts,responseEntity.getBody());

    }

    @Test
    public void testGetShiftsForADate_Conflict() {

        LocalDateDTO localDateDTO = new LocalDateDTO(LocalDate.now());
        ResponseEntity<?> responseEntity = authenticationController.getShiftsForADate(localDateDTO);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals("CANNOT use a date from the past nor today",responseEntity.getBody());
    }

    @Test
    public void testGetShiftsForADate_InternalServerError() throws Exception {

        LocalDateDTO localDateDTO = new LocalDateDTO(LocalDate.now().plusDays(1));
        when(reservationService.getShiftsForADate(any())).thenThrow(new Exception("CANNOT get shifts for a date at the moment"));
        ResponseEntity<?> responseEntity = authenticationController.getShiftsForADate(localDateDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("CANNOT get shifts for a date at the moment",responseEntity.getBody());

    }


}
