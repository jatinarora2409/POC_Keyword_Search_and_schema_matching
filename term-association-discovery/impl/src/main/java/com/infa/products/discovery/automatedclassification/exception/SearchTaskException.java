package com.infa.products.discovery.automatedclassification.exception;

public class SearchTaskException extends RuntimeException {
    private static final long serialVersionUID = 950948509348509L;

    public SearchTaskException(String message) {
        super(message);
    }

    public SearchTaskException(Throwable cause) {
        super(cause);
    }

    public SearchTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
