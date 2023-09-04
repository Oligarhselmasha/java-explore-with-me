package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.EndpointHitDto;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.ViewStatsDto;

import lombok.RequiredArgsConstructor;
import ru.practicum.explorewithme.mapper.EndpointMapper;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.repository.StatRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final StatClient statClient;
    private final EndpointMapper endpointMapper;

    @Override
    public List<ViewStatsDto> getStats(Integer userId, String start, String end, String uris, String unique) {
        List<EndpointHit> endpoints = statRepository.findAll();
//        List<EndpointHit> endpoints = statRepository.findAll();
        List<ViewStatsDto> viewStatsDtos = new ArrayList<>();
        Set<String> urisSet = new HashSet<>();
        endpoints.forEach(endpointHit -> urisSet.add(endpointHit.getUri()));
        for (String uri : urisSet) {
            Set<String> apisSet = new HashSet<>();
            endpoints.forEach(endpointHit -> apisSet.add(endpointHit.getApp()));
            apisSet.forEach(api -> viewStatsDtos.add(new ViewStatsDto(api, uri)));
        }
        for (ViewStatsDto viewStatsDto : viewStatsDtos) {
            int urisHit = (int) endpoints.stream()
                    .filter(endpointHit -> endpointHit.getUri().equals(viewStatsDto.getUri()))
                    .filter(endpointHit -> endpointHit.getApp().equals(viewStatsDto.getApp())).count();
            viewStatsDto.setHits(urisHit);
        }
        statClient.getStats(userId, start, end, uris, unique);
        return null;
    }

    @Override
    public EndpointHitDto createHit(Integer userId, EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = endpointMapper.toEndpointHit(endpointHitDto);
        statRepository.save(endpointHit);
        statClient.crateHit(userId, endpointHitDto);
        return null;
    }
}

