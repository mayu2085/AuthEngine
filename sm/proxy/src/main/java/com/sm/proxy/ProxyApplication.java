package com.sm.proxy;

import com.sm.engine.ServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Main application
 */
@EnableZuulProxy
@SpringBootApplication
@ComponentScan
@Import(ServiceConfiguration.class)
@PropertySource(value = "classpath:/smproxy.properties", name = "SMProxy")
public class ProxyApplication {
    /**
     * Application entry
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }

}
