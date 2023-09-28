package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.dto.events.EventFullDto;
import ru.practicum.explorewithme.dto.events.EventShortDto;
import ru.practicum.explorewithme.dto.events.NewEventDto;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = DATE_PATTERN)
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "state", ignore = true)
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = DATE_PATTERN)
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = DATE_PATTERN)
    Event toEvent(NewEventDto newEventDto);
}
