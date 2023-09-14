package ru.practicum.explorewithme.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NewCompilationDto {
    private List<Long> events;

    @NotNull
    private Boolean pinned;

    @NotNull
    @NotBlank
    private String title;
}
