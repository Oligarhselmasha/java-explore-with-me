package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.client.StatClient;
import ru.practicum.explorewithme.dto.StatDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatClient statClient;

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats() {
        return statClient.getStats();
    }

    @PostMapping("/hit")
    public ResponseEntity<Object> postHit(@Valid @RequestBody StatDto statDto) {
        return statClient.postHit(statDto);
    }
}
