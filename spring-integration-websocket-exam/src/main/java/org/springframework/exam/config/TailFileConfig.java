package org.springframework.exam.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.exam.service.FileContentRecordingService;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.file.tail.ApacheCommonsFileTailingMessageProducer;

@Configuration
@EnableIntegration
public class TailFileConfig {

	@Autowired
	private FileContentRecordingService fileContentRecordingService;

	public MessageProducerSupport fileContentProducer() {
		ApacheCommonsFileTailingMessageProducer tailFileProducer = new ApacheCommonsFileTailingMessageProducer();
		File file = new File(System.getProperty("catalina.base") + "/logs/catalina.out");
		tailFileProducer.setPollingDelay(2000);
		tailFileProducer.setFile(file);
		return tailFileProducer;
	}

	@Bean
	public IntegrationFlow tailFilesFlow() {
		IntegrationFlow integrationFlow = IntegrationFlows.from(this.fileContentProducer())
		.handle("fileContentRecordingService", "sendLinesToTopic")
		.get();
		return integrationFlow;
	}
	
}
