package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.Category;
import ru.practicum.explorewithme.entity.Location;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Override
    Optional<Category> findById(Long id);

    @Override
    List<Category> findAll();

    @Override
    void deleteById(Long aLong);
}
