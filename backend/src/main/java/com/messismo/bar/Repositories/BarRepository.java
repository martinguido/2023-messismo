package com.messismo.bar.Repositories;

import com.messismo.bar.Entities.Bar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarRepository extends JpaRepository<Bar, Long> {
}
