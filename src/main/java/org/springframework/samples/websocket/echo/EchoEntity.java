package org.springframework.samples.websocket.echo;


public class EchoEntity {

	private final String message;


	public EchoEntity(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
