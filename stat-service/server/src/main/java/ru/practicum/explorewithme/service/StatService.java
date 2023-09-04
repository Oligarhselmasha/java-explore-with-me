package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.EndpointHitDto;
import ru.practicum.explorewithme.ViewStatsDto;

import java.util.List;

public interface StatService {

    List<ViewStatsDto> getStats(String start, String end, String uris, Boolean unique);

    EndpointHitDto createHit(EndpointHitDto endpointHitDto);
}
