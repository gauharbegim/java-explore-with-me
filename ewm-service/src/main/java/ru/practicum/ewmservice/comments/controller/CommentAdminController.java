package ru.practicum.ewmservice.comments.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.comments.dto.CommentDto;
import ru.practicum.ewmservice.comments.service.CommentService;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/comments/{commentId}")
@AllArgsConstructor
public class CommentAdminController {

    private CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(@PathVariable Long commentId) {
        return commentService.getComment(commentId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}
