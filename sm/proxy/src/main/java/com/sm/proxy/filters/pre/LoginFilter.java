package com.sm.proxy.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.sm.engine.service.LDAPService;
import com.sm.proxy.filters.support.SmProxyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Perform Login using ldap and redirects as per login status.
 */
public class LoginFilter extends ZuulFilter {
    /**
     * The Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(LoginFilter.class);

    /**
     * Error query parameter
     */
    public static final String LOGIN_ERROR_QUERY = "error";

    /**
     * Login submit path
     */
    public static final String LOGIN_PATH = "/login";

    /**
     * LDAP service to perform login
     */
    @Autowired
    private LDAPService ldapService;

    /**
     * Login page url.
     */
    @Value("${sm.proxy.login.page}")
    private String loginUri;

    @Override
    public String filterType() {
        return SmProxyConstants.PRE_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {

        HttpServletRequest req = RequestContext.getCurrentContext().getRequest();
        return HttpMethod.POST.name().equals(req.getMethod()) && req.getRequestURL().toString().contains(LOGIN_PATH);
    }

    @Override
    public Object run() {
        LOG.debug("Executing Login filter");

        RequestContext ctx = RequestContext.getCurrentContext();
        if (login(ctx)) {
            // login successful redirect to the requested url.
            HttpSession session = ctx.getRequest().getSession();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl((String)session
                    .getAttribute(SmProxyConstants.REQUESTED_HOST))
                    .query((String) session.getAttribute(SmProxyConstants.REQUEST));

            LOG.debug("Redirecting to URL: " + builder.build().toUriString());
            redirect(builder.build().toUriString());
            session.setAttribute(SmProxyConstants.LOGIN_STATUS, true);
        }

        return true;
    }

    /**
     * Helper function to perform the login.
     *
     * @param ctx Request context
     * @return boolean login status
     */
    private boolean login(RequestContext ctx) {

        boolean  isLoginSuccess;
        //Extract the user name & password from the request
        String userName = RequestContext.getCurrentContext().getRequest().getParameter(SmProxyConstants.USERNAME);
        String password = ctx.getRequest().getParameter(SmProxyConstants.PASSWORD);
        LOG.debug(userName + "  " + password);
        if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            // Redirects to login page with error due to empty credentials.
            LOG.warn("Credentials are empty.");
            isLoginSuccess = false;
        } else {
            isLoginSuccess = ldapService.login(userName, password);
            //this will be used by custom header filter
            ctx.getRequest().getSession().setAttribute(SmProxyConstants.USERNAME, userName);
        }
        if (!isLoginSuccess) {
            // Redirects to login page with error.
            String loginErrorUrl = UriComponentsBuilder.fromPath(loginUri)
                    .query(LOGIN_ERROR_QUERY).build().toUriString();
            LOG.debug("Redirecting to " + loginErrorUrl);
            redirect(loginErrorUrl);
        }
        return isLoginSuccess;
    }

    /**
     * Helper function to perform redirection.
     *
     * @param url URL to redirect
     */
    private void redirect(String url) {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            //Skip executing downstream filters
            ctx.set(SmProxyConstants.SKIP_FILTER, "true");
            ctx.getResponse().sendRedirect(url);
            ctx.setSendZuulResponse(false);
        } catch (IOException e) {
            throw new ZuulRuntimeException(e);
        }
    }
}
