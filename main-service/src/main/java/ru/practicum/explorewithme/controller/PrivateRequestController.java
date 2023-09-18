package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.requests.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.requests.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.requests.ParticipationRequestDto;
import ru.practicum.explorewithme.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;


    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUsersEventRequests(@PathVariable("userId") Long userId,
                                                               @PathVariable("eventId") Long eventId) {
        return requestService.getUsersEventRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeUsersEventRequests(@PathVariable("userId") Long userId,
                                                                   @PathVariable("eventId") Long eventId,
                                                                   @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.changeUsersEventRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUsersRequests(@PathVariable("userId") Long userId) {
        return requestService.getUsersRequests(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postUsersRequests(@PathVariable("userId") Long userId,
                                                     @RequestParam() Long eventId
    ) {
        return requestService.postUsersRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUsersRequests(@PathVariable("userId") Long userId,
                                                       @PathVariable("requestId") Long requestId
    ) {
        return requestService.cancelUsersRequests(userId, requestId);
    }

}
