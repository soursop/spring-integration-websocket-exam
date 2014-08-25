package org.springframework.exam.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.exam.domain.CatalinaContent;
import org.springframework.exam.domain.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {
	
	private static final Logger log = LoggerFactory.getLogger(GreetingController.class);
    
	@MessageMapping("/hello")
	@SendTo("/topic/catalinalog")
	public CatalinaContent greeting(HelloMessage message) throws Exception {
		
		Thread.sleep(3000);
		log.debug("Hello, " + message.getName() + "!");
		return new CatalinaContent("Hello, " + message.getName() + "!");
		
	}
	
}