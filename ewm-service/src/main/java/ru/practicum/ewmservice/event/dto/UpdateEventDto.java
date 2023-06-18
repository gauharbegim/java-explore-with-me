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
import ru.practicum.ewmservice.event.enums.EventStateAction;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UpdateEventDto {
    @Size(min = SystemConstats.MIN_LENGTH_ANNOTATION, max = SystemConstats.MAX_LENGTH_ANNOTATION)
    String annotation;

    Long category;

    @Size(min = SystemConstats.MIN_LENGTH_DESCRIPTION, max = SystemConstats.MAX_LENGTH_DESCRIPTION)
    String description;

    @JsonFormat(pattern = SystemConstats.DT_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;

    @Valid
    LocationDto location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;

    EventStateAction stateAction;

    @Size(min = SystemConstats.MIN_LENGTH_TITLE, max = SystemConstats.MAX_LENGTH_TITLE)
    String title;
}
