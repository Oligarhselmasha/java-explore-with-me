package ru.practicum.explorewithme.dto.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {

    private Long id;

    @NotBlank
    @NotNull
    @Size(max = 50)
    private String name;
}
