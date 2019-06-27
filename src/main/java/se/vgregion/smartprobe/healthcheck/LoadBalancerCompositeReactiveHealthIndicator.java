package se.vgregion.smartprobe.healthcheck;

import org.springframework.boot.actuate.health.CompositeReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.ReactiveHealthIndicatorRegistry;
import reactor.core.publisher.Mono;

public class LoadBalancerCompositeReactiveHealthIndicator extends CompositeReactiveHealthIndicator {

    public LoadBalancerCompositeReactiveHealthIndicator(HealthAggregator healthAggregator,
                                                        ReactiveHealthIndicatorRegistry registry) {
        super(healthAggregator, registry);
    }

    @Override
    public Mono<Health> health() {
        return super.health().map(health -> {
            String statusText = health.getStatus().getCode().equals("UP") ? "ONLINE" : "OFFLINE";
            return Health.status(health.getStatus())
                    .withDetail("lbStatus", statusText)
                    .withDetails(health.getDetails()).build();
        });
    }
}
