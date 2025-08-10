package com.pointtils.pointtils.src.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Point;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
}
