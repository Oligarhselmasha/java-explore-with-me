package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.EndpointHitDto;
import ru.practicum.explorewithme.ViewStatsDto;

import java.util.List;

public interface StatService {

    List<ViewStatsDto> getStats(Integer userId, String start, String end, String uris, String unique);

    EndpointHitDto createHit(Integer userId, EndpointHitDto endpointHitDto);
}
