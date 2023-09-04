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

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final StatService statService;

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestHeader(USER_HEADER) Integer userId,
                                       @RequestParam() String start,
                                       @RequestParam() String end,
                                       @RequestParam(required = false) String uris,
                                       @RequestParam(defaultValue = "false", required = false) String unique) {
        return statService.getStats(userId, start, end, uris, unique);
    }

    @PostMapping("/hit")
    public EndpointHitDto createHit(@RequestHeader(USER_HEADER) Integer userId,
                                    @Valid @RequestBody EndpointHitDto endpointHitDto) {
        return statService.createHit(userId, endpointHitDto);
    }
}
