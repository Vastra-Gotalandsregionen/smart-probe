package se.vgregion.smartprobe.properties;

import lombok.Data;

import java.util.Map;

//@Component
//@ConfigurationProperties("endpoints")
@Data
public class EndpointProperties {

    private Map<String, Endpoint> endpoints;
}
