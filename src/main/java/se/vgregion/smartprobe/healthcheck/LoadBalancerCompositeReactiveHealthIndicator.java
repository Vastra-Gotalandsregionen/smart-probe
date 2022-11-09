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
        Status online = new Status("ONLINE");

        return Flux.fromStream(compositeReactiveHealthContributor.stream()).flatMap(c -> {
            ReactiveHealthContributor contributor = c.getContributor();

            if (contributor instanceof ReactiveHealthIndicator) {
                return ((ReactiveHealthIndicator) contributor).health();
            }

            return Mono.empty();
        }).reduce((h1, h2) -> {
            Status status1 = h1.getStatus();
            Status status2 = h2.getStatus();
            if ((status1.equals(Status.UP) || status1.equals(online))
                    && (status2.equals(Status.UP) || status2.equals(online))) {
                return Health.status(online).build();
            } else {
                return Health.status(new Status("OFFLINE"))
                        .build();
            }
        });
    }
}
