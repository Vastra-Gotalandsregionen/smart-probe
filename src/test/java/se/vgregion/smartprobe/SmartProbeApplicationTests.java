package se.vgregion.smartprobe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SmartProbeApplicationTests {

	@Autowired
	CompositeReactiveHealthContributor compositeReactiveHealthContributor;

	@Test
	public void contextLoads() {
		assertNotNull(compositeReactiveHealthContributor);
	}

}
