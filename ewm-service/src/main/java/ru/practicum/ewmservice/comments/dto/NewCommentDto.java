package ru.practicum.ewmservice.comments.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class NewCommentDto {
    @NotBlank
    @Size(max = 1000, min = 3)
    private String commentText;
}
