package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.events.CompilationDto;
import ru.practicum.explorewithme.dto.events.NewCompilationDto;
import ru.practicum.explorewithme.dto.events.UpdateCompilationRequest;
import ru.practicum.explorewithme.entity.Compilation;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    public CompilationDto postNewCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        List<Long> ids = newCompilationDto.getEvents();
        List<Event> events = eventRepository.findByIdIn(ids);
        Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    public CompilationDto changeCompilation(UpdateCompilationRequest updateCompilationRequest, Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    public void removeCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return compilationRepository.findByPinned(pinned).stream()
                .map(compilationMapper::toCompilationDto)
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        return compilationMapper.toCompilationDto(compilation);
    }
}
