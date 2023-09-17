package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.entity.Location;
import ru.practicum.explorewithme.location.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location fromDtoLocation(LocationDto locationDto);
}
