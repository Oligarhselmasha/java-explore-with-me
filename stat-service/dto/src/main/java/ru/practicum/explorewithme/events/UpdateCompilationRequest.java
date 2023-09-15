package ru.practicum.explorewithme.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCompilationRequest {
    private Long[] events;

    private Boolean pinned;

    @Size(max = 50)
    private String title;
}
