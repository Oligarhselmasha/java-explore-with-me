package ru.practicum.explorewithme.stats;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ViewStatsDto {

    private String app;

    private String uri;

    private Integer hits;


    public ViewStatsDto(String api, String uri) {
        this.app = api;
        this.uri = uri;
    }
}
