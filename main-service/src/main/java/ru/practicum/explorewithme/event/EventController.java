package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static ru.practicum.explorewithme.constants.Variables.USER_HEADER;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventController {

    private final EventClient eventClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEvent(@RequestHeader(USER_HEADER) Integer userId, @PathVariable("id") Integer eventId) {
        return eventClient.getEvent(userId, eventId);
    }

    @GetMapping()
    public List<ResponseEntity<Object>> getEvents(@RequestHeader(USER_HEADER) Integer userId) {
        return eventClient.getEvents(userId);
    }
}
