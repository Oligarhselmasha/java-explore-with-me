package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.events.EventFullDto;
import ru.practicum.explorewithme.dto.events.EventShortDto;
import ru.practicum.explorewithme.dto.events.NewEventDto;
import ru.practicum.explorewithme.dto.events.UpdateEventAdminRequest;
import ru.practicum.explorewithme.dto.requests.UpdateEventUserRequest;
import ru.practicum.explorewithme.entity.Category;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.EventState;
import ru.practicum.explorewithme.entity.Location;
import ru.practicum.explorewithme.entity.User;
import ru.practicum.explorewithme.exceptions.ConflictException;
import ru.practicum.explorewithme.exceptions.MissingException;
import ru.practicum.explorewithme.exceptions.UnCorrectableException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.mapper.LocationMapper;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.EventStateRepository;
import ru.practicum.explorewithme.repository.LocationRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;
import ru.practicum.explorewithme.variables.Status;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;
import static ru.practicum.explorewithme.variables.Constants.MAX_DATE;
import static ru.practicum.explorewithme.variables.Constants.MIN_DATE;
import static ru.practicum.explorewithme.variables.StateAction.CANCEL_REVIEW;
import static ru.practicum.explorewithme.variables.StateAction.PUBLISH_EVENT;
import static ru.practicum.explorewithme.variables.StateAction.REJECT_EVENT;
import static ru.practicum.explorewithme.variables.StateAction.SEND_TO_REVIEW;
import static ru.practicum.explorewithme.variables.Status.CANCELED;
import static ru.practicum.explorewithme.variables.Status.PENDING;
import static ru.practicum.explorewithme.variables.Status.PUBLISHED;

@Service
@RequiredArgsConstructor
public class EventService {
    private final StatService statService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventStateRepository eventStateRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final LocationRepository locationRepository;


