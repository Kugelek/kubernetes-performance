package com.kubperf.kubernetesperformanceback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//todo: wyrzucic exclude po ogarnieciu bazki
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class KubernetesPerformanceBackApplication {
	public static void main(String[] args) {
		SpringApplication.run(KubernetesPerformanceBackApplication.class, args);
	}
}
