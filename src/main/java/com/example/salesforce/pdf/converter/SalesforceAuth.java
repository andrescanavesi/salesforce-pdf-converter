package com.example.salesforce.pdf.converter;

import java.util.logging.Logger;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author Andres Canavesi
 */
public class SalesforceAuth {

    private static final Logger LOG = Logger.getLogger(SalesforceAuth.class.getName());

    private String username;
    private String password;
    private final String clientId;
    private final String clientSecret;

    /**
     *
     */
    public SalesforceAuth() {
        this.username = Configs.SALESFORCE_PDF_CONVERTER_USERNAME;
        this.password = Configs.SALESFORCE_PDF_CONVERTER_PASSWORD;
        this.clientId = Configs.SALESFORCE_PDF_CONVERTER_CLIENT_ID;
        this.clientSecret = Configs.SALESFORCE_PDF_CONVERTER_CLIENT_SECRET;
    }

    /**
     *
     * @return @throws Exception
     */
    public ApiSession login() throws Exception {
        return login(false);
    }

    /**
     *
     * @param isSandbox
     * @return
     * @throws Exception
     */
    public ApiSession login(boolean isSandbox) throws Exception {
        LOG.info("Doing login to convert html to pdf...");
        String subDomain = "login";
        if (isSandbox) {
            subDomain = "test";
        }
        StringBuilder url = new StringBuilder();
        url.append("https://").append(subDomain).append(".salesforce.com/services/oauth2/token?grant_type=password")
                .append("&client_id=")
                .append(clientId)
                .append("&client_secret=")
                .append(clientSecret)
                .append("&username=")
                .append(username)
                .append("&password=")
                .append(password);

        ApiSession apiSession;
        // Login requests must be POSTs
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            // Login requests must be POSTs
            HttpPost httpPost = new HttpPost(url.toString());
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // verify response is HTTP OK
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new Exception("Error doing login to convert html to pdf. Status code: " + statusCode + " Message: " + response.getStatusLine().getReasonPhrase());
            }
            String responseString = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = (JSONObject) new JSONTokener(responseString).nextValue();
            apiSession = new ApiSession();
            apiSession.setAccessToken(jsonObject.getString("access_token"));
            apiSession.setInstanceUrl(jsonObject.getString("instance_url"));
            if (apiSession.getAccessToken() == null) {
                throw new IllegalStateException("Access token null");
            }
            if (apiSession.getInstanceUrl() == null) {
                throw new IllegalStateException("Instance URL null");
            }
            // release connection
            httpPost.releaseConnection();
            LOG.info("Login to convert pdf ok");
        }
        return apiSession;

    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
