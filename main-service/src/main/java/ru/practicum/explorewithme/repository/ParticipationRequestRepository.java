package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.entity.ParticipationRequest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    @Query("select p from ParticipationRequest p where p.id in :ids and p.status.state = :state")
    List<ParticipationRequest> findByIdInAndStatus_State(@Param("ids") Collection<Long> ids, @Param("state") String state);

    @Query("select p from ParticipationRequest p where p.event.id = :id and p.status.state = :state")
    List<ParticipationRequest> findByEvent_IdAndStatus_State(@Param("id") Long id, @Param("state") String state);

    @Query("select p from ParticipationRequest p where p.requester.id = :id")
    List<ParticipationRequest> findByRequester_Id(@Param("id") Long id);

    @Query("select p from ParticipationRequest p where p.event.id = :id")
    List<ParticipationRequest> findByEvent_Id(@Param("id") Long id);

    @Override
    Optional<ParticipationRequest> findById(Long aLong);

    @Query("select p from ParticipationRequest p where p.id = :id and p.event.initiator.id = :id1")
    ParticipationRequest findByIdAndEvent_Initiator_Id(@Param("id") Long id, @Param("id1") Long id1);

    @Query("select p from ParticipationRequest p where p.event.id = :id and p.requester.id = :id1")
    ParticipationRequest findByEvent_IdAndRequester_Id(@Param("id") Long id, @Param("id1") Long id1);
}
