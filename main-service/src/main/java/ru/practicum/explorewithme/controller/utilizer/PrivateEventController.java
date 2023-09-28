package ru.practicum.explorewithme.controller.utilizer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.events.EventFullDto;
import ru.practicum.explorewithme.dto.events.EventShortDto;
import ru.practicum.explorewithme.dto.events.NewEventDto;
import ru.practicum.explorewithme.dto.requests.UpdateEventUserRequest;
import ru.practicum.explorewithme.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUsersEvents(@PathVariable("userId") Long userId,
                                              @RequestParam(defaultValue = "0", required = false) Integer from,
                                              @RequestParam(defaultValue = "10", required = false) Integer size) {
        return eventService.getUsersEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postNewEvent(@PathVariable("userId") Long userId,
                                     @Valid @RequestBody NewEventDto newEventDto
    ) {
        return eventService.postNewUsersEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUsersEvent(@PathVariable("userId") Long userId,
                                      @PathVariable("eventId") Long eventId) {
        return eventService.getUsersEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto changeUsersEvent(@PathVariable("userId") Long userId,
                                         @PathVariable("eventId") Long eventId,
                                         @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest
    ) {
        return eventService.changeUsersEvent(userId, eventId, updateEventUserRequest);
    }
}
