package com.messismo.bar.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;


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

    @Column(name = "reservation_date")
    private LocalDate reservationDate;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_phone")
    private String clientPhone;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "comment")
    private String comment;

    @Column(name = "state")
    private String state;

    @Column(name = "used")
    private Boolean used;


    public Reservation(Shift shift, LocalDate reservationDate, String clientEmail, String clientPhone, Integer capacity, String comment) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        if (clientPhone == null && clientEmail == null) {
            throw new IllegalArgumentException("Missing phone and email");
        }
        if (clientPhone != null) {
            if (Integer.parseInt(clientPhone) < 0) {
                throw new IllegalArgumentException("Phone must be greater than 0");
            } else {
                this.clientPhone = clientPhone;
            }
        }
        if (clientEmail != null) {
            if (!clientEmail.isEmpty()) {
                this.clientEmail = clientEmail;
            }
        }
        this.shift = shift;
        this.reservationDate = reservationDate;
        this.capacity = capacity;
        this.comment = comment;
        this.state = "Upcoming";
        this.used = false;
    }

    public void setAsUsed(){
        this.used = true;
    }

    public void updateToInProcessState() {
        this.state = "In Process";
    }

    public void updateToExpiredState() {
        this.state = "Expired";
    }

    public void updateToUpcoming() {
        this.state = "Upcoming";
    }

    public Boolean isExpired(){
        return Objects.equals(this.state, "Expired");
    }

}
