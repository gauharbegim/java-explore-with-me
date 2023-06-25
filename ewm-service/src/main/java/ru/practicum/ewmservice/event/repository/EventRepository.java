package ru.practicum.ewmservice.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.enums.EventState;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>, EventCriteriaRepository {
    List<EventEntity> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<EventEntity> findByIdAndInitiatorId(Long eventId, Long userId);

    List<EventEntity> findAllByIdIn(List<Long> eventsId);

    EventEntity findByIdAndState(Long eventId, EventState state);
}
