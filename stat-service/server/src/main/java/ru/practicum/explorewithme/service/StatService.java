package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.stats.EndpointHitDto;
import ru.practicum.explorewithme.stats.ViewStatsDto;

import java.util.List;

public interface StatService {

    List<ViewStatsDto> getStats(String start, String end, String uris, Boolean unique);

    EndpointHitDto createHit(EndpointHitDto endpointHitDto);
}
