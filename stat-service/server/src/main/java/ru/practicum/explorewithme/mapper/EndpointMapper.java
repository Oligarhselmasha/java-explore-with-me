package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.entity.EndpointHit;
import ru.practicum.explorewithme.stats.EndpointHitDto;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;

@Mapper(componentModel = "spring")
public interface EndpointMapper {

    @Mapping(source = "timestamp", target = "timestamp", dateFormat = DATE_PATTERN)
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);

    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);
}
