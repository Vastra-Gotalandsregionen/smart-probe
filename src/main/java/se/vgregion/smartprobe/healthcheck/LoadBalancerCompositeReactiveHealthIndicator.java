package se.vgregion.smartprobe.healthcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Order(0)
public class LoadBalancerCompositeReactiveHealthIndicator implements ReactiveHealthIndicator {

    @Autowired
    private CompositeReactiveHealthContributor compositeReactiveHealthContributor;

    public LoadBalancerCompositeReactiveHealthIndicator() {
    }

    @Override
    public Mono<Health> health() {
        return Flux.fromStream(compositeReactiveHealthContributor.stream()).flatMap(c -> {
            ReactiveHealthContributor contributor = c.getContributor();

            if (contributor instanceof ReactiveHealthIndicator) {
                return ((ReactiveHealthIndicator) contributor).health();
            }

            return Mono.empty();
        }).reduce((h1, h2) -> {
            if (h1.getStatus().equals(Status.UP) && h2.getStatus().equals(Status.UP)) {
                return Health.status(new Status("ONLINE")).build();
            } else {
                return Health.status(new Status("OFFLINE"))
                        .build();
            }
        });
    }
}
