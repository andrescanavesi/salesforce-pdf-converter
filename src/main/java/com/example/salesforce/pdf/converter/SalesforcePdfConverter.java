package com.example.salesforce.pdf.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author Andres Canavesi
 */
public class SalesforcePdfConverter {

    private static final Logger LOG = Logger.getLogger(SalesforcePdfConverter.class.getName());

    private ApiSession apiSession;
    private final SalesforceAuth salesforceAuth;

    /**
     *
     */
    public SalesforcePdfConverter() {
        salesforceAuth = new SalesforceAuth();
    }

    /**
     *
     * @param htmlContent
     * @return
     * @throws Exception
     */
    public File convert(String htmlContent) throws Exception {
        LOG.info("Converting html using Salesforce...");
        if (apiSession == null) {
            apiSession = salesforceAuth.login();
        }

        File pdf;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            JSONObject update = new JSONObject();
            update.put("html", htmlContent);
            HttpPost httpost = new HttpPost(apiSession.getInstanceUrl() + "/services/apexrest/htmltopdf");
            httpost.addHeader("Authorization", "Bearer " + apiSession.getAccessToken());
            httpost.addHeader("Content-type", "application/json");
            StringEntity messageEntity = new StringEntity(update.toString(), ContentType.create("application/json"));
            httpost.setEntity(messageEntity);
            // Execute the request.
            CloseableHttpResponse closeableresponse = httpclient.execute(httpost);
            // verify response is HTTP OK
            final int statusCode = closeableresponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    apiSession = null;
                    throw new Exception("Seems the access token is not valid. Do a relogin. " + closeableresponse.getStatusLine().getReasonPhrase());
                } else {
                    throw new Exception("Error generating the pdf report. Status code: " + statusCode + " Message: " + closeableresponse.getStatusLine().getReasonPhrase());
                }

            }
            LOG.log(Level.INFO, "Response Status line :{0}", closeableresponse.getStatusLine());
            String getResult = EntityUtils.toString(closeableresponse.getEntity());
            JSONObject jsonResponse = (JSONObject) new JSONTokener(getResult).nextValue();
            String base64Content = jsonResponse.getString("base64Content");
            byte[] decoded = Base64.getDecoder().decode(base64Content);
            pdf = new File(Configs.PDF_NAME);
            try (FileOutputStream file = new FileOutputStream(pdf)) {
                file.write(decoded);
            }
            LOG.log(Level.INFO, "PDF generated in file: {0}", pdf.getAbsoluteFile());
        }

        return pdf;

    }

    /**
     *
     * @return the tmp directory ending with "/"
     */
    public static String getTempDir() {
        String tempDir = System.getProperty("java.io.tmpdir");

        if (!tempDir.endsWith("/")) {
            tempDir += "/";
        }
        return tempDir;
    }
}
