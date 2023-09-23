package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.ParticipationRequest;
import ru.practicum.explorewithme.entity.User;
import ru.practicum.explorewithme.exceptions.ConflictException;
import ru.practicum.explorewithme.mapper.ParticipationRequestMapper;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.EventStateRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;
import ru.practicum.explorewithme.requests.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.requests.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.requests.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventStateRepository eventStateRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final ParticipationRequestRepository participationRequestRepository;

    public EventRequestStatusUpdateResult changeUsersEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userRepository.findById(userId).orElseThrow();
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
            participationRequestDtos.forEach(p -> p.setStatus(participationRequestRepository.findById(p.getId()).orElseThrow().getStatus().getState().name()));
            eventRequestStatusUpdateResult.setConfirmedRequests(participationRequestDtos);
        } else if (state.equals("REJECTED")) {
            if (participationRequestRepository.findByIdInAndStatus_State(eventRequestStatusUpdateRequest.getRequestIds(), "CONFIRMED").size() > 0) {
                throw new ConflictException("Already confirmed");
            }
            participationRequests.forEach(p -> p.setStatus(eventStateRepository.findByState("REJECTED")));
            participationRequests.forEach(participationRequestRepository::save);
            participationRequestDtos.forEach(p -> p.setEvent(participationRequestRepository.findById(p.getId()).orElseThrow().getEvent().getId()));
            participationRequestDtos.forEach(p -> p.setRequester(participationRequestRepository.findById(p.getId()).orElseThrow().getRequester().getId()));
            participationRequestDtos.forEach(p -> p.setStatus(participationRequestRepository.findById(p.getId()).orElseThrow().getStatus().getState().name()));
            eventRequestStatusUpdateResult.setRejectedRequests(participationRequestDtos);
        }
        return eventRequestStatusUpdateResult;
    }

    public List<ParticipationRequestDto> getUsersEventRequests(Long userId, Long eventId) {
        List<ParticipationRequestDto> participationRequestDtos = new ArrayList<>();
        userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        List<ParticipationRequest> participationRequests = participationRequestRepository.findByEvent_Id(event.getId());
        participationRequests.forEach(p -> participationRequestDtos.add(participationRequestMapper.toParticipationRequestDto(p)));
        participationRequestDtos.forEach(p -> p.setRequester(participationRequestRepository.findById(p.getId()).orElseThrow().getRequester().getId()));
        participationRequestDtos.forEach(p -> p.setStatus(participationRequestRepository.findById(p.getId()).orElseThrow().getStatus().getState().name()));
        participationRequestDtos.forEach(p -> p.setEvent(participationRequestRepository.findById(p.getId()).orElseThrow().getEvent().getId()));
        return participationRequestDtos;
    }


    public List<ParticipationRequestDto> getUsersRequests(Long userId) {
        userRepository.findById(userId).orElseThrow();
        List<ParticipationRequestDto> participationRequestDtos = new ArrayList<>();
        List<ParticipationRequest> participationRequests = participationRequestRepository.findByRequester_Id(userId);
        participationRequests.forEach(r -> participationRequestDtos.add(participationRequestMapper.toParticipationRequestDto(r)));
        participationRequestDtos.forEach(p -> p.setRequester(participationRequestRepository.findById(p.getId()).orElseThrow().getRequester().getId()));
        participationRequestDtos.forEach(p -> p.setStatus(participationRequestRepository.findById(p.getId()).orElseThrow().getStatus().getState().name()));
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
        participationRequestDto.setStatus(eventStateRepository.findByState(participationRequestSaved.getStatus().getState().name()).getState().name());
        return participationRequestDto;
    }

    public ParticipationRequestDto cancelUsersRequests(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow();
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
}
