package ru.practicum.ewmservice.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.entity.LocationEntity;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    Optional<LocationEntity> findByLatAndLon(Float lat, Float lon);
}
