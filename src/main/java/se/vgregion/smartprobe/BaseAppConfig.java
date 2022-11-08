package se.vgregion.smartprobe;

import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.yaml.snakeyaml.Yaml;
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

    @Bean
    protected CompositeReactiveHealthContributor createHealthIndicator() {
        EndpointProperties endpointProperties = getEndpointProperties();

        Map<String, ReactiveHealthContributor> beans = new HashMap<>();

        for (Map.Entry<String, Endpoint> entry : endpointProperties.getEndpoints().entrySet()) {
            Endpoint endpoint = entry.getValue();
            beans.put(entry.getKey(), new WebRequestHealthIndicator(endpoint.getUrl(), endpoint.getHost()));
        }

        beans.putAll(additionalHealthIndicators());

        return CompositeReactiveHealthContributor.fromMap(
                beans
        );
    }

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
