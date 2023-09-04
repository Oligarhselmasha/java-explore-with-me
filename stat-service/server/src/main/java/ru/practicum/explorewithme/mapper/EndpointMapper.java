package ru.practicum.explorewithme.mapper;

import ru.practicum.explorewithme.entity.EndpointHit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EndpointMapper {
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);

    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);

}
