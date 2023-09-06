package ru.practicum.explorewithme;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class EventShortDto {
    @NotBlank
    @NotNull
    private String annotation;

    @NotNull
    private CategoryDto category;

    @NotNull
    private Integer hits;

    private Long confirmedRequests;

    @NotBlank
    @NotNull
    private String eventDate;

    private Long id;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Boolean paid;

    @NotBlank
    @NotNull
    private String title;

    private Long views;

}
