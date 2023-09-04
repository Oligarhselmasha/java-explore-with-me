package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.EndpointHitDto;
import ru.practicum.explorewithme.ViewStatsDto;
import ru.practicum.explorewithme.service.StatService;

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
                                       @RequestParam(defaultValue = "false" ,required = false) Boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public EndpointHitDto createHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        return statService.createHit(endpointHitDto);
    }
}