    public EventFullDto getEvent(Long id, String ip) {
        Event event = eventRepository.findById(id).orElseThrow();
        if (!event.getState().getState().equals(Status.PUBLISHED)) {
            throw new MissingException("Event with id=" + id + " was not found");
        }
        statService.makeHit(ip, id);
        Long views = statService.getStats(id);
        event.setViews(views);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setState(eventStateRepository.findById(event.getState().getId()).orElseThrow().getState().name());
        return eventFullDto;
    }

    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                         Boolean onlyAvailable, String sort, Integer from, Integer size, String ip) {
        List<Event> events;
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDateTime start;
        LocalDateTime end;
        if (rangeEnd == null) {
            end = LocalDateTime.MAX;
        } else {
            end = LocalDateTime.parse(rangeEnd, df);
        }
        if (rangeStart == null) {
            start = LocalDateTime.now();
        } else {
            start = LocalDateTime.parse(rangeStart, df);
        }
        if (start.isAfter(end)) {
            throw new UnCorrectableException("Wrong dates.");
        }
        events = eventRepository.findAll();
        if (text != null) {
            events = events.stream().filter(event -> event.getAnnotation().toLowerCase().contains(text.toLowerCase()) ||
                    event.getDescription().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
        }
        if (categories != null) {
            events = events.stream().filter(event -> categories.contains(event.getCategory().getId())).collect(Collectors.toList());
        }
        if (paid != null) {
            events = events.stream().filter(event -> event.getPaid() == paid).collect(Collectors.toList());
        }
        events = events.stream().filter(event -> event.getEventDate().isAfter(start) && event.getEventDate().isBefore(end)).collect(Collectors.toList());
        if (Objects.equals(sort, "EVENT_DATE")) {
            events.sort(Comparator.comparing(Event::getEventDate));
        } else if (Objects.equals(sort, "VIEWS")) {
            events.sort(Comparator.comparing(Event::getViews));
        }
        if (onlyAvailable) {
            events = events.stream().filter(event -> event.getParticipantLimit() > participationRequestRepository.findByEvent_Id(event.getId()).size()).collect(Collectors.toList());
        }
        statService.makeHit(ip, null);
        return events.stream()
                .map(eventMapper::toEventShortDto)
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    public List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow();
        return eventRepository.findByInitiator_IdOrderByIdAsc(userId).stream()
                .map(eventMapper::toEventShortDto)
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    public EventFullDto getUsersEvent(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new RuntimeException();
        }
        return eventMapper.toEventFullDto(event);
    }

    public EventFullDto postNewUsersEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow();
        Event event = eventMapper.toEvent(newEventDto);
        if (Math.abs(ChronoUnit.HOURS.between(event.getEventDate(), LocalDateTime.now())) < 2 || event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new UnCorrectableException("Wrong date.");
        }
        event.setCategory(category);
        EventState eventState = eventStateRepository.findByState(PENDING);
        if (newEventDto.getLocation() != null) {
            Location location = locationRepository.save(locationMapper.fromDtoLocation(newEventDto.getLocation()));
            event.setLocation(location);
        }
        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        event.setViews(0L);
        event.setState(eventState);
        event.setConfirmedRequests(0L);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        Event eventSaved = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventSaved);
        eventFullDto.setState(PENDING.name());
        return eventFullDto;
    }

    public EventFullDto changeUsersEvent(Long userId, Long eventId, @Valid UpdateEventUserRequest updateEventUserRequest) {
        userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (event.getState().getState().equals(PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        event = eventRepository.save(updateEventUser(event, updateEventUserRequest));
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        return setEventDtoState(eventFullDto, event);
    }

    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<EventFullDto> eventFullDtos;
        Set<Long> usersIds = new HashSet<>();
        Set<Status> statesNames = new HashSet<>();
        Set<Long> categoriesIds = new HashSet<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, df);
        } else {
            start = MIN_DATE;
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, df);
        } else {
            end = MAX_DATE;
        }
        if (users != null) {
            usersIds.addAll(users);
        } else {
            Set<User> usersSet = getSetUsers();
            usersSet
                    .forEach(user -> usersIds.add(user.getId()));
        }
        if (states != null) {
            states
                    .forEach(s -> statesNames.add(Status.valueOf(s)));
        } else {
            Set<EventState> eventSet = getSetStates();
            eventSet
                    .forEach(eventState -> statesNames.add(eventState.getState()));
        }
        if (categories != null) {
            categoriesIds.addAll(categories);
        } else {
            Set<Category> categorySet = getSetCategories();
            categorySet
                    .forEach(category -> categoriesIds.add(category.getId()));
        }
        List<Event> events = eventRepository.findByInitiator_IdInAndState_StateInAndCategory_NameInAndEventDateAfterAndEventDateBeforeOrderByIdAsc(usersIds, statesNames, categoriesIds, start, end);
        eventFullDtos = events.stream()
                .map(eventMapper::toEventFullDto)
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
        return eventFullDtos.stream()
                .map(this::takeEventDtoState)
                .collect(Collectors.toList());
    }

    public EventFullDto changeEventByAdmin(UpdateEventAdminRequest updateEventAdminRequest, Long eventId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDateTime publicationDate = LocalDateTime.now();
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (Math.abs(ChronoUnit.HOURS.between(publicationDate, event.getEventDate())) < 1) {
            throw new RuntimeException();
        }
        if (updateEventAdminRequest.getEventDate() != null &&
                LocalDateTime.parse(updateEventAdminRequest.getEventDate(), df).isBefore(LocalDateTime.now())) {
            throw new UnCorrectableException("Wrong date");
        }
        event = eventRepository.save(updateEventAdmin(event, updateEventAdminRequest));
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        return setEventDtoState(eventFullDto, event);
    }

    private Set<User> getSetUsers() {
        return new HashSet<>(userRepository.findAll());
    }

    private Set<EventState> getSetStates() {
        return new HashSet<>(eventStateRepository.findAll());
    }

    private Set<Category> getSetCategories() {
        return new HashSet<>(categoryRepository.findAll());
    }

    private Event updateEventAdmin(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Long categoryId = Long.valueOf(updateEventAdminRequest.getCategory());
            Category category = categoryRepository.findById(categoryId).orElseThrow();
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(), df);
            event.setEventDate(eventDate);
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            Location location = locationMapper.fromDtoLocation(updateEventAdminRequest.getLocation());
            event.setLocation(locationRepository.save(location));
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (Objects.equals(updateEventAdminRequest.getStateAction(), PUBLISH_EVENT.name())) {
                if (Objects.equals(event.getState().getState(), PENDING)) {
                    event.setState(eventStateRepository.findByState(PUBLISHED));
                    event.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new ConflictException("Its not pending event.");
                }
            } else if (Objects.equals(updateEventAdminRequest.getStateAction(), REJECT_EVENT.name())) {
                if (Objects.equals(event.getState().getState(), PUBLISHED)) {
                    throw new ConflictException("Its already published.");
                }
                event.setState(eventStateRepository.findByState(CANCELED));
            }
        }
        return event;
    }

    private Event updateEventUser(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
            LocalDateTime eventDate = LocalDateTime.parse(updateEventUserRequest.getEventDate(), df);
            if (Math.abs(ChronoUnit.HOURS.between(eventDate, LocalDateTime.now())) < 2 || eventDate.isBefore(event.getEventDate())) {
                throw new UnCorrectableException("Wrong date.");
            }
            event.setEventDate(eventDate);
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory().getId()).orElseThrow();
            event.setCategory(category);
        }
        if (updateEventUserRequest.getLocation() != null) {
            Location location = locationMapper.fromDtoLocation(updateEventUserRequest.getLocation());
            event.setLocation(location);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(SEND_TO_REVIEW.name())) {
                event.setState(eventStateRepository.findByState(PENDING));
            }
            if (updateEventUserRequest.getStateAction().equals(CANCEL_REVIEW.name())) {
                event.setState(eventStateRepository.findByState(CANCELED));
            }
        }
        return event;
    }

    private EventFullDto setEventDtoState(EventFullDto eventFullDto, Event event) {
        eventFullDto.setState(eventStateRepository.findByState(event.getState().getState()).getState().name());
        return eventFullDto;
    }

    private EventFullDto takeEventDtoState(EventFullDto eventFullDto) {
        eventFullDto.setState(eventRepository.findById(eventFullDto.getId()).orElseThrow().getState().getState().name());
        return eventFullDto;
    }
}
