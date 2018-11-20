package com.accounting;

import org.springframework.boot.ApplicationHome;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RestController;


/*@RestController
@EnableAutoConfiguration
@SpringBootApplication
@EnableJpaAuditing
public class AccountingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountingApplication.class, args);
	}
}
*/

@EnableAutoConfiguration
@SpringBootApplication
@EnableJpaAuditing
public class AccountingApplication extends SpringBootServletInitializer{
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AccountingApplication.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(AccountingApplication.class, args);
		
	}
	
}

