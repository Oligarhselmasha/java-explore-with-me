package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.entity.Compilation;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("select c from Compilation c where c.pinned = :pinned")
    List<Compilation> findByPinned(@Param("pinned") Boolean pinned);

    @Override
    Optional<Compilation> findById(Long aLong);

    @Override
    void deleteById(Long aLong);
}
