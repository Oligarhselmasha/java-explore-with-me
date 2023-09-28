package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.requests.ParticipationRequestDto;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.EventState;
import ru.practicum.explorewithme.entity.ParticipationRequest;
import ru.practicum.explorewithme.entity.User;
import ru.practicum.explorewithme.exceptions.ConflictException;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.EventStateRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;
import static ru.practicum.explorewithme.variables.Status.CANCELED;
import static ru.practicum.explorewithme.variables.Status.CONFIRMED;
import static ru.practicum.explorewithme.variables.Status.PENDING;
import static ru.practicum.explorewithme.variables.Status.PUBLISHED;
import static ru.practicum.explorewithme.variables.Status.REJECTED;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventStateRepository eventStateRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    public EventRequestStatusUpdateResult changeUsersEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        String state = eventRequestStatusUpdateRequest.getStatus();
        List<Long> reqIds = eventRequestStatusUpdateRequest.getRequestIds();
        List<ParticipationRequestDto> participationRequestDtos = participationRequestRepository.findByIdInAndEvent_Initiator_Id(reqIds, userId).stream()
                .map(this::toParticipationRequestDto)
                .collect(Collectors.toList());
        reqIds = participationRequestDtos.stream()
                .map(ParticipationRequestDto::getId)
                .collect(Collectors.toList());
        if (state.equals(CONFIRMED.name())) {
            if (participationRequestDtos.size() > event.getParticipantLimit() - event.getConfirmedRequests()) {
                throw new ConflictException("Out of limit.");
            }
            event.setConfirmedRequests(event.getConfirmedRequests() + participationRequestDtos.size());

            eventRepository.updateConfirmedRequestsById(eventId, (long) participationRequestDtos.size());
            List<ParticipationRequest> requests = participationRequestRepository.findByIdIn(reqIds);
            EventState status = eventStateRepository.findByState(CONFIRMED);
            requests.forEach(r -> r.setStatus(status));
            requests.stream().map(participationRequestRepository::save);
            eventRepository.save(event);
            result.setConfirmedRequests(requests.stream()
                    .map(this::toParticipationRequestDto)
                    .collect(Collectors.toList()));
            return result;
        } else if (state.equals(REJECTED.name())) {
            if (participationRequestRepository.findByIdInAndStatus_State(eventRequestStatusUpdateRequest.getRequestIds(), CONFIRMED).size() > 0) {
                throw new ConflictException("Already confirmed");
            }
            List<ParticipationRequest> requests = participationRequestRepository.findByIdIn(reqIds);
            EventState status = eventStateRepository.findByState(REJECTED);
            requests.forEach(r -> r.setStatus(status));
            requests.stream().map(participationRequestRepository::save);
            result.setRejectedRequests(participationRequestDtos);
            result.setRejectedRequests(requests.stream()
                    .map(this::toParticipationRequestDto)
                    .collect(Collectors.toList()));
            eventRepository.save(event);
            return result;
        }
        return result;
    }

    public List<ParticipationRequestDto> getUsersEventRequests(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        return participationRequestRepository.findByEvent_Id(event.getId()).stream()
                .map(this::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> getUsersRequests(Long userId) {
        userRepository.findById(userId).orElseThrow();
        return participationRequestRepository.findByRequester_Id(userId).stream()
                .map(this::toParticipationRequestDto)
                .collect(Collectors.toList());
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
        if (!Objects.equals(event.getState().getState(), PUBLISHED)) {
            throw new ConflictException("Its not published event.");
        }
        if (event.getParticipantLimit() != 0 && participationRequestRepository.findByEvent_IdAndStatus_State(eventId, CONFIRMED).size() == event.getParticipantLimit()) {
            throw new ConflictException("Participation limit has been reached.");
        }
        ParticipationRequest participationRequestNew = new ParticipationRequest();
        participationRequestNew.setRequester(user);
        participationRequestNew.setEvent(event);
        if (event.getParticipantLimit() != 0) {
            if (event.getRequestModeration()) {
                participationRequestNew.setStatus(eventStateRepository.findByState(PENDING));
                participationRequestNew.setCreated(LocalDateTime.now());
            } else {
                participationRequestNew.setStatus(eventStateRepository.findByState(CONFIRMED));
                participationRequestNew.setCreated(LocalDateTime.now());
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            }
        } else {
            participationRequestNew.setStatus(eventStateRepository.findByState(CONFIRMED));
            participationRequestNew.setCreated(LocalDateTime.now());
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        eventRepository.save(event);
        return toParticipationRequestDto(participationRequestRepository.save(participationRequestNew));
    }

    public ParticipationRequestDto cancelUsersRequests(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow();
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId).orElseThrow();
        if (!Objects.equals(participationRequest.getRequester().getId(), userId)) {
            throw new RuntimeException();
        }
        participationRequest.setStatus(eventStateRepository.findByState(CANCELED));
        return toParticipationRequestDto(participationRequestRepository.save(participationRequest));
    }

    private ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(participationRequest.getRequester().getId());
        participationRequestDto.setEvent(participationRequest.getEvent().getId());
        participationRequestDto.setStatus(participationRequest.getStatus().getState().name());
        participationRequestDto.setCreated(df.format(participationRequest.getCreated()));
        participationRequestDto.setId(participationRequest.getId());
        return participationRequestDto;
    }
}
