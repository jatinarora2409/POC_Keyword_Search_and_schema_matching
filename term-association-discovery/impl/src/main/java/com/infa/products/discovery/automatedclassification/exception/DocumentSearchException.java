package com.infa.products.discovery.automatedclassification.exception;

public class DocumentSearchException extends Exception {

	private static final long serialVersionUID = 1L;

	public DocumentSearchException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocumentSearchException(String message) {
		super(message);
	}
}
