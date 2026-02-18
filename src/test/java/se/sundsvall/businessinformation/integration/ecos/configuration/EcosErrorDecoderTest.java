package se.sundsvall.businessinformation.integration.ecos.configuration;

import feign.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.exception.ClientProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcosErrorDecoderTest {

	@InjectMocks
	private EcosErrorDecoder errorDecoder;

	@Test
	void shouldDecodeEmptyResponse() {
		// given
		final Response response = mock(Response.class);
		when(response.body()).thenReturn(null);
		when(response.reason()).thenReturn("Bad Request");

		// when
		final Exception decodedError = errorDecoder.decode("someMethodKey", response);

		// then
		assertThat(decodedError).isInstanceOf(ClientProblem.class);
		assertThat(decodedError.getMessage()).isEqualTo("Bad Request: Bad request exception from Ecos Bad Request");
	}

	@Test
	void shouldHandleIOException() throws IOException {
		// given
		final Response response = mock(Response.class);
		final Response.Body body = mock(Response.Body.class);
		when(response.body()).thenReturn(body);
		when(body.asInputStream()).thenThrow(new IOException("Failed to read response body"));

		// when
		final Exception decodedError = errorDecoder.decode("someMethodKey", response);

		// then
		assertThat(decodedError).isInstanceOf(ClientProblem.class);
		assertThat(decodedError.getMessage()).isEqualTo("Bad Request: Bad request exception from Ecos Failed to read response body");
	}

	@Test
	void shouldDecodeSOAPFault() throws Exception {
		// given
		final Response response = mock(Response.class);
		final InputStream inputStream = new ByteArrayInputStream(
			"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><soap:Fault><faultcode>soap:Server</faultcode><faultstring>Internal Server Error</faultstring></soap:Fault></soap:Body></soap:Envelope>".getBytes());
		final Response.Body body = mock(Response.Body.class);
		when(response.body()).thenReturn(body);
		when(body.asInputStream()).thenReturn(inputStream);

		final var decoder = new EcosErrorDecoder();

		// when
		final Exception decodedError = decoder.decode("someMethodKey", response);

		// then
		assertThat(decodedError).isInstanceOf(ClientProblem.class);
		assertThat(decodedError.getMessage()).isEqualTo("Bad Request: Bad request exception from Ecos Internal Server Error");
	}
}
