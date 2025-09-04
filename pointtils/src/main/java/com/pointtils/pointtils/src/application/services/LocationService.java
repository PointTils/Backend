package com.pointtils.pointtils.src.application.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.mapper.LocationMapper;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.infrastructure.repositories.LocationRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {
    
    private final LocationRepository locationRepository;
    
    public Location saveLocation(LocationDTO locationDTO, User user) {
        Location location = LocationMapper.toDomain(locationDTO, user);
        return locationRepository.save(location);
    }
    
    public List<Location> findByUser(User user) {
        return locationRepository.findByUser(user);
    }
    
    public LocationDTO getPrimaryLocationForUser(User user) {
        List<Location> locations = findByUser(user);
        return LocationMapper.toDto(locations);
    }
    
    public void updateUserLocation(User user, LocationDTO locationDTO) {
        List<Location> existingLocations = findByUser(user);
        locationRepository.deleteAll(existingLocations);
        
        if (locationDTO != null) {
            saveLocation(locationDTO, user);
        }
    }
}
