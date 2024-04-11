package se.sundsvall.businessinformation.integration.ecos.configuration;

import static org.zalando.problem.Status.BAD_REQUEST;

import java.io.IOException;
import java.io.InputStream;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

import org.zalando.problem.ThrowableProblem;

import se.sundsvall.dept44.exception.ClientProblem;

import feign.Response;
import feign.codec.ErrorDecoder;

public class EcosErrorDecoder implements ErrorDecoder {


	private final String soapProtocol;

	public EcosErrorDecoder() {
		this.soapProtocol = SOAPConstants.SOAP_1_1_PROTOCOL;
	}

	@Override
	public Exception decode(final String methodKey, final Response response) {
		if (response.body() != null) {
			try {
				final SOAPMessage message = createSOAPMessage(response.body().asInputStream());

				if ((message.getSOAPBody() != null) &&
					message.getSOAPBody().hasFault()) {
					return defaultError(message.getSOAPBody().getFault().getFaultString());
				}

			} catch (final IOException | SOAPException exception) {
				return defaultError(exception.getMessage());
			}
		}
		return defaultError(response.reason());
	}

	protected SOAPMessage createSOAPMessage(final InputStream inputStream) throws SOAPException, IOException {
		return MessageFactory.newInstance(this.soapProtocol).createMessage(null, inputStream);
	}

	private ThrowableProblem defaultError(final String message) {
		return new ClientProblem(BAD_REQUEST, "Bad request exception from Ecos " + message);
	}

}
