package com.messismo.bar.DTOs;

import com.messismo.bar.Entities.Shift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewReservationRequestDTO {

    private Integer capacity;

    private Shift shift;

    private LocalDate startingDate;

    private LocalDate finishingDate;

    private String clientEmail;

    private String clientPhone;

    private String comment;

}

