package com.sm.proxy.filters.support;

/**
 * Enum defines the Front end system names.
 */
public enum FrontEndSystem {

    PARTNER_CENTER("PartnerCenter"),

    BUSINESS_WEB("BWEB");

    /**
     * Front end system name.
     */
    private String value;

    /**
     * Constructor for front end system enum.
     * @param value name of the front end system
     */
    FrontEndSystem(String value) {
        this.value = value;
    }

    /**
     * Returns the value.
     * @return String value of the front end system.
     */
    public String value() {
        return this.value;
    }

}
