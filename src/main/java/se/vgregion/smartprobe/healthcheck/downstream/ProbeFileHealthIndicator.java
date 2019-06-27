package se.vgregion.smartprobe.healthcheck.downstream;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.BaseStream;

public class ProbeFileHealthIndicator implements ReactiveHealthIndicator {

    @Override
    public Mono<Health> health() {
        Flux<String> using = Flux.using(
                () -> Files.lines(Path.of(System.getProperty("user.home"), "probe.html")),
                Flux::fromStream,
                BaseStream::close
        );

        return using
                .any(line -> line.toUpperCase().contains("ONLINE"))
                .map(any -> any ? Health.up().build() : Health.down().build());
    }

}
