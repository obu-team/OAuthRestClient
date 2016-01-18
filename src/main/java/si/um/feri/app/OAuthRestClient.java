package si.um.feri.app;

import com.google.gson.Gson;
import com.sun.deploy.net.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.um.feri.app.model.CarCommand;
import si.um.feri.app.model.CommandState;
import si.um.feri.app.model.OBUIdResponse;
import si.um.feri.app.model.POSTCommand;

import java.io.IOException;

public class OAuthRestClient {

    private Logger logger = LoggerFactory.getLogger(OAuthRestClient.class.getName());

    private String baseUrl;
    private String clientId;
    private String secret;
    private String username;
    private String password;

    private OAuthClientRequest request = null;
    private OAuthJSONAccessTokenResponse tokenResponse = null;
    private OAuthClient oAuthClient = null;

    private Gson gson = null;

    public OAuthRestClient(String baseUrl, String clientId, String secret, String username, String password)
            throws OAuthProblemException, OAuthSystemException {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.secret = secret;
        this.username = username;
        this.password = password;
        this.gson = new Gson();
        obtainToken();
    }

    private void obtainToken() throws OAuthSystemException, OAuthProblemException {
        this.oAuthClient = new OAuthClient(new URLConnectionClient());

        this.request = OAuthClientRequest
                .tokenLocation(this.baseUrl + "oauth/token")
                .setGrantType(GrantType.PASSWORD)
                .setClientId(this.clientId)
                .setClientSecret(this.secret)
                .setUsername(this.username)
                .setPassword(this.password)
                .buildQueryMessage();

        this.tokenResponse = this.oAuthClient.accessToken(this.request);
    }

    public String createNewOBU() throws OAuthProblemException, OAuthSystemException {
        OAuthClientRequest bearerClientRequest = null;
        try {
            bearerClientRequest = new OAuthBearerClientRequest(this.baseUrl + "/restSecure/createNewOBU")
                    .setAccessToken(this.tokenResponse.getAccessToken()).buildQueryMessage();
        } catch (OAuthSystemException e) {
            logger.warn("OAuthException - obtain refreshed token");
            obtainToken();
            return createNewOBU();
        }

        OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
        if(resourceResponse.getResponseCode() == 401) {
            obtainToken();
            return createNewOBU();
        }
        OBUIdResponse obuId = gson.fromJson(resourceResponse.getBody(), OBUIdResponse.class);
        return obuId.getObuid();
    }

    public String getOBULocation(String obuId) throws OAuthProblemException, OAuthSystemException {
        OAuthClientRequest bearerClientRequest = null;
        try {
            bearerClientRequest = new OAuthBearerClientRequest(this.baseUrl + "/restSecure/" + obuId + "/trr/location")
                    .setAccessToken(this.tokenResponse.getAccessToken()).buildQueryMessage();
        } catch (OAuthSystemException e) {
            logger.warn("OAuthException - obtain refreshed token");
            obtainToken();
            return getOBULocation(obuId);
        }

        OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
        if(resourceResponse.getResponseCode() == 401) {
            obtainToken();
            return getOBULocation(obuId);
        }
        return resourceResponse.getBody();
    }

    public String getOBUDriveHistory(String obuId) throws OAuthProblemException, OAuthSystemException {
        OAuthClientRequest bearerClientRequest = null;
        try {
            bearerClientRequest = new OAuthBearerClientRequest(this.baseUrl + "/restSecure/" + obuId + "/trr/driveHistory")
                    .setAccessToken(this.tokenResponse.getAccessToken()).buildQueryMessage();
        } catch (OAuthSystemException e) {
            logger.warn("OAuthException - obtain refreshed token");
            obtainToken();
            return getOBUDriveHistory(obuId);
        }

        OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
        if(resourceResponse.getResponseCode() == 401) {
            obtainToken();
            return getOBUDriveHistory(obuId);
        }
        return resourceResponse.getBody();
    }

    public String getOBUErrors(String obuId) throws OAuthProblemException, OAuthSystemException {
        OAuthClientRequest bearerClientRequest = null;
        try {
            bearerClientRequest = new OAuthBearerClientRequest(this.baseUrl + "/restSecure/" + obuId + "/trr/error")
                    .setAccessToken(this.tokenResponse.getAccessToken()).buildQueryMessage();
        } catch (OAuthSystemException e) {
            logger.warn("OAuthException - obtain refreshed token");
            obtainToken();
            return getOBUErrors(obuId);
        }

        OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
        if(resourceResponse.getResponseCode() == 401) {
            obtainToken();
            return getOBUErrors(obuId);
        }
        return resourceResponse.getBody();
    }

    public String sendNotificationToOBU(String obuId, String message) throws OAuthProblemException, OAuthSystemException, IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(this.baseUrl + "/restSecure/" + obuId + "/trr/notification");
        httpPost.setHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("notification", message);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        CloseableHttpResponse response = client.execute(httpPost);
        client.close();

        if(response.getStatusLine().getStatusCode() == 401) {
            obtainToken();
            return sendNotificationToOBU(obuId, message);
        }

        return response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase();
    }

    public String sendCarCommand(String obuId, CarCommand cmd, CommandState cs) throws OAuthProblemException, OAuthSystemException {
        OAuthClientRequest bearerClientRequest = null;
        POSTCommand postCommand = new POSTCommand();
        postCommand.setCarCommand(cmd);
        postCommand.setCommandState(cs);
        try {
            bearerClientRequest = new OAuthBearerClientRequest(this.baseUrl + "/restSecure/" + obuId + "/trr/command")
                    .setAccessToken(this.tokenResponse.getAccessToken())
                    .buildQueryMessage();
        } catch (OAuthSystemException e) {
            obtainToken();
            e.printStackTrace();
            return sendCarCommand(obuId, cmd, cs);
        }
        bearerClientRequest.addHeader(OAuth.HeaderType.CONTENT_TYPE, "application/json");
        bearerClientRequest.setBody(gson.toJson(postCommand));

        OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.POST, OAuthResourceResponse.class);
        if(resourceResponse.getResponseCode() == 401) {
            obtainToken();
            return sendCarCommand(obuId, cmd, cs);
        }
        return resourceResponse.getBody();
    }

}
