package se.vgregion.smartprobe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.DefaultReactiveHealthIndicatorRegistry;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.ReactiveHealthIndicatorRegistry;
import org.springframework.context.annotation.Bean;
import org.yaml.snakeyaml.Yaml;
import se.vgregion.smartprobe.healthcheck.LoadBalancerCompositeReactiveHealthIndicator;
import se.vgregion.smartprobe.healthcheck.downstream.WebRequestHealthIndicator;
import se.vgregion.smartprobe.properties.Endpoint;
import se.vgregion.smartprobe.properties.EndpointProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseAppConfig {
    @Autowired
    private HealthAggregator healthAggregator;

    @Bean
    protected ReactiveHealthIndicator createHealthIndicator() {
        EndpointProperties endpointProperties = getEndpointProperties();

        Map<String, ReactiveHealthIndicator> beans = new HashMap<>();

        for (Map.Entry<String, Endpoint> entry : endpointProperties.getEndpoints().entrySet()) {
            Endpoint endpoint = entry.getValue();
            beans.put(entry.getKey(), new WebRequestHealthIndicator(endpoint.getUrl(), endpoint.getHost()));
        }

        beans.putAll(additionalHealthIndicators());

        ReactiveHealthIndicatorRegistry registry = new DefaultReactiveHealthIndicatorRegistry(beans);

        return new LoadBalancerCompositeReactiveHealthIndicator(BaseAppConfig.this.healthAggregator, registry) /*{

            @Override
            public Mono<Health> health() {
                return super.health().map(health -> {
                    String statusText = health.getStatus().getCode().equals("UP") ? "ONLINE" : "OFFLINE";
                    return Health.status(health.getStatus())
                            .withDetail("lbStatus", statusText)
                            .withDetails(health.getDetails()).build();
                });
            }
        }*/;
    }

    @Bean
    public EndpointProperties getEndpointProperties() {
        EndpointProperties endpointProperties;
        try (InputStream inputStream = new FileInputStream(getYamlFile())) {

            Yaml yaml = new Yaml();

            endpointProperties = yaml.loadAs(inputStream, EndpointProperties.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return endpointProperties;
    }

    protected abstract Map<? extends String,? extends ReactiveHealthIndicator> additionalHealthIndicators();

    protected abstract File getYamlFile();
}
