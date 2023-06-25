package ru.practicum.ewmservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmservice.constants.SystemConstats;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class NewEventDto {
    @NotBlank
    @Size(min = SystemConstats.MIN_LENGTH_ANNOTATION, max = SystemConstats.MAX_LENGTH_ANNOTATION)
    String annotation;

    @NotNull
    Long category;

    @NotBlank
    @NotEmpty
    @Size(min = SystemConstats.MIN_LENGTH_DESCRIPTION, max = SystemConstats.MAX_LENGTH_DESCRIPTION)
    String description;

    @NotNull
    @JsonFormat(pattern = SystemConstats.DT_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;

    @NotNull
    @Valid
    LocationDto location;

    Boolean paid = false;

    @PositiveOrZero
    Integer participantLimit = 0;

    Boolean requestModeration = true;

    @NotBlank
    @Size(min = SystemConstats.MIN_LENGTH_TITLE, max = SystemConstats.MAX_LENGTH_TITLE)
    String title;
}