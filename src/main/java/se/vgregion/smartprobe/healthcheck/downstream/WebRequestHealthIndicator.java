package se.vgregion.smartprobe.healthcheck.downstream;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;

public class WebRequestHealthIndicator implements ReactiveHealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebRequestHealthIndicator.class);

    private final String host;
    private String url;

    public WebRequestHealthIndicator(String baseUrl, String host) {
        this.url = baseUrl;
        this.host = host;
    }

    @Override
    public Mono<Health> health() {
        return HttpClient
                .create()
                .headers(entries -> entries.add("Host", host))
                .get()
                .uri(url)
                .responseSingle((r, body) -> Mono.just(r).zipWith(body.asString().defaultIfEmpty("")))
                .map(tuple2 -> {
                    HttpClientResponse httpClientResponse = tuple2.getT1();
                    if (httpClientResponse.status().equals(HttpResponseStatus.OK)) {
                        return new Health.Builder().up()
                                .withDetail("statusCode", httpClientResponse.status().code())
                                .withDetail("url", this.url)
                                .withDetail("host", this.host)
                                .withDetail("body", tuple2.getT2().substring(0, Math.min(100, tuple2.getT2().length())))
                                .build();
                    } else {
                        return new Health.Builder().down()
                                .withDetail("statusCode", httpClientResponse.status().code())
                                .withDetail("reason", httpClientResponse.status().reasonPhrase())
                                .withDetail("url", this.url)
                                .withDetail("host", this.host)
                                .withDetail("body", tuple2.getT2().substring(0, Math.min(100, tuple2.getT2().length())))
                                .build();
                    }
                })
                .onErrorResume(ex -> {
                    LOGGER.error(ex.getMessage(), ex);
                    return Mono.just(new Health.Builder().down(ex).build());
                });
    }

    @Data
    @AllArgsConstructor
    static class ResponseWithBody {
        private HttpClientResponse httpClientResponse;
        private ByteBufFlux byteBufFlux;
    }
}
