package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.entity.EndpointHit;
import ru.practicum.explorewithme.exceptions.UnCorrectableException;
import ru.practicum.explorewithme.mapper.EndpointMapper;
import ru.practicum.explorewithme.repository.StatRepository;
import ru.practicum.explorewithme.stats.EndpointHitDto;
import ru.practicum.explorewithme.stats.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static variables.Constants.DATE_PATTERN;


@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final EndpointMapper endpointMapper;
    private final StatRepository statRepository;

    @Override
    public List<ViewStatsDto> getStats(String start, String end, String uris, Boolean unique) {
        List<EndpointHit> endpoints = statRepository.findAll();
        if (uris != null) {
            String[] urisMas = uris.split(",");
            List<String> urisList = Arrays.stream(urisMas)
                    .collect(Collectors.toList());
            endpoints = endpoints.stream()
                    .filter(endpointHit -> urisList.contains(endpointHit.getUri()))
                    .collect(Collectors.toList());
        }
        if (unique) {
            endpoints = endpoints.stream()
                    .distinct()
                    .collect(Collectors.toList());
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDateTime startTime = LocalDateTime.parse(start, df);
        LocalDateTime endTime = LocalDateTime.parse(end, df);
        if (startTime.isAfter(endTime)) {
            throw new UnCorrectableException("Wrong date");
        }
        endpoints = endpoints.stream()
                .filter(endpointHit -> endpointHit.getTimestamp().isAfter(startTime))
                .filter(endpointHit -> endpointHit.getTimestamp().isBefore(endTime))
                .collect(Collectors.toList());
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
        return viewStatsDtos.stream()
                .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public EndpointHitDto createHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = endpointMapper.toEndpointHit(endpointHitDto);
        statRepository.save(endpointHit);
        return endpointMapper.toEndpointHitDto(endpointHit);
    }
}

