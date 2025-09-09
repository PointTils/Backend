package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.User;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    
    Location findByUser(User user);
    
    Location findByUf(String uf);
    
    Location findByUfAndCity(String uf, String city);
}
