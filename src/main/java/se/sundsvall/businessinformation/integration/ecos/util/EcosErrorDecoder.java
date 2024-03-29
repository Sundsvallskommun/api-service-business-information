package se.sundsvall.businessinformation.integration.ecos.util;

import java.io.IOException;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.dept44.exception.ClientProblem;

import feign.Response;
import feign.codec.ErrorDecoder;

public class EcosErrorDecoder implements ErrorDecoder {
    
    private final String soapProtocol;
    
    public EcosErrorDecoder() {
        this.soapProtocol = SOAPConstants.SOAP_1_1_PROTOCOL;
    }
    
    public Exception decode(String methodKey, Response response) {
        if (response.body() != null) {
            try {
                SOAPMessage message = MessageFactory.newInstance(this.soapProtocol).createMessage(null, response.body().asInputStream());
                if (message.getSOAPBody() != null &&
                    message.getSOAPBody().hasFault() &&
                    message.getSOAPBody().getFault().getFaultString().contains("Sequence contains no elements")) {
                    throw new ClientProblem(Status.NOT_FOUND, "No case was found in ECOS.");
                }
            } catch (IOException | SOAPException ignored) {
                return defaultError();
            }
        }
        return defaultError();
    }
    
    private ThrowableProblem defaultError() {
        return new ClientProblem(Status.BAD_REQUEST, "Bad request exception from MinutMiljo (Ecos)");
    }
}
