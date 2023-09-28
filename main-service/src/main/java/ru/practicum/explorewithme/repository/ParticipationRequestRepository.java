package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.entity.ParticipationRequest;
import ru.practicum.explorewithme.variables.Status;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    @Query("select p from ParticipationRequest p where p.id in :ids and p.status.state = :state")
    List<ParticipationRequest> findByIdInAndStatus_State(@Param("ids") Collection<Long> ids, @Param("state") Status state);

    @Query("select p from ParticipationRequest p where p.id in :ids")
    List<ParticipationRequest> findByIdIn(@Param("ids") Collection<Long> ids);

    @Query("select p from ParticipationRequest p where p.event.id = :id and p.status.state = :state")
    List<ParticipationRequest> findByEvent_IdAndStatus_State(@Param("id") Long id, @Param("state") Status state);

    @Query("select p from ParticipationRequest p where p.requester.id = :id")
    List<ParticipationRequest> findByRequester_Id(@Param("id") Long id);

    @Query("select p from ParticipationRequest p where p.event.id = :id")
    List<ParticipationRequest> findByEvent_Id(@Param("id") Long id);

    @Override
    Optional<ParticipationRequest> findById(Long aLong);

    @Query("select p from ParticipationRequest p where p.event.id = :id and p.requester.id = :id1")
    ParticipationRequest findByEvent_IdAndRequester_Id(@Param("id") Long id, @Param("id1") Long id1);

    @Query("select p from ParticipationRequest p where p.id in :ids and p.event.initiator.id = :id")
    List<ParticipationRequest> findByIdInAndEvent_Initiator_Id(@Param("ids") Collection<Long> ids, @Param("id") Long id);

}
