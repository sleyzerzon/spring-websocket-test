package org.springframework.samples.websocket.echo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.messaging.annotation.MessageExceptionHandler;
import org.springframework.web.messaging.annotation.SubscribeEvent;
import org.springframework.web.messaging.support.WebMessagingTemplate;

import reactor.util.Assert;


@Controller
public class StompController {

	private final WebMessagingTemplate messagingTemplate;


	@Autowired
	public StompController(WebMessagingTemplate messagingTemplate) {
		Assert.notNull(messagingTemplate, "messagingTemplate is required");
		this.messagingTemplate = messagingTemplate;
	}


	@SubscribeEvent(value="/init")
	public String init() {
		this.messagingTemplate.convertAndSend("/topic/echo", "Echo init");
		return "Init data";
	}

	@MessageMapping(value="/echo")
	public void echoMessage(String text) {
		if (text.equals("exception")) {
			throw new IllegalStateException();
		}
		this.messagingTemplate.convertAndSend("/topic/echo", "Echo message: " + text);
	}

	@MessageExceptionHandler
	public void handleMessageException(IllegalStateException ex) {
		// TODO: this doesn't work yet
		this.messagingTemplate.convertAndSend("/user/u1/echo", "Exception: " + ex.getMessage());
	}

	@RequestMapping(value="/echo", method=RequestMethod.POST)
	@ResponseBody
	public void echoRequest(String text) {
		if (text.equals("exception")) {
			throw new IllegalStateException();
		}
		this.messagingTemplate.convertAndSend("/topic/echo", "Echo HTTP POST: " + text);
	}

	@ExceptionHandler
	public void handleException(IllegalStateException ex) {
		// TODO: this doesn't work yet
		this.messagingTemplate.convertAndSend("/user/u1/echo", "Exception: " + ex.getMessage());
	}

}
