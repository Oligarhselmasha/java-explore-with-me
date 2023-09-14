package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.entity.User;
import ru.practicum.explorewithme.mapper.UserMapper;
import ru.practicum.explorewithme.repository.UserRepository;
import ru.practicum.explorewithme.users.NewUserRequest;
import ru.practicum.explorewithme.users.UserDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void removeUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public UserDto postNewUser(NewUserRequest newUserRequest) {
        User user = userMapper.fromNewUserRequest(newUserRequest);
        return userMapper.toUserDto(userRepository.save(user));
    }

    public List<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        List<UserDto> userDtos = new ArrayList<>();
        List<Long> usersIds = Arrays.stream(ids).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (usersIds.isEmpty()) {
            List<User> usersList = userRepository.findAll();
            usersList.forEach(user -> usersIds.add(user.getId()));
        }
        List<User> users = userRepository.findByIdInOrderByIdAsc(usersIds);
        if (users.size() <= size) {
            size = users.size();
        }
        users = new ArrayList<>(users).subList(from, size);
        users.forEach(user -> userDtos.add(userMapper.toUserDto(user)));
        return userDtos;
    }
}
