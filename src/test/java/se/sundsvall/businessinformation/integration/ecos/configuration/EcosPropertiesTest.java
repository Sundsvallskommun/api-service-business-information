package se.sundsvall.businessinformation.integration.ecos.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.businessinformation.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class EcosPropertiesTest {

	@Autowired
	private EcosProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(5);
		assertThat(properties.readTimeout()).isEqualTo(30);
		assertThat(properties.username()).isEqualTo("someUsername");
		assertThat(properties.password()).isEqualTo("somePassword");
	}
}
