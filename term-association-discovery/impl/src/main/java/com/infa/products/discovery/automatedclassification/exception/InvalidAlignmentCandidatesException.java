package com.infa.products.discovery.automatedclassification.exception;

public class InvalidAlignmentCandidatesException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String longForm;
	
	private String shortForm;
	
	public InvalidAlignmentCandidatesException(String message, String longForm, String shortForm) {
		super(message);
		this.longForm = longForm;
		this.shortForm = shortForm;
	}

	public String getLongForm() {
		return longForm;
	}

	public String getShortForm() {
		return shortForm;
	}
	
	
}
