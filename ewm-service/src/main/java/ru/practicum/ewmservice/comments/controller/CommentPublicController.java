package ru.practicum.ewmservice.comments.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.comments.dto.CommentDto;
import ru.practicum.ewmservice.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/events/{eventId}/comments")
@AllArgsConstructor
public class CommentPublicController {

    private CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getEventAllComments(@PathVariable Long eventId,
                                                @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Valid @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getEventAllComments(eventId, from, size);
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    public Long getEventCommentsCnt(@PathVariable Long eventId) {
        return commentService.getEventCommentsCount(eventId);
    }
}