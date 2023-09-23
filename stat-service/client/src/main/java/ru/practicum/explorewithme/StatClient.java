package ru.practicum.explorewithme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.stats.EndpointHitDto;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import static ru.practicum.explorewithme.variables.Constants.*;


@Service
public class StatClient extends BaseClient {


    @Autowired
    public StatClient(@Value("${client.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createHit(EndpointHitDto endpointHitDto) {
        return post("/hit/", endpointHitDto);
    }

    public ResponseEntity<Object> getStats(Long id) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        Map<String, Object> parameters = Map.of(
                "start", df.format(MIN_DATE),
                "end", df.format(MAX_DATE),
                "uris", "/events/" + id
        );
        return get("/stats?end={end}&start={start}&uris={uris}", parameters);
    }
}
