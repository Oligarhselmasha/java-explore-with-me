package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.variables.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e where e.id in :ids")
    List<Event> findByIdIn(@Param("ids") Collection<Long> ids);

    @Query("select e from Event e where e.initiator.id = :id order by e.id")
    List<Event> findByInitiator_IdOrderByIdAsc(@Param("id") Long id);

    @Override
    Optional<Event> findById(Long id);

    @Query("select e from Event e where e.category.id = :id")
    List<Event> findByCategory_Id(@Param("id") Long id);

    @Query("select e from Event e " +
            "where e.initiator.id in :ids and e.state.state in :states and e.category.id in :ids2 and e.eventDate > :eventDate and e.eventDate < :eventDate1 " +
            "order by e.id")
    List<Event> findByInitiator_IdInAndState_StateInAndCategory_NameInAndEventDateAfterAndEventDateBeforeOrderByIdAsc(@Param("ids") Collection<Long> ids, @Param("states") Collection<Status> states, @Param("ids2") Collection<Long> ids2, @Param("eventDate") LocalDateTime eventDate, @Param("eventDate1") LocalDateTime eventDate1);

    @Transactional
    @Modifying
    @Query("update Event e set e.confirmedRequests = :confirmedRequests where e.id = :id")
    int updateConfirmedRequestsById(@Param("confirmedRequests") Long confirmedRequests, @Param("id") Long id);


}
