package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
