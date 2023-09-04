package ru.practicum.explorewithme;

import javax.validation.constraints.NotNull;

public class EndpointHitDto {

    private Integer id;

    @NotNull
    private String app;

    @NotNull
    private String uri;

    @NotNull
    private String ip;

    @NotNull
    private String timestamp;

}
