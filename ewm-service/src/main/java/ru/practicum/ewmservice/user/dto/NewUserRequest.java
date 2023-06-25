package ru.practicum.ewmservice.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmservice.constants.SystemConstats;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserRequest {
    @Email
    @NotNull
    @NotBlank
    @Size(min = SystemConstats.MIN_LENGTH_EMAIL, max = SystemConstats.MAX_LENGTH_EMAIL)
    String email;

    Long id;

    @NotNull
    @NotBlank
    @Size(min = SystemConstats.MIN_LENGTH_USER_NAME, max = SystemConstats.MAX_LENGTH_USER_NAME)
    String name;
}
