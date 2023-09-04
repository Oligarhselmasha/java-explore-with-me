package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Integer> {

    @Override
    List<EndpointHit> findAll();

}
