package ru.practicum.explorewithme.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCompilationRequest {
    private Long[] events;

    private Boolean pinned;

    private String title;
}
