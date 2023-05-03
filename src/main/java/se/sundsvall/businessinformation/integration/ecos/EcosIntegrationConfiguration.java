package se.sundsvall.businessinformation.integration.ecos;

import org.apache.http.auth.NTCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.zalando.logbook.Logbook;
import se.sundsvall.dept44.configuration.webservicetemplate.WebServiceTemplateBuilder;

@Configuration
public class EcosIntegrationConfiguration {


    private static final Logger LOG = LoggerFactory.getLogger(EcosIntegrationConfiguration.class);

    private final EcosIntegrationProperties properties;
    private final Logbook logbook;

    @Autowired
    public EcosIntegrationConfiguration(final EcosIntegrationProperties properties, Logbook logbook) {
        this.properties = properties;
        this.logbook = logbook;
    }


    //@Bean
    public HttpComponentsMessageSender httpComponentsMessageSender() {
        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        NTCredentials credentials = new NTCredentials(properties.getUsername(),
                properties.getPassword(), null, null);
        httpComponentsMessageSender.setCredentials(credentials);
        return httpComponentsMessageSender;
    }

    @Bean(name = "minutmiljo-webservice-template")
    public WebServiceTemplate getMinutmiljoWebserviceTemplate() {

        final WebServiceTemplateBuilder builder =
                new WebServiceTemplateBuilder().withBaseUrl(properties.getUrl())
                        .withPackageToScan("minutmiljo")
                        .withLogbook(logbook);
        var stuff = builder.build();
        stuff.setMessageSender(httpComponentsMessageSender());
        return stuff;
    }

}
