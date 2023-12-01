package com.messismo.bar.Controllers;

import com.messismo.bar.DTOs.AuthenticationRequestDTO;
import com.messismo.bar.DTOs.NewReservationRequestDTO;
import com.messismo.bar.DTOs.PasswordRecoveryDTO;
import com.messismo.bar.DTOs.RegisterRequestDTO;
import com.messismo.bar.Exceptions.*;
import com.messismo.bar.Services.AuthenticationService;
import com.messismo.bar.Services.BarService;
import com.messismo.bar.Services.PasswordRecoveryService;
import com.messismo.bar.Services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final PasswordRecoveryService passwordRecoveryService;

    private final BarService barService;

    private final ReservationService reservationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            if (request.getEmail() == null || request.getPassword() == null || request.getUsername() == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing data for user registration");
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
            return ResponseEntity.status(HttpStatus.OK).body(passwordRecoveryService.forgotPassword(email));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/changeForgottenPassword")
    public ResponseEntity<String> changeForgottenPassword(@RequestBody PasswordRecoveryDTO passwordRecoveryDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(passwordRecoveryService.changeForgottenPassword(passwordRecoveryDTO));
        } catch (UserNotFoundException | NoPinCreatedForUserException | PinExpiredException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/addReservation")
    public ResponseEntity<String> addReservation(@RequestBody NewReservationRequestDTO newReservationRequestDTO){
        if(newReservationRequestDTO.getCapacity()==null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing capacity to create a reservation");
        }
        else if(newReservationRequestDTO.getCapacity()<=0 || newReservationRequestDTO.getCapacity()>barService.getBarConfiguration().getCapacity()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("CANNOT have the capacity under 1 or higher than maximum capacity");
        }
        else if( newReservationRequestDTO.getShift()==null || newReservationRequestDTO.getStartingDate() == null || newReservationRequestDTO.getFinishingDate() == null || newReservationRequestDTO.getComment() == null || (newReservationRequestDTO.getClientPhone()==null && newReservationRequestDTO.getClientEmail()==null)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing information to add a reservation");
        }
        else{
            try{
                return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.addReservation(newReservationRequestDTO));
            }catch(BarCapacityExceededException | ReservationStartingDateMustBeBeforeFinishinDateException e){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }

    }




    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.status(HttpStatus.OK).body("Server is up!");
    }


}

