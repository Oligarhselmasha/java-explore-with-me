package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.entity.Compilation;
import ru.practicum.explorewithme.events.CompilationDto;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationDto toCompilationDto(Compilation compilation);
}
