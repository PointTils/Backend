package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.User;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    List<Location> findByUser(User user);
    
    List<Location> findByUf(String uf);
    
    List<Location> findByUfAndCity(String uf, String city);
}
