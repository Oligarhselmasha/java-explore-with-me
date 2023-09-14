package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.EventFullDto;
import ru.practicum.explorewithme.events.EventShortDto;
import ru.practicum.explorewithme.events.NewEventDto;
import ru.practicum.explorewithme.requests.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.requests.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.requests.ParticipationRequestDto;
import ru.practicum.explorewithme.requests.UpdateEventUserRequest;
import ru.practicum.explorewithme.service.EventService;

import javax.servlet.http.HttpServletRequest;
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
                                              @RequestParam(defaultValue = "10", required = false) Integer size,
                                              HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return eventService.getUsersEvents(userId, from, size, ip);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto postNewEvent(@PathVariable("userId") Long userId,
                                     @RequestBody NewEventDto newEventDto,
                                     HttpServletRequest request
    ) {
        String ip = request.getRemoteAddr();
        return eventService.postNewUsersEvent(userId, newEventDto, ip);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUsersEvent(@PathVariable("userId") Long userId,
                                      @PathVariable("eventId") Long eventId,
                                      HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return eventService.getUsersEvent(userId, eventId, ip);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto changeUsersEvent(@PathVariable("userId") Long userId,
                                         @PathVariable("eventId") Long eventId,
                                         @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest
    ) {
        return eventService.changeUsersEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUsersEventRequests(@PathVariable("userId") Long userId,
                                                         @PathVariable("eventId") Long eventId) {
        return eventService.getUsersEventRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeUsersEventRequests(@PathVariable("userId") Long userId,
                                                                   @PathVariable("eventId") Long eventId,
                                                                   @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventService.changeUsersEventRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUsersRequests(@PathVariable("userId") Long userId) {
        return eventService.getUsersRequests(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto postUsersRequests(@PathVariable("userId") Long userId,
                                                     @RequestParam() Long eventId
    ) {
        return eventService.postUsersRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUsersRequests(@PathVariable("userId") Long userId,
                                                       @PathVariable("requestId") Long requestId
    ) {
        return eventService.cancelUsersRequests(userId, requestId);
    }
}
