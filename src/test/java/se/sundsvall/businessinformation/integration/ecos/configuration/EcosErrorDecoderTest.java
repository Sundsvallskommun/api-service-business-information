package se.sundsvall.businessinformation.integration.ecos.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.soap.SOAPMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se.sundsvall.dept44.exception.ClientProblem;

import feign.Response;

class EcosErrorDecoderTest {

	private EcosErrorDecoder errorDecoder;

	@BeforeEach
	void setUp() {
		errorDecoder = new EcosErrorDecoder();
	}

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
		final InputStream inputStream = new ByteArrayInputStream("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><soap:Fault><faultcode>soap:Server</faultcode><faultstring>Internal Server Error</faultstring></soap:Fault></soap:Body></soap:Envelope>".getBytes());
		final Response.Body body = mock(Response.Body.class);
		when(response.body()).thenReturn(body);
		when(body.asInputStream()).thenReturn(inputStream);

		final var decoder = new EcosErrorDecoder() {

			@Override
			protected SOAPMessage createSOAPMessage(final InputStream inputStream) throws SOAPException, IOException {
				final SOAPMessage message = mock(SOAPMessage.class);
				final SOAPBody soapBody = mock(SOAPBody.class);
				final SOAPFault soapFault = mock(SOAPFault.class);
				when(soapBody.hasFault()).thenReturn(true);
				when(soapBody.getFault()).thenReturn(soapFault);
				when(soapFault.getFaultString()).thenReturn("Internal Server Error");
				when(message.getSOAPBody()).thenReturn(soapBody);
				return message;
			}
		};

		// when
		final Exception decodedError = decoder.decode("someMethodKey", response);

		// then
		assertThat(decodedError).isInstanceOf(ClientProblem.class);
		assertThat(decodedError.getMessage()).isEqualTo("Bad Request: Bad request exception from Ecos Internal Server Error");
	}

}
