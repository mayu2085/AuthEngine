package com.sm.proxy.config;

import com.sm.proxy.client.LandingPageDealerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
/**
 * Bean configuration for Landing page dealer webservice.
 */
@Configuration
public class LandingPageDealerConfig {

    /**
     * Landing page webservice url.
     */
    @Value("${sm.proxy.landing_page_dealer_url}")
    private String landingPageUri;

    /**
     * The marshaller
     * @return Jaxb2Marshaller jaxb2 marshaller
     */
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("com.sm.proxy.client");
        return marshaller;
    }

    /**
     * Landing page dealer client bean.
     *
     * @param marshaller jaxb2 marshaller
     * @return LandingPageDealerClient webservice client
     */
    @Bean
    public LandingPageDealerClient landingPageDealerClient(Jaxb2Marshaller marshaller) {
        LandingPageDealerClient client = new LandingPageDealerClient();
        client.setDefaultUri(landingPageUri);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
