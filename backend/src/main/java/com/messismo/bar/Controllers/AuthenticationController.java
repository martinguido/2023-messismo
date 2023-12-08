package com.messismo.bar.Controllers;

import com.messismo.bar.DTOs.AuthenticationRequestDTO;
import com.messismo.bar.DTOs.NewReservationRequestDTO;
import com.messismo.bar.DTOs.PasswordRecoveryDTO;
import com.messismo.bar.DTOs.RegisterRequestDTO;
import com.messismo.bar.Exceptions.*;
import com.messismo.bar.Services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final PasswordRecoveryService passwordRecoveryService;

    private final BarService barService;

    private final ReservationService reservationService;

    private final ShiftService shiftService;

    private final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private final String passwordRegex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,}$";

    private final String phoneRegex = "^[0-9]*$";

    private final String commentRegex = "^[a-zA-Z0-9\\s]{1,150}$";


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {

        try {

            if (request.getEmail() == null || request.getPassword() == null || request.getUsername() == null || request.getUsername().isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing data for user registration");
            } else if (!request.getEmail().matches(emailRegex)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Wrong email format");
            } else if (!request.getPassword().matches(passwordRegex)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Password has to be at least 8 characters long, with an uppercase,lowercase and a number");
            } else {
                return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(request));
            }
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDTO request) {
        try {
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing data for user login");
            } else if (!request.getEmail().matches(emailRegex)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Wrong email format");
            } else if (!request.getPassword().matches(passwordRegex)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Password has to be at least 8 characters long, with an uppercase,lowercase and a number");
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(authenticationService.authenticate(request));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        try {
            if (email == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing data for password recovery");
            } else if (!email.matches(emailRegex)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Wrong email format");
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(passwordRecoveryService.forgotPassword(email));
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/changeForgottenPassword")
    public ResponseEntity<String> changeForgottenPassword(@RequestBody PasswordRecoveryDTO passwordRecoveryDTO) {
        try {
            if (passwordRecoveryDTO.getEmail() == null || passwordRecoveryDTO.getNewPassword() == null || passwordRecoveryDTO.getPin() == null || passwordRecoveryDTO.getPin().isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing data for changing password");
            } else if (!passwordRecoveryDTO.getEmail().matches(emailRegex)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Wrong email format");
            } else if (!passwordRecoveryDTO.getNewPassword().matches(passwordRegex)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Password has to be at least 8 characters long, with an uppercase,lowercase and a number");
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(passwordRecoveryService.changeForgottenPassword(passwordRecoveryDTO));
            }
        } catch (UserNotFoundException | NoPinCreatedForUserException | PinExpiredException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/addReservation")
    public ResponseEntity<String> addReservation(@RequestBody NewReservationRequestDTO newReservationRequestDTO) {
        LocalDate actualDate = LocalDate.now();
        if (newReservationRequestDTO.getCapacity() == null) {
            System.out.println("CASO 1");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing capacity to create a reservation");
        } else if (newReservationRequestDTO.getCapacity() <= 0 || newReservationRequestDTO.getCapacity() > barService.getBarConfiguration().getCapacity()) {
            System.out.println("CASO 2");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("CANNOT have the capacity under 1 or higher than maximum capacity");
        } else if (Objects.equals(newReservationRequestDTO.getClientPhone(), "") && Objects.equals(newReservationRequestDTO.getClientEmail(), "")) {
            System.out.println("CASO 3");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing phone or email to create a reservation");
        } else if (newReservationRequestDTO.getShift() == null || newReservationRequestDTO.getReservationDate() == null  || newReservationRequestDTO.getComment() == null || (newReservationRequestDTO.getClientPhone() == null && newReservationRequestDTO.getClientEmail() == null)) {
            System.out.println("CASO 4");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing information to add a reservation");
        } else if (newReservationRequestDTO.getClientEmail() != null && (!newReservationRequestDTO.getClientEmail().isEmpty() && !newReservationRequestDTO.getClientEmail().matches(emailRegex))) {
            System.out.println("CASO 5");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Wrong email format");
        } else if (newReservationRequestDTO.getClientPhone() != null && (!(newReservationRequestDTO.getClientPhone().isEmpty()) && !newReservationRequestDTO.getClientPhone().matches(phoneRegex))) {
            System.out.println("CASO 6");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Wrong phone format");
        } else if (!newReservationRequestDTO.getComment().matches(commentRegex)) {
            System.out.println("CASO 7");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Wrong comment format");
        } else if (newReservationRequestDTO.getReservationDate().isBefore(actualDate)) {
            System.out.println("CASO 8");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("CANNOT use a date from the past");
        } else {
            try {
                System.out.println("CASO 9");
                return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.addReservation(newReservationRequestDTO));
            } catch (BarCapacityExceededException e) {
                System.out.println("CASO 10");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } catch (Exception e) {
                System.out.println("CASO 11");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }

    }

    @GetMapping("/getAllShifts")
    public ResponseEntity<?> getAllShifts() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(shiftService.getAllShifts());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.status(HttpStatus.OK).body("Server is up!");
    }


}

