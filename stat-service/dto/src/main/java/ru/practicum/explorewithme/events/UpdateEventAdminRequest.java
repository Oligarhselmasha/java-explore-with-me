package ru.practicum.explorewithme.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explorewithme.location.LocationDto;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEventAdminRequest {

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
