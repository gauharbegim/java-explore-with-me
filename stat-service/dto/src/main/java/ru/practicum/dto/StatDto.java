package ru.practicum.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class StatDto {
    private final String app;
    private final String uri;
    private final Integer hits;
}
