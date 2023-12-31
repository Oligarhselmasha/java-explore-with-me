package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.entity.User;
import ru.practicum.explorewithme.dto.users.NewUserRequest;
import ru.practicum.explorewithme.dto.users.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    User fromNewUserRequest(NewUserRequest newUserRequest);
}
