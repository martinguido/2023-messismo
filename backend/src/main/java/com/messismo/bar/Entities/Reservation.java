package com.messismo.bar.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @Column(name = "starting_date")
    private LocalDateTime startingDate;

    @Column(name = "finishing_date")
    private LocalDateTime finishingDate;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_phone")
    private Integer clientPhone;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "comment")
    private String comment;


    public Reservation(Shift shift, LocalDateTime startingDate, LocalDateTime finishingDate, String clientEmail, Integer capacity, String comment) {
        this.shift = shift;
        this.startingDate = startingDate;
        this.finishingDate = finishingDate;
        this.clientEmail = clientEmail;
        this.capacity = capacity;
        this.comment = comment;
    }

    public Reservation(Shift shift, LocalDateTime startingDate, LocalDateTime finishingDate, Integer clientPhone, Integer capacity, String comment) {
        this.shift = shift;
        this.startingDate = startingDate;
        this.finishingDate = finishingDate;
        this.clientPhone = clientPhone;
        this.capacity = capacity;
        this.comment = comment;
    }

    public Reservation(Shift shift, LocalDateTime startingDate, LocalDateTime finishingDate, String clientEmail, Integer clientPhone, Integer capacity, String comment) {
        this.shift = shift;
        this.startingDate = startingDate;
        this.finishingDate = finishingDate;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.capacity = capacity;
        this.comment = comment;
    }

}
