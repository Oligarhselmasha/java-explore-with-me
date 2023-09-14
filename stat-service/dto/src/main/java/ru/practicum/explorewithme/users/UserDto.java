package ru.practicum.explorewithme.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    @NotBlank
    @NotNull
    @Email
    private String email;

    private Long id;

    @NotBlank
    @NotNull
    private String name;
}
