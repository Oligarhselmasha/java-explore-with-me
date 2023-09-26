package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.entity.User;
import ru.practicum.explorewithme.exceptions.ConflictException;
import ru.practicum.explorewithme.mapper.UserMapper;
import ru.practicum.explorewithme.repository.UserRepository;
import ru.practicum.explorewithme.dto.users.NewUserRequest;
import ru.practicum.explorewithme.dto.users.UserDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll().stream()
                    .map(userMapper::toUserDto)
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findByIdIn(ids).stream()
                    .map(userMapper::toUserDto)
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }
    }

    private Set<User> getSetUsers() {
        return new HashSet<>(userRepository.findAll());
    }
}
