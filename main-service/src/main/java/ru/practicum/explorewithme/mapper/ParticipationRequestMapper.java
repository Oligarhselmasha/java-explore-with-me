package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.ParticipationRequest;
import ru.practicum.explorewithme.events.EventFullDto;
import ru.practicum.explorewithme.events.EventShortDto;
import ru.practicum.explorewithme.events.NewEventDto;
import ru.practicum.explorewithme.requests.ParticipationRequestDto;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "status", ignore = true)
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);
}
