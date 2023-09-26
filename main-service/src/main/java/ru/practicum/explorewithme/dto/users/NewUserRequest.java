package ru.practicum.explorewithme.dto.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank
    @NotNull
    @Email
    @Size(min = 6, max = 254)
    private String email;

    @NotBlank
    @NotNull
    @Size(min = 2, max = 250)
    private String name;
}
