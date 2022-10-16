package com.kubperf.kubernetesperformanceback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

////todo: wyrzucic exclude po ogarnieciu bazki
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
//todo: wyrzucic exclude po ogarnieciu bazki
@SpringBootApplication
public class KubernetesPerformanceBackApplication {
	// test comment
	public static void main(String[] args) {
		SpringApplication.run(KubernetesPerformanceBackApplication.class, args);
	}
}
