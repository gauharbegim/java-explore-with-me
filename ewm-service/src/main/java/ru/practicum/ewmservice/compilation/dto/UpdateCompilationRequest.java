package ru.practicum.ewmservice.compilation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmservice.constants.SystemConstats;

import javax.validation.constraints.Size;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UpdateCompilationRequest {
    @Size(min = SystemConstats.MIN_UPDATECOMPILATIONREQUEST_LENGTH_TITLE, max = SystemConstats.MAX_UPDATECOMPILATIONREQUEST_LENGTH_TITLE)
    String title;

    Boolean pinned;

    List<Long> events;
}