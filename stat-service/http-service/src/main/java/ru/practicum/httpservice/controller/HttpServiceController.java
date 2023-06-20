package ru.practicum.httpservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;
import ru.practicum.httpservice.service.StatService;
import ru.practicum.dto.ViewStats;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class HttpServiceController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHit endpointHit) {
        statService.saveHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStat(@RequestParam(name = "start")
                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @RequestParam(name = "end")
                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(name = "uris", defaultValue = "") List<String> uris,
                                   @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return statService.getStat(start, end, uris, unique);
    }
}
