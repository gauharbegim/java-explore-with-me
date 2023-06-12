package ru.practicum.httpservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.httpservice.entity.AppEntity;

import java.util.Optional;

@Repository
public interface AppRepository extends JpaRepository<AppEntity, Long> {
    Optional<AppEntity> getByName(String name);
}
