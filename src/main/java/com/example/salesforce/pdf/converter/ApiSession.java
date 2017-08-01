package com.example.salesforce.pdf.converter;

/**
 *
 * @author Andres Canavesi
 */
public class ApiSession {

    private String accessToken;
    private String instanceUrl;

    /**
     *
     * @return
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     *
     * @param accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     *
     * @return
     */
    public String getInstanceUrl() {
        return instanceUrl;
    }

    /**
     *
     * @param instanceUrl
     */
    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }

}
