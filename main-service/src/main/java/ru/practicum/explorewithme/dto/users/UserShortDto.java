package ru.practicum.explorewithme.dto.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UserShortDto {
    @NotNull
    private Long id;

    @NotBlank
    @NotNull
    private String name;
}
