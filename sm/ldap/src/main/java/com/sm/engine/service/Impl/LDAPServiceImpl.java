package com.sm.engine.service.Impl;

import com.sm.engine.LDAPProperties;
import com.sm.engine.exception.ApiException;
import com.sm.engine.exception.NotFoundException;
import com.sm.engine.model.UserAttributesRequest;
import com.sm.engine.service.LDAPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.CountNameClassPairCallbackHandler;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchControls;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * The ldap service implement the interface.
 */
@Service
public class LDAPServiceImpl implements LDAPService {
    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LDAPServiceImpl.class);

    /**
     * The ldap template.
     */
    private LdapTemplate template;

    /**
     * The ldap properties.
     */
    private LDAPProperties properties;

    /**
     * The ldap service constructor to inject ldap properties and build ldap template.
     *
     * @param properties the ldap properties
     */
    @Autowired
    public LDAPServiceImpl(LDAPProperties properties) {
        this.properties = properties;
        LdapContextSource cs = new LdapContextSource();
        cs.setCacheEnvironmentProperties(false);
        cs.setUrl(properties.getUrl());
        cs.setUserDn(properties.getAdminUserDN());
        cs.setPassword(properties.getAdminUserPassword());
        cs.setPooled(properties.isPooled());
        cs.afterPropertiesSet();
        template = new LdapTemplate(cs);
        template.setDefaultSearchScope(properties.getSearchScope().getId());
    }

    /**
     * List ldap users.
     *
     * @return the ldap users.
     * @throws ApiException throws if any error happens.
     */
    @Override
    public List<String> listUsers() throws ApiException {
        return searchSingleAttribute(properties.getUserSearchBase(), properties.getUserAttribute());
    }

    /**
     * List ldap groups.
     *
     * @return the ldap groups.
     * @throws ApiException throws if any error happens.
     */
    @Override
    public List<String> listGroups() throws ApiException {
        return searchSingleAttribute(properties.getGroupSearchBase(), properties.getGroupAttribute());
    }

    /**
     * Evaluate rule.
     *
     * @param rule the rule.
     * @return true if rule is valid otherwise false.
     */
    @Override
    public boolean evaluateRule(String rule) {
        try {
            SearchControls ctls = new SearchControls();
            // use count limit to speed up
            ctls.setCountLimit(1);
            ctls.setSearchScope(properties.getSearchScope().getId());
            CountNameClassPairCallbackHandler handler = new CountNameClassPairCallbackHandler();
            template.search(rule, "objectclass=*", ctls, handler);
            return handler.getNoOfRows() > 0;
        } catch (NameNotFoundException ex) {
            LOG.error("Error happened during evaluating rule", ex);
            return false;
        }
    }

    /**
     * Get user attributes.
     *
     * @param request the user attributes request.
     * @return the user attributes.
     * @throws ApiException throws if any error happens.
     */
    @Override
    public Map<String, Object> getUserAttributes(UserAttributesRequest request) throws ApiException {
        try {
            return template.lookup(request.getUserDN(), request.getAttributes(), new AbstractContextMapper<Map<String, Object>>() {
                @Override
                protected Map<String, Object> doMapFromContext(DirContextOperations ctx) {
                    Map<String, Object> result = new HashMap<>();
                    for (String key : request.getAttributes()) {
                        String[] values = ctx.getStringAttributes(key);
                        if (values != null && values.length > 1) {
                            // real multi values
                            result.put(key, values);
                        } else {
                            // single value
                            result.put(key, ctx.getStringAttribute(key));
                        }
                    }
                    return result;
                }
            });
        } catch (NameNotFoundException ex) {
            LOG.error("UserDN:" + request.getUserDN() + " not found!", ex);
            throw new NotFoundException("The match ldap user not found!");
        }
    }

    /**
     * Search single attribute with search base and attribute name.
     *
     * @param searchBase the search base.
     * @param attribute  the attribute name.
     * @return the single attribute list for ldap entries
     * @throws ApiException throws if any error happens.
     */
    private List<String> searchSingleAttribute(String searchBase, String attribute) throws ApiException {
        try {
            return template.search(
                    query().attributes(attribute)
                            .base(searchBase)
                            .where(attribute).isPresent(),
                    (AttributesMapper<String>) attrs -> (String) attrs.get(attribute).get());
        } catch (NameNotFoundException ex) {
            LOG.error("Invalid search base " + searchBase, ex);
            throw new NotFoundException("The match ldap search base " + searchBase + " not found!");
        }
    }

    /**
     * Authenticate the user with given user name and password using LDAP.
     * @param userId user name
     * @param password password
     * @return boolean login status
     */
    public boolean login(String userId, String password) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("uid", userId));
        return template.authenticate(properties.getUserSearchBase(), filter.toString(), password);
    }
}
