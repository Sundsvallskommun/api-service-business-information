package se.sundsvall.businessinformation.integration.ecos;

import java.nio.charset.StandardCharsets;

import javax.net.ssl.X509TrustManager;

import jakarta.xml.soap.SOAPConstants;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import se.sundsvall.businessinformation.integration.ecos.util.EcosErrorDecoder;
import se.sundsvall.businessinformation.integration.ecos.util.NTLMAuthenticator;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.security.Truststore;

import feign.Client;
import feign.jaxb.JAXBContextFactory;
import feign.okhttp.OkHttpClient;
import feign.soap.SOAPDecoder;
import feign.soap.SOAPEncoder;

@Import(FeignConfiguration.class)
public class EcosIntegrationConfiguration {
    
    private static final JAXBContextFactory JAXB_FACTORY = new JAXBContextFactory.Builder()
        .withMarshallerJAXBEncoding(StandardCharsets.UTF_8.toString())
        .build();
    
    private static final SOAPEncoder.Builder ENCODER_BUILDER = new SOAPEncoder.Builder()
        .withCharsetEncoding(StandardCharsets.UTF_8)
        .withFormattedOutput(false)
        .withJAXBContextFactory(JAXB_FACTORY)
        .withSOAPProtocol(SOAPConstants.SOAP_1_1_PROTOCOL)
        .withWriteXmlDeclaration(true);
    private final EcosIntegrationProperties properties;
    
    EcosIntegrationConfiguration(EcosIntegrationProperties properties) {
        this.properties = properties;
    }
    
    @Bean
    Client okHttpClient(final Truststore trustStore) {
        
        final var trustManagerFactory = trustStore.getTrustManagerFactory();
        final var trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
        
        return new OkHttpClient(new okhttp3.OkHttpClient.Builder()
            .sslSocketFactory(trustStore.getSSLContext().getSocketFactory(), trustManager)
            .authenticator(new NTLMAuthenticator(properties.getUsername(),
                properties.getPassword()))
            .build());
    }
    
    @Bean
    FeignBuilderCustomizer feignBuilderCustomizer(EcosIntegrationProperties properties) {
        return FeignMultiCustomizer.create()
            .withEncoder(ENCODER_BUILDER.build())
            .withDecoder(new SOAPDecoder(JAXB_FACTORY))
            .withErrorDecoder(new EcosErrorDecoder())
            .withRequestTimeoutsInSeconds(properties.getConnectTimeout(), properties.getReadTimeout())
            .composeCustomizersToOne();
    }
    
    
}
