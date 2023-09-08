package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.EndpointHitDto;
import ru.practicum.explorewithme.EventFullDto;
import ru.practicum.explorewithme.EventShortDto;
import ru.practicum.explorewithme.StatClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final StatClient statClient;


    public EventFullDto getEvent(Integer id, String ip) {
        EventFullDto eventFullDto = new EventFullDto();
        statClient.createHit(makeHit(ip, id));
        return eventFullDto;
    }

    public List<EventShortDto> getEvents(String text, Integer[] categories, Boolean paid, String rangeStart, String rangeEnd,
                                         Boolean onlyAvailable, String sort, Integer from, Integer size, String ip) {
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        statClient.createHit(makeHit(ip, null));
        return eventShortDtos;
    }

    private EndpointHitDto makeHit(String ip, Integer id) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setIp(ip);
        String uri = "/events";
        if (id != null) {
            uri += id;
        }
        endpointHitDto.setUri(uri);
        return endpointHitDto;
    }
}
