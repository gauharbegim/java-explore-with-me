package ru.practicum.ewmservice.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.comments.entity.DislikeEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface DislikeRepository extends JpaRepository<DislikeEntity, Long> {
    Boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    Optional<DislikeEntity> getByCommentIdAndUserId(Long commentId, Long userId);

    Integer countAllByCommentId(Long commentId);

    void deleteAllByCommentId(Long commentId);

    List<DislikeEntity> findAllByCommentIdIn(List<Long> comments);
}
