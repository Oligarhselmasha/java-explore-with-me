package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.EndpointHit;

public interface StatRepository extends JpaRepository<EndpointHit, Integer> {

}
