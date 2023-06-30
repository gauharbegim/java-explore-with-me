package ru.practicum.ewmservice.comments.service;

import ru.practicum.ewmservice.comments.dto.CommentDto;
import ru.practicum.ewmservice.comments.dto.NewCommentDto;
import ru.practicum.ewmservice.comments.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    void addLikeToComment(Long userId, Long commentId);

    void addDislikeToComment(Long userId, Long commentId);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest updateCommentRequest);

    CommentDto getComment(Long commentId);

    List<CommentDto> getEventAllComments(Long eventId, Integer from, Integer size);

    Long getEventCommentsCount(Long eventId);

    void deleteCommentByUser(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    void deleteLike(Long userId, Long commentId);

    void deleteDislike(Long userId, Long commentId);
}
