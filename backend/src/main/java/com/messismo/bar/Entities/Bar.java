package com.messismo.bar.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "bars")
public class Bar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bar_id", nullable = false)
    private Long barId;

    @Column(name = "capacity")
    private Integer capacity;


    public Bar(Integer capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        } else {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
    }

    public void updateCapacity(Integer newCapacity) {
        if (newCapacity > 0) {
            this.capacity = newCapacity;
        } else {
            throw new IllegalArgumentException("New capacity must be greater than 0");
        }
    }
}
