package ru.practicum.explorewithme.controller.common;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.events.EventFullDto;
import ru.practicum.explorewithme.dto.events.EventShortDto;
import ru.practicum.explorewithme.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @RequestParam(required = false, defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, ip);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEvent(@PathVariable("id") Long id,
                                 HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return eventService.getEvent(id, ip);
    }
}
