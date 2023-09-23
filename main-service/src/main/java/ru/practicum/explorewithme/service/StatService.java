package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.stats.EndpointHitDto;
import ru.practicum.explorewithme.stats.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.explorewithme.variables.Constants.*;

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
        List<ViewStatsDto> list = (List<ViewStatsDto>) statClient.getStats(id);
        return (long) list.get(0).getHits();
    }
}
