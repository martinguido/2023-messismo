package com.messismo.bar.Repositories;

import com.messismo.bar.Entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
