package com.sm.proxy.filters.error;

import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpStatus;

/**
 * This is helper class.
 */
class ErrorHelper {

    /**
     * Find the ZuulException from deep inside the stacktrace of given Throwable
     *
     * @param t Throwable
     * @return ZuulException
     */
    static ZuulException findZuulException(Throwable t) {
        ZuulException exception;
        if (t.getCause().getCause() instanceof ZuulException) {
            // thrown by the local filter
            exception = (ZuulException) t.getCause().getCause();
        } else if (t.getCause() instanceof ZuulException) {
            // wrapped exception
            exception = (ZuulException) t.getCause();
        } else if (t instanceof ZuulException) {
            // lifecycle exceptions
            exception = (ZuulException) t;
        } else {
            exception = new ZuulException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value(), t.getMessage());
        }
        return exception;
    }
}
