package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.events.EventFullDto;
import ru.practicum.explorewithme.events.EventShortDto;
import ru.practicum.explorewithme.events.NewEventDto;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "state", ignore = true)
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = DATE_PATTERN)
    Event toEvent(NewEventDto newEventDto);
}
