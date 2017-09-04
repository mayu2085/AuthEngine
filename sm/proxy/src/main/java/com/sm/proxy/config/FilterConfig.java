package com.sm.proxy.config;

import com.sm.proxy.filters.error.ApiErrorResponseFilter;
import com.sm.proxy.filters.error.HtmlErrorResponseFilter;
import com.sm.proxy.filters.pre.AuthenticationFilter;
import com.sm.proxy.filters.pre.CustomHeaderFilter;
import com.sm.proxy.filters.pre.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Zuul filter bean configurations
 */
@Configuration
public class FilterConfig {
    /**
     *  Authentication filter bean initialization
     *
     * @return AuthenticationFilter authentication filter
     */
    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter();
    }

    /**
     * Api Error Response Filter bean initialization
     *
     * @return ApiErrorResponseFilter api error response filter
     */
    @Bean
    public ApiErrorResponseFilter apiErrorResponseFilter() {
        return new ApiErrorResponseFilter();
    }

    /**
     * Custom Header Filter bean initialization
     *
     * @return CustomHeaderFilter custom header filter
     */
    @Bean
    public CustomHeaderFilter customHeaderFilter() {
        return new CustomHeaderFilter();
    }

    /**
     * Html Error Response Filter bean initialization
     *
     * @return HtmlErrorResponseFilter html error response filter
     */
    @Bean
    public HtmlErrorResponseFilter htmlErrorResponseFilter() {
        return new HtmlErrorResponseFilter();
    }

    /**
     * Login Filter bean initialization
     *
     * @return LoginFilter login filter
     */
    @Bean
    public LoginFilter loginFilter() {
        return new LoginFilter();
    }

    /**
     * Property Sources Placeholder Configurer bean initialization
     *
     * @return PropertySourcesPlaceholderConfigurer  property source placeholder
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
