package ru.practicum.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitDto;
import ru.practicum.httpclient.constants.StatConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClientImpl extends BaseClient implements StatsClient {

    @Autowired
    public StatsClientImpl(@Value("${stats-client.uri}") String uri, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(uri))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void saveHit(HitDto endpointHit) {
        post(StatConstants.HIT_ENDPOINT, endpointHit);
    }

    public ResponseEntity<Object> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return getStats(start, end, uris, null);
    }

    private ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Недопустимый временной промежуток.");
        }

        StringBuilder uriBuilder = new StringBuilder(StatConstants.STATS_ENDPOINT + "?start={start}&end={end}");
        Map<String, Object> parameters = Map.of(
                "start", start.format(StatConstants.DT_FORMATTER),
                "end", end.format(StatConstants.DT_FORMATTER)
        );

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }
        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }

        return get(uriBuilder.toString(), parameters);
    }

}