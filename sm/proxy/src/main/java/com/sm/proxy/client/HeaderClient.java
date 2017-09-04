package com.sm.proxy.client;

import com.sm.proxy.filters.support.SmProxyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client for requesting Headers REST service
 */
@Service
public class HeaderClient {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderClient.class);

    /**
     * Authorization header constant
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Header Service path
     */
    private static final String SERVICE_PATH= "/headers/evaluate/{username}";
    /**
     * Header API host
     */
    @Value("${sm.proxy.header_api_path}")
    private String host;

    /**
     * API token
     */
    @Value("${sm.proxy.api_token}")
    private String apiToken;

    /**
     * To fetch the header parameter from API
     * @param userName user name
     * @return List header list
     */
    public List<Map<String, String>> evaluate(String userName) {

        Map<String, String> vars = new HashMap<>();
        vars.put(SmProxyConstants.USERNAME, userName);
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, "Bearer " + apiToken);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        ResponseEntity response = template.exchange(host + SERVICE_PATH, HttpMethod.GET, httpEntity, List.class, vars);
        List<Map<String, String>> resp = (List<Map<String, String>>) response.getBody();
        resp.forEach(it -> {
            it.forEach((key, value) -> {
                LOGGER.debug(key + value);
            });
        });

        return resp;

    }
}
