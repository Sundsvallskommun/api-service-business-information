package se.sundsvall.businessinformation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import se.sundsvall.dept44.util.jacoco.ExcludeFromJacocoGeneratedCoverageReport;

@EnableFeignClients
@SpringBootApplication
@ExcludeFromJacocoGeneratedCoverageReport
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
