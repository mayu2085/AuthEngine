package com.sm.engine.exception;

import org.springframework.http.HttpStatus;

/**
 * The not found exception with code as 404 and message.
 */
public class NotFoundException extends ApiException {
    /**
     * The exception constructor with message.
     *
     * @param msg the message
     */
    public NotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND.value(), msg);
    }
}
