package ru.practicum.explorewithme;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserShortDto {
    @NotNull
    private Long id;

    @NotBlank
    @NotNull
    private String name;
}
