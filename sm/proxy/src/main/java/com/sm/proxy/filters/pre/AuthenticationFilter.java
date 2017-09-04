package com.sm.proxy.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.sm.proxy.client.LandingPageDealerClient;
import com.sm.proxy.client.GetOrderDataResponse;
import com.sm.proxy.filters.support.FrontEndSystem;
import com.sm.proxy.filters.support.SmProxyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;

/**
 * It's responsible to validate the token. Throws an exception if the token is not found in the request.
 */
public class AuthenticationFilter extends ZuulFilter {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

    /**
     * Landing page dealer service client.
     */
    @Autowired
    private LandingPageDealerClient landingPageDealerClient;

    /**
     * Login page url.
     */
    @Value("${sm.proxy.login.page}")
    private String loginUri;

    /**
     * Front end system name from the configuration.
     */
    @Value("${sm.proxy.front_end_system}")
    private String frontEndSystem;

    /**
     * XML document factory
     */
    private static DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

    /**
     * XPath factory
     */
    private static XPathFactory xPathFactory = XPathFactory.newInstance();


    static {
        docFactory.setNamespaceAware(true);
    }

    @Override
    public String filterType() {
        return SmProxyConstants.PRE_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {

        return RequestContext.getCurrentContext().get(SmProxyConstants.SKIP_FILTER) == null;
    }

    /**
     * Helper function to extract the token from the request.
     *
     * @param request HttpServletRequest
     * @return String extracted token
     */
    private String getToken(HttpServletRequest request) {
        String token;
        if (HttpMethod.GET.name().equals(request.getMethod())) {
            token = getTokenFromQueryParam(request);
        } else {
            token = getTokenFromBody(request);
        }
        return token;
    }

    /**
     * Token extracted from the xml message body.
     *
     * @param request HttpServletRequest
     * @return String extracted token
     */
    private String getTokenFromBody(HttpServletRequest request) {
        LOG.debug("Extracting token from Body");
        DocumentBuilder docBuilder;
        String token;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(request.getInputStream());
            XPathExpression expr = xPathFactory.newXPath().compile("//token/text()");
            token = (String) expr.evaluate(doc, XPathConstants.STRING);

        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            LOG.error(e.getMessage(), e);
            throw new ZuulRuntimeException(e);
        }
        return token;
    }

    /**
     * Helper function to extract the token from the query parameter.
     *
     * @param request HttpServletRequest
     * @return String extracted token
     */
    private String getTokenFromQueryParam(HttpServletRequest request) {
        LOG.debug("Extracting token from header");
        return request.getParameter("token");
    }

    @Override
    public Object run() {
        LOG.debug("Executing authentication filter");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        LOG.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        String token = getToken(request);
        if (StringUtils.isEmpty(token)) {
            LOG.warn("Invalid token");
            // Throwing the runtime exception stops all further filter chain and invoke error filter chains.
            // Since the interface doesn't allow to throw checked exception, throwing runtime exception.
            throw new ZuulRuntimeException(new ZuulException("Invalid authentication token.",
                    HttpStatus.UNAUTHORIZED.value(), "Token is empty"));
        }

        //if the login is successful, skip login again, instead verify the current request with the previous.
        HttpSession currentSession = ctx.getRequest().getSession();
        if (currentSession.getAttribute(SmProxyConstants.LOGIN_STATUS) != null && (boolean)currentSession.getAttribute(SmProxyConstants.LOGIN_STATUS)) {
            LOG.info(ctx.getRequest().getQueryString());

            if (!ctx.getRequest().getQueryString().equals(currentSession.getAttribute(SmProxyConstants.REQUEST))) {
                currentSession.invalidate();
                throw new ZuulRuntimeException(new ZuulException("Authenticated request is tampered.",
                        HttpStatus.UNAUTHORIZED.value(), "Authenticated request is tampered"));
            }
            ctx.set(SmProxyConstants.USERNAME, currentSession.getAttribute(SmProxyConstants.USERNAME));
            currentSession.invalidate();
            return true;
        }

        GetOrderDataResponse resp = landingPageDealerClient.getOrderData(token, frontEndSystem);
        LOG.debug("Front end system: " + resp.getFrontEndSystem());
        if(FrontEndSystem.BUSINESS_WEB.value().equals(resp.getFrontEndSystem())) {
            LOG.debug("Redirecting the request to login");
            redirectToLogin(ctx);
        }
        if (FrontEndSystem.PARTNER_CENTER.value().equals(resp.getFrontEndSystem())) {
            ctx.set(SmProxyConstants.USERNAME, resp.getA1Login());
        }
        return null;
    }

    /**
     * Helper function to serialize the request and redirect the call to login.
     *
     * @param ctx RequestContext
     */
    private void redirectToLogin(RequestContext ctx) {

        HttpSession session = ctx.getRequest().getSession(true);
        LOG.info(ctx.getRequest().getQueryString());
        session.setAttribute(SmProxyConstants.REQUEST, ctx.getRequest().getQueryString());
        session.setAttribute(SmProxyConstants.REQUESTED_HOST, ctx.getRequest().getRequestURL().toString());
        try {
            LOG.info(session.getId());
            ctx.set(SmProxyConstants.SKIP_FILTER, "true");
            ctx.setSendZuulResponse(false);
            ctx.getResponse().sendRedirect(loginUri);

        } catch (IOException e) {
            throw new ZuulRuntimeException(e);
        }
    }
}
