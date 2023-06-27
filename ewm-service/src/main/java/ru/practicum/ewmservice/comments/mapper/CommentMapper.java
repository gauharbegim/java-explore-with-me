package ru.practicum.ewmservice.comments.mapper;

import ru.practicum.ewmservice.comments.dto.CommentDto;
import ru.practicum.ewmservice.comments.dto.NewCommentDto;
import ru.practicum.ewmservice.comments.entity.CommentEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static CommentEntity toComment(NewCommentDto newCommentDto) {
        return CommentEntity.builder()
                .commentText(newCommentDto.getCommentText())
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(CommentEntity comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .commentText(comment.getCommentText())
                .createdOn(comment.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }

    public static List<CommentDto> toCommentDto(Iterable<CommentEntity> comments) {
        List<CommentDto> result = new ArrayList<>();

        for (CommentEntity comment : comments) {
            result.add(toCommentDto(comment));
        }

        return result;
    }
}
