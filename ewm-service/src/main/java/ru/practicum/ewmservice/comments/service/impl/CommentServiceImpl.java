package ru.practicum.ewmservice.comments.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.comments.mapper.CommentMapper;
import ru.practicum.ewmservice.comments.dto.CommentDto;
import ru.practicum.ewmservice.comments.dto.NewCommentDto;
import ru.practicum.ewmservice.comments.dto.UpdateCommentRequest;
import ru.practicum.ewmservice.comments.entity.CommentEntity;
import ru.practicum.ewmservice.comments.entity.DislikeEntity;
import ru.practicum.ewmservice.comments.entity.LikeEntity;
import ru.practicum.ewmservice.comments.repository.CommentRepository;
import ru.practicum.ewmservice.comments.repository.DislikeRepository;
import ru.practicum.ewmservice.comments.repository.LikeRepository;
import ru.practicum.ewmservice.comments.service.CommentService;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.service.EventService;
import ru.practicum.ewmservice.exception.ForbiddenException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.user.entity.UserEntity;
import ru.practicum.ewmservice.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final EventService eventService;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final LikeRepository likesRepository;
    private final DislikeRepository dislikeRepository;

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        UserEntity user = userService.getUserById(userId);
        EventEntity event = eventService.getEventById(eventId);

        CommentEntity comment = CommentMapper.toComment(newCommentDto);

        comment.setEvent(event);
        comment.setAuthor(user);

        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        commentDto.setLikesCount(likesRepository.countAllByCommentId(comment.getId()));
        commentDto.setDislikesCount(dislikeRepository.countAllByCommentId(comment.getId()));

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getComment(Long commentId) {
        CommentEntity comment = getCommentById(commentId);

        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        commentDto.setLikesCount(likesRepository.countAllByCommentId(commentId));
        commentDto.setDislikesCount(dislikeRepository.countAllByCommentId(commentId));
        return commentDto;
    }

    @Override
    public void addLikeToComment(Long userId, Long commentId) {
        CommentEntity comment = getCommentById(commentId);
        UserEntity user = userService.getUserById(userId);

        if (comment.getAuthor().equals(user)) {
            throw new ForbiddenException("Автор не может лайкать свой коментарий");
        }

        if (likesRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            throw new ForbiddenException("От пользователь на коментарий есть существующий лайк");
        }

        if (dislikeRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            deleteDislike(userId, commentId);
        }

        LikeEntity commentLike = LikeEntity.builder()
                .commentId(commentId)
                .userId(user.getId())
                .createdOn(LocalDateTime.now())
                .build();

        likesRepository.save(commentLike);
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        CommentEntity comment = getCommentById(commentId);
        UserEntity user = userService.getUserById(userId);

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ForbiddenException("Only comment author can edit comment");
        }

        if (updateCommentRequest.getCommentText() != null && !updateCommentRequest.getCommentText().isBlank()) {
            comment.setCommentText(updateCommentRequest.getCommentText());
        }

        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        commentDto.setLikesCount(likesRepository.countAllByCommentId(commentId));
        commentDto.setDislikesCount(dislikeRepository.countAllByCommentId(commentId));

        return commentDto;
    }

    @Override
    public void deleteLike(Long userId, Long commentId) {
        getCommentById(commentId);
        userService.getUserById(userId);
        LikeEntity like = getLikeByCommentIdAndUserId(commentId, userId);

        likesRepository.deleteById(like.getId());
    }

    @Override
    public void addDislikeToComment(Long userId, Long commentId) {
        CommentEntity comment = getCommentById(commentId);
        UserEntity user = userService.getUserById(userId);

        if (comment.getAuthor().equals(user)) {
            throw new ForbiddenException("Автор не может дизлайкать свой коментарий");
        }

        if (dislikeRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            throw new ForbiddenException("От пользователь на коментарий есть существующий дизлайк");
        }

        if (likesRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            deleteLike(userId, commentId);
        }

        DislikeEntity commentDislike = DislikeEntity.builder()
                .commentId(commentId)
                .userId(user.getId())
                .createdOn(LocalDateTime.now())
                .build();

        dislikeRepository.save(commentDislike);
    }

    @Override
    public void deleteDislike(Long userId, Long commentId) {
        getCommentById(commentId);
        userService.getUserById(userId);
        DislikeEntity dislike = getDislikeByCommentIdAndUserId(commentId, userId);

        dislikeRepository.deleteById(dislike.getId());
    }

    @Override
    public List<CommentDto> getEventAllComments(Long eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        eventService.getEventById(eventId);

        List<CommentEntity> comments = commentRepository.findAllByEventId(eventId, pageable);
        List<CommentDto> commentDtos = CommentMapper.toCommentDto(comments);

        List<Long> commentIds = commentDtos.stream()
                .map(CommentDto::getId)
                .collect(Collectors.toList());

        Map<Long, List<LikeEntity>> commentLikesMap = likesRepository.findAllByCommentIdIn(commentIds)
                .stream()
                .collect(Collectors.groupingBy(LikeEntity::getCommentId));

        Map<Long, List<DislikeEntity>> commentDislikesMap = dislikeRepository.findAllByCommentIdIn(commentIds)
                .stream()
                .collect(Collectors.groupingBy(DislikeEntity::getCommentId));

        for (CommentDto commentDto : commentDtos) {
            Long commentId = commentDto.getId();

            List<LikeEntity> commentLikes = commentLikesMap.getOrDefault(commentId, Collections.emptyList());
            Integer likesCount = commentLikes.size();

            List<DislikeEntity> commentDislikes = commentDislikesMap.getOrDefault(commentId, Collections.emptyList());
            Integer dislikesCount = commentDislikes.size();

            commentDto.setLikesCount(likesCount);
            commentDto.setDislikesCount(dislikesCount);
        }

        return commentDtos;
    }

    @Override
    public Long getEventCommentsCount(Long eventId) {
        eventService.getEventById(eventId);
        return commentRepository.countByEventId(eventId);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        CommentEntity comment = getCommentById(commentId);
        UserEntity user = userService.getUserById(userId);

        if (!comment.getAuthor().equals(user)) {
            throw new ForbiddenException("No ability to delete comment.");
        }

        likesRepository.deleteAllByCommentId(commentId);
        dislikeRepository.deleteAllByCommentId(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        getCommentById(commentId);

        likesRepository.deleteAllByCommentId(commentId);
        dislikeRepository.deleteAllByCommentId(commentId);
        commentRepository.deleteById(commentId);
    }

    private CommentEntity getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Коментарий с таким id не найден."));
    }

    private LikeEntity getLikeByCommentIdAndUserId(Long commentId, Long userId) {
        return likesRepository.getByCommentIdAndUserId(commentId, userId).orElseThrow(() ->
                new NotFoundException("Лайк с таким id не найден."));
    }

    private DislikeEntity getDislikeByCommentIdAndUserId(Long commentId, Long userId) {
        return dislikeRepository.getByCommentIdAndUserId(commentId, userId).orElseThrow(() ->
                new NotFoundException("Дизлайк с таким id не найден."));
    }
}
