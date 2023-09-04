package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapping;
import ru.practicum.explorewithme.EndpointHitDto;
import ru.practicum.explorewithme.entity.EndpointHit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EndpointMapper {
    @Mapping(source = "timestamp", target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);

    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);

}
