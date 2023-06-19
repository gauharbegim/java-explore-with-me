package ru.practicum.ewmservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.constants.SystemConstats;
import ru.practicum.ewmservice.user.dto.UserShortDto;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDto {
    String annotation;
    CategoryDto category;
    Long confirmedRequests;

    @JsonFormat(pattern = SystemConstats.DT_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;

    Long id;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long views;
}