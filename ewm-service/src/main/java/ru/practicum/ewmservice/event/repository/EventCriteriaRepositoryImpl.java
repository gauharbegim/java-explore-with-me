package ru.practicum.ewmservice.event.repository;

import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.enums.EventState;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class EventCriteriaRepositoryImpl implements EventCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<EventEntity> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventEntity> query = builder.createQuery(EventEntity.class);
        Root<EventEntity> root = query.from(EventEntity.class);
        Predicate criteria = builder.conjunction();

        if (users != null && !users.isEmpty()) {
            criteria = builder.and(criteria, root.get("initiator").in(users));
        }

        if (states != null && !states.isEmpty()) {
            criteria = builder.and(criteria, root.get("state").in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            criteria = builder.and(criteria, root.get("category").in(categories));
        }

        if (rangeStart != null) {
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        query.select(root).where(criteria);
        return entityManager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }

    public List<EventEntity> getEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, Integer from, Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventEntity> query = builder.createQuery(EventEntity.class);
        Root<EventEntity> root = query.from(EventEntity.class);
        Predicate criteria = builder.conjunction();

        if (text != null && !text.isBlank()) {
            Predicate annotation = builder.like(builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%");
            Predicate description = builder.like(builder.lower(root.get("description")), "%" + text.toLowerCase() + "%");
            criteria = builder.and(criteria, builder.or(annotation, description));
        }

        if (categories != null && !categories.isEmpty()) {
            criteria = builder.and(criteria, root.get("category").in(categories));
        }

        if (paid != null) {
            criteria = builder.and(criteria, root.get("paid").in(paid));
        }

        if (rangeStart == null && rangeEnd == null) {
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.now()));
        } else {
            if (rangeStart != null) {
                criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }

            if (rangeEnd != null) {
                criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }
        }

        criteria = builder.and(criteria, root.get("state").in(EventState.PUBLISHED));

        query.select(root).where(criteria);
        return entityManager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }
}
