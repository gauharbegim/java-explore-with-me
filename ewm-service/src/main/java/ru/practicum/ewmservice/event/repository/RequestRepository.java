package ru.practicum.ewmservice.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.dto.RequestStats;
import ru.practicum.ewmservice.event.entity.RequestEntity;
import ru.practicum.ewmservice.event.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    List<RequestEntity> findAllByRequesterId(Long requesterId);

    Optional<RequestEntity> findByEventIdAndRequesterId(Long eventId, Long userId);

    List<RequestEntity> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<RequestEntity> findAllByEventId(Long eventId);

    List<RequestEntity> findAllByIdIn(List<Long> requestIds);

    @Query("SELECT new ru.practicum.ewmservice.event.dto.RequestStats(r.event.id, count(r.id)) " +
            "FROM RequestEntity AS r " +
            "WHERE r.event.id IN ?1 " +
            "AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id")
    List<RequestStats> getConfirmedRequests(List<Long> eventsId);
}
