package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.EventFullDto;
import ru.practicum.explorewithme.EventShortDto;
import ru.practicum.explorewithme.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/event")
    public List<EventShortDto> getEvents(@RequestParam() String text,
                                         @RequestParam() Integer[] categories,
                                         @RequestParam() Boolean paid,
                                         @RequestParam() String rangeStart,
                                         @RequestParam() String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam() String sort,
                                         @RequestParam() Integer from,
                                         @RequestParam() Integer size,
                                         HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, ip);
    }

    @GetMapping("/event/{id}")
    public EventFullDto getEvent(@PathVariable("id") Integer id,
                                 HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return eventService.getEvent(id, ip);
    }
}
