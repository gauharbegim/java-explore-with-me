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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClientImpl extends BaseClient implements StatsClient {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClientImpl(@Value("${stats-client.uri}") String uri, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(uri))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    @Override
    public void saveHit(HitDto hit) {
        post("/hit", hit);
    }

    @Override
    public ResponseEntity<Object> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return get(encodeTime(start), encodeTime(end), uris, unique);
    }

    private String encodeTime(LocalDateTime time) {
        String str = time.format(formatter);
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    private ResponseEntity<Object> get(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("start", start);
        parameter.put("end", end);
        parameter.put("uris", uris);
        parameter.put("unique", unique);
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameter);
    }
}