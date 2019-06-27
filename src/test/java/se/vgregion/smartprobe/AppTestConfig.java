package se.vgregion.smartprobe;

import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Map;

@Configuration
public class AppTestConfig extends BaseAppConfig {

    //    @Autowired
//    private ReactiveHealthIndicatorRegistry registry;

    @Override
    protected Map<? extends String, ? extends ReactiveHealthIndicator> additionalHealthIndicators() {
        return Map.of();
    }

    @Override
    protected File getYamlFile() {
        return new File(this.getClass().getClassLoader().getResource("test.yml").getFile());
    }
}
