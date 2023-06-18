package ru.practicum.ewmservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmservice.constants.SystemConstats;
import ru.practicum.ewmservice.event.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private Long requester;

    @JsonFormat(pattern = SystemConstats.DT_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime created;

    private RequestStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipationRequestDto that = (ParticipationRequestDto) o;
        return Objects.equals(id, that.id) && Objects.equals(event, that.event) &&
                Objects.equals(requester, that.requester) && Objects.equals(created, that.created) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, requester, created, status);
    }
}
