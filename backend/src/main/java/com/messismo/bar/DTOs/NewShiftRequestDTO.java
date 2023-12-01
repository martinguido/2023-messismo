package com.messismo.bar.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewShiftRequestDTO {

    private LocalTime startingHour;

    private LocalTime finishingHour;
}
