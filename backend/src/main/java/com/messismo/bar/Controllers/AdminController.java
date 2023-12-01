package com.messismo.bar.Controllers;

import com.messismo.bar.DTOs.DeleteShiftRequestDTO;
import com.messismo.bar.DTOs.ModifyBarCapacityRequestDTO;
import com.messismo.bar.DTOs.NewShiftRequestDTO;
import com.messismo.bar.DTOs.UserIdDTO;
import com.messismo.bar.Exceptions.*;
import com.messismo.bar.Services.BarService;
import com.messismo.bar.Services.ShiftService;
import com.messismo.bar.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@CrossOrigin("*")
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    private final BarService barService;

    private final ShiftService shiftService;

    @PutMapping("/validateAdmin")
    public ResponseEntity<?> validateAdmin(@RequestBody UserIdDTO userIdDTO) {
        if (userIdDTO.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing userId to upgrade to manager");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.validateEmployee(userIdDTO));
        } catch (UsernameNotFoundException | CannotUpgradeToManager e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/modifyBarCapacity")
    public ResponseEntity<String> modifyBarCapacity(@RequestBody ModifyBarCapacityRequestDTO modifyBarCapacityRequestDTO) {
        if (modifyBarCapacityRequestDTO.getBarId() == null || modifyBarCapacityRequestDTO.getNewCapacity() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing information to modify bar capacity");
        } else {
            try {
                return ResponseEntity.status(HttpStatus.OK).body(barService.modifyBarCapacity(modifyBarCapacityRequestDTO));
            } catch (BarNotFoundException | AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @GetMapping("/getBarConfiguration")
    public ResponseEntity<?> getBarConfiguration() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(barService.getBarConfiguration());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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


    @PostMapping("/addShift")
    public ResponseEntity<String> addShift(@RequestBody NewShiftRequestDTO newShiftRequestDTO) {
        if (newShiftRequestDTO.getStartingHour() == null || newShiftRequestDTO.getFinishingHour() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing information to create a shift");
        } else if (newShiftRequestDTO.getStartingHour().isAfter(newShiftRequestDTO.getFinishingHour()) || newShiftRequestDTO.getStartingHour().equals(newShiftRequestDTO.getFinishingHour())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Starting time must be before finishing time");
        } else {
            try {
                return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.addShift(newShiftRequestDTO));
            } catch (CannotCreateShiftInBetweenOtherShiftException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }


    @DeleteMapping("/deleteShift")
    public ResponseEntity<String> deleteShift(@RequestBody DeleteShiftRequestDTO deleteShiftRequestDTO) {
        if (deleteShiftRequestDTO.getShiftId() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing information to delete a shift");
        } else {
            try {
                return ResponseEntity.status(HttpStatus.OK).body(shiftService.deleteShift(deleteShiftRequestDTO));
            } catch (ShiftNotFoundException | CannotDeleteAShiftWithReservationsException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }
}