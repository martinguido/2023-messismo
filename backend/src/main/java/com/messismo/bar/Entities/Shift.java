package com.messismo.bar.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shift_id", nullable = false)
    private Long shiftId;

    @Column(name = "starting_hour")
    private LocalTime startingHour;

    @Column(name = "finishingHour")
    private LocalTime finishingHour;

    public Shift(LocalTime startingHour, LocalTime finishingHour) {
        this.startingHour = startingHour;
        this.finishingHour = finishingHour;
    }


}
