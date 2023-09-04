package ru.practicum.explorewithme;

import javax.validation.constraints.NotNull;

public class ViewStatsDto {

    private String app;

    private String uri;

    private Integer hits;

    public ViewStatsDto(String api, String uri, Integer hits) {
        this.app = api;
        this.uri = uri;
        this.hits = hits;
    }

    public ViewStatsDto(String api, String uri) {
        this.app = api;
        this.uri = uri;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }
}
