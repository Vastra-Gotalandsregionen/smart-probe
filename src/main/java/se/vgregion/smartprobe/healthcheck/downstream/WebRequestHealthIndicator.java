package se.vgregion.smartprobe.healthcheck.downstream;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;

import java.util.Map;
import java.util.stream.Stream;

//@Component
public class WebRequestHealthIndicator implements ReactiveHealthIndicator {

    private final String host;
    private String baseUrl;

    public WebRequestHealthIndicator(String baseUrl, String host) {
        this.baseUrl = baseUrl;
        this.host = host;
    }

    @Override
    public Mono<Health> health() {
        return HttpClient
                .create()
                .baseUrl(baseUrl)
                .headers(entries -> entries.add("Host", host))
                .request(HttpMethod.GET)
                .responseSingle((r, body) -> Mono.just(r).zipWith(body.asString().defaultIfEmpty("")))
//                .response()
                .map(tuple2 -> {
                    HttpClientResponse httpClientResponse = tuple2.getT1();
                    if (httpClientResponse.status().equals(HttpResponseStatus.OK)) {
                        return new Health.Builder().up()
                                .withDetail("statusCode", httpClientResponse.status().code())
                                .withDetail("url", this.baseUrl)
                                .withDetail("host", this.host)
                                .withDetail("body", tuple2.getT2().substring(0, 100))
                                .build();
                    } else {
                        return new Health.Builder().down()
                                .withDetail("statusCode", httpClientResponse.status().code())
                                .withDetail("reason", httpClientResponse.status().reasonPhrase())
                                .withDetail("url", this.baseUrl)
                                .withDetail("host", this.host)
                                .withDetail("body", tuple2.getT2().substring(0, 100))
                                .build();
                    }
                })
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()));

    }

    @Data
    @AllArgsConstructor
    static class ResponseWithBody {
        private HttpClientResponse httpClientResponse;
        private ByteBufFlux byteBufFlux;
    }

    private Health upHealth(HttpClientResponse response) {
//        String body = response.currentContext().get(String.class);
        return new Health.Builder().up().build();
//        return new Health.Builder().status("ONLINE").build();
    }

    /*private Mono<Health> checkDownstreamServiceHealth() {
        // we could use WebClient to check health reactively
//        return Mono.just(new Health.Builder().down().build());
        return Mono.just(upHealth());
    }*/
    private Mono<Health> checkDownstreamServiceHealth2() {
        // we could use WebClient to check health reactively
//        return Mono.just(new Health.Builder().down().build());
        return Mono.just(new Health.Builder().down().build());
    }
}
