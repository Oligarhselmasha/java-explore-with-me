package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.entity.*;
import ru.practicum.explorewithme.events.*;
import ru.practicum.explorewithme.exceptions.ConflictException;
import ru.practicum.explorewithme.exceptions.MissingException;
import ru.practicum.explorewithme.exceptions.UnCorrectableException;
import ru.practicum.explorewithme.mapper.*;
import ru.practicum.explorewithme.repository.*;
import ru.practicum.explorewithme.requests.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.requests.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.requests.ParticipationRequestDto;
import ru.practicum.explorewithme.requests.UpdateEventUserRequest;
import ru.practicum.explorewithme.stats.EndpointHitDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;

@Service
@RequiredArgsConstructor
public class EventService {
    private final StatClient statClient;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventStateRepository eventStateRepository;
    private final CompilationRepository compilationRepository;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;
    private final CategoryMapper categoryMapper;

    private final LocationMapper locationMapper;
    private final ParticipationRequestMapper participationRequestMapper;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final LocationRepository locationRepository;


    public EventFullDto getEvent(Long id, String ip) {
        Event event = eventRepository.findById(id).orElseThrow();
        if (!event.getState().getState().equals("PUBLISHED")) {
            throw new MissingException("Event with id=" + id + " was not found");
        }
        event.setViews(event.getViews() + 1);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setState(eventStateRepository.findById(event.getState().getId()).orElseThrow().getState());
        statClient.createHit(makeHit(ip, id));
        return eventFullDto;
    }

    public List<EventShortDto> getEvents(String text, Long[] categories, Boolean paid, String rangeStart, String rangeEnd,
                                         Boolean onlyAvailable, String sort, Integer from, Integer size, String ip) {
        List<Event> events = new ArrayList<>();
        List<EventShortDto> eventShortDtos = new ArrayList<>();
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
        if (categories != null) {
            List<Long> categoriesIds = Arrays.stream(categories).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            events = events.stream().filter(event -> categoriesIds.contains(event.getCategory().getId())).collect(Collectors.toList());
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
        if (events.size() <= size) {
            size = events.size();
        }
        events = new ArrayList<>(events).subList(from, size);
        events.forEach(event -> eventShortDtos.add(eventMapper.toEventShortDto(event)));
        statClient.createHit(makeHit(ip, null));
        return eventShortDtos;
    }

    public List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size, String ip) {
        User user = userRepository.findById(userId).orElseThrow();
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        List<Event> events = eventRepository.findByInitiator_IdOrderByIdAsc(userId);
        List<Event> eventsAll = eventRepository.findAll();
        if (!events.isEmpty()) {
            List<EventShortDto> sortedEventShortDtoList;
            events.forEach(event -> eventShortDtoList.add(eventMapper.toEventShortDto(event)));
            if (eventShortDtoList.size() <= size) {
                size = eventShortDtoList.size();
            }
            sortedEventShortDtoList = eventShortDtoList.stream()
                    .sorted(Comparator.comparing(EventShortDto::getId))
                    .collect(Collectors.toList()).subList(from, size);
            return sortedEventShortDtoList;
        }
        return eventShortDtoList;
    }

