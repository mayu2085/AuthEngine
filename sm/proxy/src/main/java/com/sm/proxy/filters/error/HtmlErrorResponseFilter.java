package com.sm.proxy.filters.error;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.sm.proxy.filters.support.SmProxyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * This handles the error occurred in filters. This filter executes only for the HTTP request which accepts "text/html".
 * If it's 401 unauthorised access then the caller will be redirected to Login page.
 * For all other errors it will redirect to Error page.
 */
public class HtmlErrorResponseFilter extends ZuulFilter {
    /**
     * The Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(HtmlErrorResponseFilter.class);

    @Value("${sm.proxy.error.page}")
    private String errorPage;
    @Value("${sm.proxy.login.page}")
    private String loginPage;

    /**
     * Sets errorPage property
     *
     * @param errorPage error page property value
     */
    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    /**
     * Sets loginPage property
     *
     * @param loginPage login page property value
     */
    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    @Override
    public String filterType() {
        return SmProxyConstants.ERROR_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getThrowable() != null
                && (ctx.getRequest().getHeader(HttpHeaders.ACCEPT).equals(MediaType.TEXT_HTML_VALUE));
    }

    @Override
    public Object run() {
        LOG.info("Executing HTML error response filter");
        RequestContext context = RequestContext.getCurrentContext();
        Throwable t = context.getThrowable();
        ZuulException e = ErrorHelper.findZuulException(t);
        // Redirecting to the login/error location
        context.setResponseStatusCode(301);
        if (e.nStatusCode == HttpStatus.UNAUTHORIZED.value()) {
            context.getResponse().addHeader("Location", this.loginPage);
        } else {
            // Redirecting to error.html if any other exception occurs
            context.getResponse().addHeader("Location", this.errorPage);
        }
        // Removing throwable so that SendResponseFilter gets executed
        context.remove("throwable");
        return true;
    }
}
