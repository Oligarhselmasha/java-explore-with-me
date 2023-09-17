package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.service.StatService;
import ru.practicum.explorewithme.stats.EndpointHitDto;
import ru.practicum.explorewithme.stats.ViewStatsDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam() String start,
                                       @RequestParam() String end,
                                       @RequestParam(required = false) String uris,
                                       @RequestParam(defaultValue = "false", required = false) Boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto createHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        return statService.createHit(endpointHitDto);
    }
}
