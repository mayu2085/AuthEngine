package com.sm.engine.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The ldap user information.
 */
@Getter
@Setter
public class UserInfo {
    /**
     * The user dn.
     */
    private String userDN;

    /**
     * The user groups.
     */
    private List<String> groups;
}
