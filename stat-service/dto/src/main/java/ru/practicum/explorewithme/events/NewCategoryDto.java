package ru.practicum.explorewithme.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class NewCategoryDto {

    @NotBlank
    @NotNull
    private String name;
}