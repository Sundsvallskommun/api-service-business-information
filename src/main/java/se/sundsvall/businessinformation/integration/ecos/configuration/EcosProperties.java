package se.sundsvall.businessinformation.integration.ecos.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.ecos")
public record EcosProperties(int connectTimeout, int readTimeout, String username, String password) {}
