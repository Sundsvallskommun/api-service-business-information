package se.sundsvall.businessinformation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BusinessInformationApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BusinessInformationApplication.class, args);
    }
    
}
