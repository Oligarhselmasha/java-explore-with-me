package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.entity.EventState;

import java.util.List;
import java.util.Optional;

public interface EventStateRepository extends JpaRepository<EventState, Integer> {
    @Query("select e from EventState e where e.state = :state")
    EventState findByState(@Param("state") String state);


    @Override
    Optional<EventState> findById(Integer integer);
}
