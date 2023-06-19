package ru.practicum.ewmservice.category.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmservice.constants.SystemConstats;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class NewCategoryDto {
    @NotBlank
    @Size(min = SystemConstats.MIN_LENGTH_CATEGORY_NAME, max = SystemConstats.MAX_LENGTH_CATEGORY_NAME)
    String name;
}