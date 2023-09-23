package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.stats.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;

@Service
@RequiredArgsConstructor
public class StatService {

    private final StatClient statClient;

    public void makeHit(String ip, Long id) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setIp(ip);
        String uri = "/events";
        if (id != null) {
            uri = uri + "/" + id;
        }
        endpointHitDto.setUri(uri);
        endpointHitDto.setTimestamp(LocalDateTime.now().format(df));
        statClient.createHit(endpointHitDto);
    }

    public Long getStats(Long id) {
        ResponseEntity<Object> response = statClient.getStats(id);
        Object body = response.getBody();
        ArrayList<LinkedHashMap<Object, Object>> ll = (ArrayList<LinkedHashMap<Object, Object>>) body;
        assert ll != null;
        int a = (Integer) ll.get(0).get("hits");
        return (long) a;
    }
}
