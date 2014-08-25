package org.springframework.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.exam.domain.CatalinaContent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class FileContentRecordingService {
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	public void sendLinesToTopic(String line) {
		this.simpMessagingTemplate.convertAndSend("/topic/catalinalog", new CatalinaContent(line));
	}
	
}
