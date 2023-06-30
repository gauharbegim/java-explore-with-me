package ru.practicum.ewmservice.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.comments.entity.LikeEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    Optional<LikeEntity> getByCommentIdAndUserId(Long commentId, Long userId);

    Integer countAllByCommentId(Long commentId);

    void deleteAllByCommentId(Long commentId);

    List<LikeEntity> findAllByCommentIdIn(List<Long> comments);
}
