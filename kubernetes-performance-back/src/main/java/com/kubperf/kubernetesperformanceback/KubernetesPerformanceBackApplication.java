package com.kubperf.kubernetesperformanceback;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// todo: wyrzucic exclude po ogarnieciu bazki
// @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@SpringBootApplication
public class KubernetesPerformanceBackApplication {
	// test comment
	public static MeterRegistry registry = new SimpleMeterRegistry();
	public static void main(String[] args) {
		SpringApplication.run(KubernetesPerformanceBackApplication.class, args);
		Metrics.addRegistry(registry);
	}
}
