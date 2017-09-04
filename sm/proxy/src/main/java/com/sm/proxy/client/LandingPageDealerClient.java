package com.sm.proxy.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

/**
 * Webservice client to call the Landing page dealer webservice.
 */
@Configuration
public class LandingPageDealerClient extends WebServiceGatewaySupport {

    /**
     * Landing page url.
     */
    @Value("${sm.proxy.landing_page_dealer_url}")
    private String landingPageUri;

    /**
     * Client method to invoke get order data operation.
     *
     * @param token the token
     * @param frontEndSystem name of the front end system
     * @return GetOrderDataResponse getOrderData response
     */
    public GetOrderDataResponse getOrderData(String token, String frontEndSystem) {
        GetOrderDataRequest request = new GetOrderDataRequest();
        request.setToken(token);
        request.setFrontEndSystem(frontEndSystem);
        GetOrderDataResponse response = (GetOrderDataResponse) getWebServiceTemplate()
                .marshalSendAndReceive(landingPageUri, request, new SoapActionCallback(""));
        return response;
    }
}