    public EventFullDto getUsersEvent(Long userId, Long eventId, String ip) {
        User user = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new RuntimeException();
        }
        return eventMapper.toEventFullDto(event);
    }

    public EventFullDto postNewUsersEvent(Long userId, NewEventDto newEventDto, String ip) {
        User user = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow();
        Event event = eventMapper.toEvent(newEventDto);
        if (Math.abs(ChronoUnit.HOURS.between(event.getEventDate(), LocalDateTime.now())) < 2 || event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new UnCorrectableException("Wrong date.");
        }
        event.setCategory(category);
        EventState eventState = eventStateRepository.findByState("PENDING");
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
        eventFullDto.setState("PENDING");
        return eventFullDto;
    }

    public EventFullDto changeUsersEvent(Long userId, Long eventId, @Valid UpdateEventUserRequest updateEventUserRequest) {
        User user = userRepository.findById(userId).orElseThrow();
        Event eventBefore = eventRepository.findById(eventId).orElseThrow();
        if (eventBefore.getState().getState().equals("PUBLISHED")) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            eventBefore.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
            LocalDateTime eventDate = LocalDateTime.parse(updateEventUserRequest.getEventDate(), df);
            if (Math.abs(ChronoUnit.HOURS.between(eventDate, LocalDateTime.now())) < 2 || eventDate.isBefore(eventBefore.getEventDate())) {
                throw new UnCorrectableException("Wrong date.");
            }
            eventBefore.setEventDate(eventDate);
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            eventBefore.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getPaid() != null) {
            eventBefore.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory().getId()).orElseThrow();
            eventBefore.setCategory(category);
        }
        if (updateEventUserRequest.getLocation() != null) {
            Location location = locationMapper.fromDtoLocation(updateEventUserRequest.getLocation());
            eventBefore.setLocation(location);
        }
        if (updateEventUserRequest.getDescription() != null) {
            eventBefore.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            eventBefore.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getTitle() != null) {
            eventBefore.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals("SEND_TO_REVIEW")) {
                eventBefore.setState(eventStateRepository.findByState("PENDING"));
            }
            if (updateEventUserRequest.getStateAction().equals("CANCEL_REVIEW")) {
                eventBefore.setState(eventStateRepository.findByState("CANCELED"));
            }
        }
        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(eventBefore));
        eventFullDto.setState(eventStateRepository.findById(eventBefore.getState().getId()).orElseThrow().getState());
        return eventFullDto;
    }

    public EventRequestStatusUpdateResult changeUsersEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User user = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        String state = eventRequestStatusUpdateRequest.getStatus();
        List<ParticipationRequest> participationRequests = new ArrayList<>();
        List<Long> reqIds = eventRequestStatusUpdateRequest.getRequestIds();
        reqIds.forEach(r -> participationRequests.add(participationRequestRepository.findByIdAndEvent_Initiator_Id(r, userId)));
        List<ParticipationRequestDto> participationRequestDtos = new ArrayList<>();
        participationRequests.forEach(r -> participationRequestDtos.add(participationRequestMapper.toParticipationRequestDto(r)));
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        if (state.equals("CONFIRMED")) {
            if (participationRequestDtos.size() > event.getParticipantLimit() - event.getConfirmedRequests()) {
                throw new ConflictException("Out of limit.");
            }
            event.setConfirmedRequests(event.getConfirmedRequests() + participationRequestDtos.size());
            participationRequests.forEach(p -> p.setStatus(eventStateRepository.findByState("CONFIRMED")));
            participationRequests.forEach(participationRequestRepository::save);
            participationRequestDtos.forEach(p -> p.setEvent(participationRequestRepository.findById(p.getId()).orElseThrow().getEvent().getId()));
            participationRequestDtos.forEach(p -> p.setRequester(participationRequestRepository.findById(p.getId()).orElseThrow().getRequester().getId()));
            participationRequestDtos.forEach(p -> p.setStatus(participationRequestRepository.findById(p.getId()).orElseThrow().getStatus().getState()));
            eventRequestStatusUpdateResult.setConfirmedRequests(participationRequestDtos);
        } else if (state.equals("REJECTED")) {
            if (participationRequestRepository.findByIdInAndStatus_State(eventRequestStatusUpdateRequest.getRequestIds(), "CONFIRMED").size() > 0) {
                throw new ConflictException("Already confirmed");
            }
            participationRequests.forEach(p -> p.setStatus(eventStateRepository.findByState("REJECTED")));
            participationRequests.forEach(participationRequestRepository::save);
            participationRequestDtos.forEach(p -> p.setEvent(participationRequestRepository.findById(p.getId()).orElseThrow().getEvent().getId()));
            participationRequestDtos.forEach(p -> p.setRequester(participationRequestRepository.findById(p.getId()).orElseThrow().getRequester().getId()));
            participationRequestDtos.forEach(p -> p.setStatus(participationRequestRepository.findById(p.getId()).orElseThrow().getStatus().getState()));
            eventRequestStatusUpdateResult.setRejectedRequests(participationRequestDtos);
        }
        return eventRequestStatusUpdateResult;
    }

    public List<ParticipationRequestDto> getUsersEventRequests(Long userId, Long eventId) {
        List<ParticipationRequestDto> participationRequestDtos = new ArrayList<>();
        User user = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        List<ParticipationRequest> participationRequests = participationRequestRepository.findByEvent_Id(event.getId());
        participationRequests.forEach(p -> participationRequestDtos.add(participationRequestMapper.toParticipationRequestDto(p)));
        participationRequestDtos.forEach(p -> p.setRequester(participationRequestRepository.findById(p.getId()).orElseThrow().getRequester().getId()));
        participationRequestDtos.forEach(p -> p.setStatus(participationRequestRepository.findById(p.getId()).orElseThrow().getStatus().getState()));
        participationRequestDtos.forEach(p -> p.setEvent(participationRequestRepository.findById(p.getId()).orElseThrow().getEvent().getId()));
        return participationRequestDtos;
    }


    public List<ParticipationRequestDto> getUsersRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<ParticipationRequestDto> participationRequestDtos = new ArrayList<>();
        List<ParticipationRequest> participationRequests = participationRequestRepository.findByRequester_Id(userId);
        participationRequests.forEach(r -> participationRequestDtos.add(participationRequestMapper.toParticipationRequestDto(r)));
        participationRequestDtos.forEach(p -> p.setRequester(participationRequestRepository.findById(p.getId()).orElseThrow().getRequester().getId()));
        participationRequestDtos.forEach(p -> p.setStatus(participationRequestRepository.findById(p.getId()).orElseThrow().getStatus().getState()));
        participationRequestDtos.forEach(p -> p.setEvent(participationRequestRepository.findById(p.getId()).orElseThrow().getEvent().getId()));
        return participationRequestDtos;
    }

    public ParticipationRequestDto postUsersRequests(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        ParticipationRequest participationRequestOld = participationRequestRepository.findByEvent_IdAndRequester_Id(eventId, userId);
        if (participationRequestOld != null) {
            throw new ConflictException("Request already exist.");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Request from initiator.");
        }
        if (!Objects.equals(event.getState().getState(), "PUBLISHED")) {
            throw new ConflictException("Its not published event.");
        }
        if (event.getParticipantLimit() != 0 && participationRequestRepository.findByEvent_IdAndStatus_State(eventId, "CONFIRMED").size() == event.getParticipantLimit()) {
            throw new ConflictException("Participation limit has been reached.");
        }
        ParticipationRequest participationRequestNew = new ParticipationRequest();
        participationRequestNew.setRequester(user);
        participationRequestNew.setEvent(event);
        if (event.getParticipantLimit() != 0) {
            if (event.getRequestModeration()) {
                participationRequestNew.setStatus(eventStateRepository.findByState("PENDING"));
                participationRequestNew.setCreated(LocalDateTime.now());
            } else {
                participationRequestNew.setStatus(eventStateRepository.findByState("CONFIRMED"));
                participationRequestNew.setCreated(LocalDateTime.now());
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            }
        } else {
            participationRequestNew.setStatus(eventStateRepository.findByState("CONFIRMED"));
            participationRequestNew.setCreated(LocalDateTime.now());
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        eventRepository.save(event);
        ParticipationRequest participationRequestSaved = participationRequestRepository.save(participationRequestNew);
        ParticipationRequestDto participationRequestDto = participationRequestMapper.toParticipationRequestDto(participationRequestSaved);
        participationRequestDto.setEvent(eventId);
        participationRequestDto.setRequester(userId);
        participationRequestDto.setStatus(eventStateRepository.findByState(participationRequestSaved.getStatus().getState()).getState());
        return participationRequestDto;
    }

    public ParticipationRequestDto cancelUsersRequests(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow();
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId).orElseThrow();
        if (!Objects.equals(participationRequest.getRequester().getId(), userId)) {
            throw new RuntimeException();
        }
        participationRequest.setStatus(eventStateRepository.findByState("CANCELED"));
        ParticipationRequest participationRequestNew = participationRequestRepository.save(participationRequest);
        ParticipationRequestDto participationRequestDto = participationRequestMapper.toParticipationRequestDto(participationRequestNew);
        participationRequestDto.setRequester(userId);
        participationRequestDto.setEvent(participationRequestNew.getEvent().getId());
        participationRequestDto.setStatus("CANCELED");
        return participationRequestDto;
    }

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<CompilationDto> compilationDtos = new ArrayList<>();
        List<Compilation> compilations = compilationRepository.findByPinned(pinned);
        if (compilations.size() <= size) {
            size = compilations.size();
        }
        compilations = new ArrayList<>(compilations).subList(from, size);
        compilations.forEach(c -> compilationDtos.add(compilationMapper.toCompilationDto(c)));
        return compilationDtos;
    }

    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        return compilationMapper.toCompilationDto(compilation);
    }

    public CategoryDto getCategorie(Long catId) {
        return categoryMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new MissingException("Not found category")));
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        List<CategoryDto> categoryDtos = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();
        if (categories.size() <= size) {
            size = categories.size();
        }
        categories = new ArrayList<>(categories).subList(from, size);
        categories.forEach(c -> categoryDtos.add(categoryMapper.toCategoryDto(c)));
        return categoryDtos;
    }

    public List<EventFullDto> getEvents(Long users, String states, Long categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        List<Long> usersIds = new ArrayList<>();
        List<String> statesNames = new ArrayList<>();
        List<Long> categoriesIds = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, df);
        } else {
            start = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, df);
        } else {
            end = LocalDateTime.of(2100, 1, 1, 0, 0, 0);
        }
        if (users != null) {
            usersIds.add(users);
        } else {
            List<User> usersList = userRepository.findAll();
            List<Long> finalUsersIds = usersIds;
            usersList.forEach(user -> finalUsersIds.add(user.getId()));
        }
        if (states != null) {
            statesNames.add(states);
        } else {
            List<EventState> eventsStatesList = eventStateRepository.findAll();
            List<String> finalStatesNames = statesNames;
            eventsStatesList.forEach(eventState -> finalStatesNames.add(eventState.getState()));
        }
        if (categories != null) {
            categoriesIds.add(categories);
        } else {
            List<Category> categoryList = categoryRepository.findAll();
            List<Long> finalCategoriesIds = categoriesIds;
            categoryList.forEach(category -> finalCategoriesIds.add(category.getId()));
        }
        List<Event> eventsAll = eventRepository.findAll();
        List<Event> events = eventRepository.findByInitiator_IdInAndState_StateInAndCategory_NameInAndEventDateAfterAndEventDateBeforeOrderByIdAsc(usersIds, statesNames, categoriesIds, start, end);
        if (events.size() <= size) {
            size = events.size();
        }
        events = new ArrayList<>(events).subList(from, size);
        events.forEach(event -> eventFullDtos.add(eventMapper.toEventFullDto(event)));
        eventFullDtos.forEach(event -> event
                .setState(eventRepository
                        .findById(event.getId())
                        .orElseThrow()
                        .getState()
                        .getState()));
        return eventFullDtos;
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
            if (Objects.equals(updateEventAdminRequest.getStateAction(), "PUBLISH_EVENT")) {
                if (Objects.equals(event.getState().getState(), "PENDING")) {
                    event.setState(eventStateRepository.findByState("PUBLISHED"));
                    event.setPublishedOn(publicationDate);
                } else {
                    throw new ConflictException("Its not pending event.");
                }
            } else if (Objects.equals(updateEventAdminRequest.getStateAction(), "REJECT_EVENT")) {
                if (Objects.equals(event.getState().getState(), "PUBLISHED")) {
                    throw new ConflictException("Its already published.");
                }
                event.setState(eventStateRepository.findByState("CANCELED"));
            }
        }
        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setState(eventStateRepository.findById(event.getState().getId()).orElseThrow().getState());
        return eventFullDto;
    }

    public CategoryDto changeCategory(CategoryDto categoryDto, Long catId) {
        String name = categoryDto.getName();
        List<Category> categories = categoryRepository.findAll();
        List<String> names = new ArrayList<>();
        categories.forEach(category -> names.add(category.getName()));
        if (names.contains(name) && !Objects.equals(categoryRepository.findByName(name).getId(), catId)) {
            throw new ConflictException("category already exist");
        }
        Category category = categoryRepository.findById(catId).orElseThrow();
        category.setName(name);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public void removeCategory(Long catId) {
        List<Event> events = eventRepository.findByCategory_Id(catId);
        if (!events.isEmpty()) {
            throw new ConflictException("This category contains events");
        }
        categoryRepository.deleteById(catId);
    }

    public CategoryDto postNewCategory(NewCategoryDto newCategoryDto) {
        String name = newCategoryDto.getName();
        List<Category> categories = categoryRepository.findAll();
        List<String> names = new ArrayList<>();
        categories.forEach(category -> names.add(category.getName()));
        if (names.contains(name)) {
            throw new ConflictException("category already exist");
        }
        Category category = categoryMapper.toCategory(newCategoryDto);
        CategoryDto afterSave = categoryMapper.toCategoryDto(categoryRepository.save(category));
        return afterSave;
    }

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
        List<Long> eventsIds = new ArrayList<>();
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

    private EndpointHitDto makeHit(String ip, Long id) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setIp(ip);
        String uri = "/events";
        if (id != null) {
            uri += id;
        }
        endpointHitDto.setUri(uri);
        endpointHitDto.setTimestamp(LocalDateTime.now().format(df));
        return endpointHitDto;
    }
}
