package ru.practicum.httpservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.httpservice.service.StatService;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitOutputDto;
import ru.practicum.dto.StatDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class HttpServiceController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public HitOutputDto saveHit(@Valid @RequestBody HitDto hit) {
        return statService.saveHit(hit);
    }

    @GetMapping("/stats")
    public List<StatDto> getStat(@RequestParam(name = "start")
                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                 @RequestParam(name = "end")
                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                 @RequestParam(name = "uris", defaultValue = "") List<String> uris,
                                 @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return statService.getStat(start, end, uris, unique);
    }
}
