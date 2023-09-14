package ru.practicum.explorewithme.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explorewithme.location.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @NotNull
    private String annotation;

    @NotNull
    private Long category;

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

    @NotBlank
    @NotNull
    private String title;

}
