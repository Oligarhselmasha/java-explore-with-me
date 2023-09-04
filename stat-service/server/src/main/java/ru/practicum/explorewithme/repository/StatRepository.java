package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.EndpointHit;

import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Integer> {

    @Override
    List<EndpointHit> findAll();

}
