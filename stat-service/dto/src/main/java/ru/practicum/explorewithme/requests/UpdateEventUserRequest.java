package ru.practicum.explorewithme.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explorewithme.events.CategoryDto;
import ru.practicum.explorewithme.location.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEventUserRequest {
    @NotBlank
    @NotNull
    private String annotation;

    @NotNull
    private CategoryDto category;

    private String description;

    @NotBlank
    @NotNull
    private String eventDate;

    @NotNull
    private LocationDto location;

    @NotNull
    private Boolean paid;

    private Integer participantLimit = 0;

    private Boolean requestModeration;

    private String stateAction;

    @NotBlank
    @NotNull
    private String title;
}
