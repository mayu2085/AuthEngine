package com.sm.engine;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.query.SearchScope;
import org.springframework.validation.annotation.Validated;

/**
 * The application properties.
 */
@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "ldap")
public class LDAPProperties {

    /**
     * The ldap server url.
     */
    @NotBlank
    private String url;

    /**
     * The ldap server search scope.
     */
    private SearchScope searchScope;

    /**
     * The ldap server admin user dn.
     */
    @NotBlank
    private String adminUserDN;

    /**
     * The ldap server admin user password.
     */
    @NotBlank
    private String adminUserPassword;


    /**
     * The user search base.
     */
    @NotBlank
    private String userSearchBase;

    /**
     * The user attribute.
     */
    @NotBlank
    private String userAttribute;


    /**
     * The group search base.
     */
    @NotBlank
    private String groupSearchBase;


    /**
     * The group attribute.
     */
    @NotBlank
    private String groupAttribute;

    /**
     * The user lookup attributes.
     */
    private String[] userLookupAttributes;

    /**
     * The password attribute.
     */
    @NotBlank
    private String passwordAttribute;

    /**
     * The pooled flag.
     */
    private boolean pooled;
}