package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.entity.Compilation;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.events.CompilationDto;
import ru.practicum.explorewithme.events.NewCompilationDto;
import ru.practicum.explorewithme.events.UpdateCompilationRequest;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        List<Long> eventsIds;
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        if (updateCompilationRequest.getEvents() != null) {
            eventsIds = Arrays.stream(updateCompilationRequest.getEvents()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            List<Event> events = eventRepository.findByIdIn(eventsIds);
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
}
