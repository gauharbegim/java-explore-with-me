package ru.practicum.ewmservice.comments.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.comments.dto.CommentDto;
import ru.practicum.ewmservice.comments.dto.NewCommentDto;
import ru.practicum.ewmservice.comments.dto.UpdateCommentRequest;
import ru.practicum.ewmservice.comments.service.CommentService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/comments")
@AllArgsConstructor
public class CommentPrivateController {

    private CommentService commentService;

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PostMapping("/{commentId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLikeToComment(@PathVariable Long userId,
                                 @PathVariable Long commentId) {
        commentService.addLikeToComment(userId, commentId);
    }

    @DeleteMapping("/{commentId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long userId,
                           @PathVariable Long commentId) {
        commentService.deleteLike(userId, commentId);
    }

    @PostMapping("/{commentId}/dislike")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDislikeToComment(@PathVariable Long userId,
                                    @PathVariable Long commentId) {
        commentService.addDislikeToComment(userId, commentId);
    }


    @DeleteMapping("/{commentId}/dislike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDislike(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentService.deleteDislike(userId, commentId);
    }


    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @Valid @RequestBody UpdateCommentRequest updateCommentRequest) {
        return commentService.updateComment(userId, commentId, updateCommentRequest);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable Long userId,
                                    @PathVariable Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }
}
