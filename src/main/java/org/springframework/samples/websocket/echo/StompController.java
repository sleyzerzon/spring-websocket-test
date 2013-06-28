package org.springframework.samples.websocket.echo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.messaging.annotation.MessageExceptionHandler;
import org.springframework.web.messaging.annotation.SubscribeEvent;
import org.springframework.web.messaging.support.PubSubMessageBuilder;


@Controller
public class StompController {

	private final MessageChannel brokerChannel;

	private final MessageChannel clientChannel;


	@Autowired
	public StompController(@Qualifier("messageBrokerChannel") MessageChannel brokerChannel,
			@Qualifier("clientOutputChannel") MessageChannel clientChannel) {

		this.brokerChannel = brokerChannel;
		this.clientChannel = clientChannel;
	}


	@SubscribeEvent(value="/init")
	public Message<?> init() {

		Message<String> message = PubSubMessageBuilder.withPayload("Echo init").destination("/topic/echo").build();
		this.brokerChannel.send(message);

		return PubSubMessageBuilder.withPayload(new EchoEntity("Init data"))
				.contentType(MediaType.APPLICATION_JSON).build();
	}


	@MessageMapping(value="/echo")
	public void echoMessage(String text) {

		if (text.equals("exception")) {
			throw new IllegalStateException();
		}

		text = "Echo message: " + text;
		Message<String> message = PubSubMessageBuilder.withPayload(text).destination("/topic/echo").build();

		this.brokerChannel.send(message);
	}


	@MessageExceptionHandler
	public void handleMessageException(IllegalStateException ex) {

		Message<String> message = PubSubMessageBuilder.withPayload("Exception: " + ex.getMessage())
				.destination("/error").build();

		this.clientChannel.send(message);
	}


	@RequestMapping(value="/echo", method=RequestMethod.POST)
	@ResponseBody
	public void echoRequest(String text) {

		if (text.equals("exception")) {
			throw new IllegalStateException();
		}

		text = "Echo HTTP POST: " + text;
		Message<String> message = PubSubMessageBuilder.withPayload(text).destination("/topic/echo").build();

		this.brokerChannel.send(message);
	}


	@ExceptionHandler
	public void handleException(IllegalStateException ex) {

		Message<String> message = PubSubMessageBuilder.withPayload("Exception: " + ex.getMessage())
				.destination("/error").build();

		this.brokerChannel.send(message);
	}

}
