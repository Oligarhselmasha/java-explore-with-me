package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.entity.ParticipationRequest;
import ru.practicum.explorewithme.dto.requests.ParticipationRequestDto;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(source = "created", target = "created", dateFormat = DATE_PATTERN)
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);
}
