package ru.practicum.explorewithme.dto.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NewCompilationDto {
    private List<Long> events;

    private Boolean pinned;

    @NotNull
    @NotBlank
    @Size(max = 50)
    private String title;
}
