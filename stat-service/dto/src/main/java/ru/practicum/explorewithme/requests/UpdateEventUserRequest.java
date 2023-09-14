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

    private String annotation;

    private CategoryDto category;

    private String description;

    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    private String title;
}
