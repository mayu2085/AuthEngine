package com.sm.engine.exception;



/**
 * The api exception with code and message.
 */

public class ApiException extends Exception {
    /**
     * The code for error.
     */
    private int code;

    /**
     * The exception constructor with code and message.
     *
     * @param code the code
     * @param msg  the message
     */
    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
