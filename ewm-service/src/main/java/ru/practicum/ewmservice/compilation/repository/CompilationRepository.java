package ru.practicum.ewmservice.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.compilation.entity.CompilationEntity;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> {
    List<CompilationEntity> findAllByPinned(Boolean pinned, Pageable pageable);
}
