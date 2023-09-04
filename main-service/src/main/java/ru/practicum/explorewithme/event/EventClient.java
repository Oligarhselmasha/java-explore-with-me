package ru.practicum.explorewithme.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.client.BaseClient;

import java.util.List;

public class EventClient extends BaseClient {
    private static final String API_PREFIX = "/events";
    @Autowired
    public EventClient(@Value("${client.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getEvent(Integer userId, Integer eventId) {
        return null;
    }

    public List<ResponseEntity<Object>> getEvents(Integer userId) {
        return null;
    }
}
