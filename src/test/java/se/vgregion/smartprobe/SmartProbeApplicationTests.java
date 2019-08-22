package se.vgregion.smartprobe;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.vgregion.smartprobe.properties.EndpointProperties;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmartProbeApplicationTests {

	@Autowired
	EndpointProperties endpointProperties;

	@Test
	public void contextLoads() {
		assertNotNull(endpointProperties);
	}

}
