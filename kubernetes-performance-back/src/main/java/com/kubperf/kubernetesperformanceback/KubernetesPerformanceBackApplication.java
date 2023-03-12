package com.kubperf.kubernetesperformanceback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

////todo: wyrzucic exclude po ogarnieciu bazki
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
//todo: wyrzucic exclude po ogarnieciu bazki
@SpringBootApplication
public class KubernetesPerformanceBackApplication {
	// test comment
	public static void main(String[] args) {
		SpringApplication.run(KubernetesPerformanceBackApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
