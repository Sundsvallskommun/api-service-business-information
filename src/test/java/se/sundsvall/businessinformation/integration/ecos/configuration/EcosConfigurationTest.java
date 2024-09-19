package se.sundsvall.businessinformation.integration.ecos.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import se.sundsvall.dept44.security.Truststore;

@ExtendWith(MockitoExtension.class)
class EcosConfigurationTest {

	@Mock
	private TrustManagerFactory trustManagerFactory;

	@Mock
	private X509TrustManager x509TrustManager;

	@Mock
	private SSLSocketFactory sslSocketFactory;

	@Mock
	private SSLContext sslContext;

	@Mock
	private EcosProperties ecosProperties;

	@Mock
	private Truststore trustStore;

	@Mock
	private Route route;

	@Mock
	private Response response;

	@InjectMocks
	private EcosConfiguration ecosConfiguration;

	@Test
	void okHttpClient() {

		// Arrange
		when(trustStore.getTrustManagerFactory()).thenReturn(trustManagerFactory);
		when(trustStore.getSSLContext()).thenReturn(sslContext);
		when(trustStore.getSSLContext().getSocketFactory()).thenReturn(sslSocketFactory);
		when(trustManagerFactory.getTrustManagers()).thenReturn(new TrustManager[] { x509TrustManager });
		when(x509TrustManager.getAcceptedIssuers()).thenReturn(new X509Certificate[] {}); // Empty array

		// Act
		final var result = ecosConfiguration.okHttpClient(trustStore);

		// Assert
		assertThat(result).isNotNull();
	}

	@Test
	void feignBuilderCustomizer() {

		// Act
		final var result = ecosConfiguration.feignBuilderCustomizer(ecosProperties);

		// Assert
		assertThat(result).isNotNull();
	}

	@ParameterizedTest
	@ValueSource(strings = { "NTLM", "NTLM TlRMTVNTUAACAAAAAAAAACgAAAABggAAU3J2Tm9uY2UAAAAAAAAAAA==", "somethingElse" })
	void ntlmAuthenticator(final String value) {

		// Arrange
		final var authenticator = new EcosConfiguration.NTLMAuthenticator("username", "password");

		// Mock
		when(response.headers()).thenReturn(new okhttp3.Headers.Builder().add("WWW-Authenticate", value).build());
		when(response.request()).thenReturn(new Request.Builder().url("http://localhost").build());

		// Act
		final var result = authenticator.authenticate(route, response);

		// Assert
		assertThat(result).isNotNull().satisfies(req -> assertThat(req.header("Authorization")).startsWith("NTLM"));
	}
}
