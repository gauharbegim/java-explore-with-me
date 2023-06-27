package ru.practicum.ewmservice.comments.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CommentDto {

    @NotNull
    private Long id;

    @NotBlank
    private String authorName;

    @NotBlank
    private String commentText;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private String createdOn;

    private Integer likesCount;
    private Integer dislikesCount;
}
