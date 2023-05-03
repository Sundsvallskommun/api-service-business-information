package se.sundsvall.businessinformation.integration.ecos;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("integration.ecos")
class EcosIntegrationProperties {
    private int connectTimeout;
    private int readTimeout;
    private String url;
    private String username;
    private String password;
    
}
