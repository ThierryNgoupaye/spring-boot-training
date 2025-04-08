package tech.formation.springSecurityTraining;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication()
public class SecurityImplementationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityImplementationApplication.class, args);
	}

}
