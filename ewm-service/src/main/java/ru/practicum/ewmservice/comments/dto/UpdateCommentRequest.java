package ru.practicum.ewmservice.comments.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentRequest {
    @NotBlank
    @Size(max = 1000, min = 3)
    private String commentText;
}
