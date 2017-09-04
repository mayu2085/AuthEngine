package com.sm.proxy.filters.error;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.sm.proxy.filters.support.SmProxyConstants;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * This handles the error occurred in filter.
 * This filter will evaluated only the requester accepts "application/xml" or "application/json"
 */
public class ApiErrorResponseFilter extends ZuulFilter {

    /**
     * The Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ApiErrorResponseFilter.class);
    /**
     * Error template for the request accepts application/xml.
     */
    private final String xmlError = "<soap:Envelope>\n" +
            "<soap:Body>\n" +
            "  <soap:Fault>\n" +
            "    <FaultCode>${errorCode}</FaultCode>\n" +
            "    <FaultString>${errorMessage}</FaultString>\n" +
            "  </soap:Fault>\n" +
            "  </soap:Body>\n" +
            "  </soap:Envelope>";
    /**
     * Error template for the request accepts application/json.
     */
    private final String jsonError = "{\n" +
            "\t\"error\": {\n" +
            "\t\t\"code\": ${errorCode},\n" +
            "\t\t\"message\": \"${errorMessage}\"\n" +
            "\t}\n" +
            "}";

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
                && !(ctx.getRequest().getHeader(HttpHeaders.ACCEPT).equals(MediaType.TEXT_HTML_VALUE));
    }

    @Override
    public Object run() {
        LOG.info("Executing error response filter");
        RequestContext context = RequestContext.getCurrentContext();
        Throwable t = context.getThrowable();
        ZuulException e = ErrorHelper.findZuulException(t);
        LOG.error(e.getMessage(), e);
        // Stops the call goes to the backend services
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(e.nStatusCode);
        context.getResponse().setHeader(HttpHeaders.CONTENT_TYPE, context.getRequest().getHeader(HttpHeaders.ACCEPT));
        context.getResponse().setCharacterEncoding("UTF-8");
        context.setResponseBody(getErrorMessage(e));
        context.remove("throwable");
        return true;
    }

    /**
     * Helper method to select the error response template based on the request context.
     * @return String error template
     */
    private String getErrorTemplate() {
        String accepts = RequestContext.getCurrentContext().getRequest().getHeader("Accept");
        if (MediaType.APPLICATION_XML_VALUE.equals(accepts)) {
            return xmlError;
        } else if (MediaType.APPLICATION_JSON_VALUE.equals(accepts)) {
            return jsonError;
        } else {
            return xmlError;
        }
    }

    /**
     * Helper method to get the error message based on the ZuulException.
     *
     * @param e  ZuulException
     * @return String error message
     */
    private String getErrorMessage(ZuulException e) {
        LOG.debug("Generating error  message");
        String template = getErrorTemplate();
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("errorCode", Integer.toString(e.nStatusCode));
        msgMap.put("errorMessage", e.getMessage());
        StrSubstitutor sub = new StrSubstitutor(msgMap);
        return sub.replace(template);
    }
}
