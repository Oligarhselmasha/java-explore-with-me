package ru.practicum.explorewithme;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class EventFullDto {
    @NotBlank
    @NotNull
    private String annotation;

    @NotNull
    private CategoryDto category;

    private Long confirmedRequests;

    private String createdOn;

    private String description;

    @NotBlank
    @NotNull
    private String eventDate;

    private Long id;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Location location;

    @NotNull
    private Boolean paid;

    private Integer participantLimit = 0;

    private String publishedOn;

    private Boolean requestModeration;

    private String state;

    @NotBlank
    @NotNull
    private String title;

    private Long views;
}
