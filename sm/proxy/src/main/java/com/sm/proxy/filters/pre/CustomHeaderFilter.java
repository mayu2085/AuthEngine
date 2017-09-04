package com.sm.proxy.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.sm.proxy.client.HeaderClient;
import com.sm.proxy.filters.support.SmProxyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * It's responsible to add custom header configured in the configuration file.
 */
public class CustomHeaderFilter extends ZuulFilter {
    /**
     * The Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CustomHeaderFilter.class);

    /**
     * Header service client.
     */
    @Autowired
    private HeaderClient headerClient;

    @Override
    public String filterType() {
        return SmProxyConstants.PRE_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return 7;
    }

    @Override
    public boolean shouldFilter() {

        return RequestContext.getCurrentContext().get(SmProxyConstants.SKIP_FILTER) == null;
    }

    @Override
    public Object run() {
        LOG.info("Executing Custom header filter");
        RequestContext ctx = RequestContext.getCurrentContext();
        String userName = (String)ctx.get(SmProxyConstants.USERNAME);
        userName = StringUtils.isEmpty(userName) ? "" : userName;
        List<Map<String, String>> headers = headerClient.evaluate(userName);
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach( header -> {
                ctx.addZuulRequestHeader(header.get(SmProxyConstants.HEADER_KEY), header.get(SmProxyConstants.HEADER_VALUE));
            });
        }

        return null;
    }

}
