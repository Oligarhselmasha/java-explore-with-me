package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.entity.User;
import ru.practicum.explorewithme.exceptions.ConflictException;
import ru.practicum.explorewithme.mapper.UserMapper;
import ru.practicum.explorewithme.repository.UserRepository;
import ru.practicum.explorewithme.users.NewUserRequest;
import ru.practicum.explorewithme.users.UserDto;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void removeUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public UserDto postNewUser(NewUserRequest newUserRequest) {
        if (userRepository.findByName(newUserRequest.getName()) != null) {
            throw new ConflictException("user already exist");
        }
        User user = userMapper.fromNewUserRequest(newUserRequest);
        return userMapper.toUserDto(userRepository.save(user));
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<UserDto> userDtos = new ArrayList<>();
        Set<Long> usersIds = new HashSet<>();
        if (ids != null) {
            usersIds.addAll(ids);
        } else {
            Set<User> usersList = getSetUsers();
            usersList.stream()
                    .forEach(user -> usersIds.add(user.getId()));
        }
        List<User> users = userRepository.findByIdIn(usersIds);
        if (users.size() <= size) {
            size = users.size();
        }
        users = new ArrayList<>(users).subList(from, size);
        users.forEach(user -> userDtos.add(userMapper.toUserDto(user)));
        return userDtos;
    }

    private Set<User> getSetUsers() {
        return new HashSet<>(userRepository.findAll());
    }
}
