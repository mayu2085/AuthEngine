package com.sm.engine;

import com.sm.engine.service.Impl.LDAPServiceImpl;
import com.sm.engine.service.LDAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LDAP Service configuration
 */
@Configuration
@EnableConfigurationProperties(LDAPProperties.class)
public class ServiceConfiguration {
    /**
     * The LDAP properties to initialize the bean
     */
    @Autowired LDAPProperties ldapProperties;

    /**
     * LDAP Service bean initialization
     * @return LDAPService ldap service
     */
    @Bean
    public LDAPService ldapService() {
        return new LDAPServiceImpl(ldapProperties);
    }
}
