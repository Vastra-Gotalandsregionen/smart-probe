package se.vgregion.smartprobe;

import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Configuration;
import se.vgregion.smartprobe.healthcheck.downstream.ProbeFileHealthIndicator;

import java.io.File;
import java.util.Map;

@Configuration
public class AppConfig extends BaseAppConfig {

    @Override
    protected Map<? extends String, ? extends ReactiveHealthIndicator> additionalHealthIndicators() {
        return Map.of("probeFile", new ProbeFileHealthIndicator());
    }

    @Override
    protected File getYamlFile() {
        return new File(System.getProperty("user.home") + "/.app/smart-probe/endpoints.yml");
    }

}
