package com.sm.engine.service;

import com.sm.engine.exception.ApiException;
import com.sm.engine.model.UserAttributesRequest;

import java.util.List;
import java.util.Map;

/**
 * The ldap service interface.
 */
public interface LDAPService {
    /**
     * List ldap users.
     *
     * @return the ldap users.
     * @throws ApiException throws if any error happens.
     */
    List<String> listUsers() throws ApiException;

    /**
     * List ldap groups.
     *
     * @return the ldap groups.
     * @throws ApiException throws if any error happens.
     */
    List<String> listGroups() throws ApiException;

    /**
     * Evaluate rule.
     *
     * @param rule the rule.
     * @return true if rule is valid otherwise false.
     */
    boolean evaluateRule(String rule);

    /**
     * Get user attributes.
     *
     * @param request the user attributes request.
     * @return the user attributes.
     * @throws ApiException throws if any error happens.
     */
    Map<String, Object> getUserAttributes(UserAttributesRequest request) throws ApiException;

    /**
     * Authenticate the user with given user name and password using LDAP.
     * @param userId user name
     * @param password password
     * @return boolean login status
     */
    boolean login(String userId, String password);
}
