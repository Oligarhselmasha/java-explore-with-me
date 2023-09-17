package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(Long id);

    @Override
    void deleteById(Long aLong);

    @Query("select u from User u where u.id in :ids order by u.id")
    List<User> findByIdInOrderByIdAsc(@Param("ids") Collection<Long> ids);

    @Query("select u from User u where u.id in :ids")
    List<User> findByIdIn(@Param("ids") Collection<Long> ids);

    @Query("select u from User u where u.name = :name")
    User findByName(@Param("name") String name);



}
