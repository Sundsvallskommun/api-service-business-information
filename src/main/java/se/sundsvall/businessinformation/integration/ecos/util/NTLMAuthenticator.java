package se.sundsvall.businessinformation.integration.ecos.util;

import java.util.List;

import org.apache.hc.client5.http.impl.auth.NTLMEngine;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

  public class NTLMAuthenticator implements Authenticator {
    final NTLMEngine engine = new NTLMEngineImpl();
    private final String username;
    private final String password;
    private final String ntlmMsg1;
    
    public NTLMAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
        String localNtlmMsg1 = null;
        try {
            localNtlmMsg1 = engine.generateType1Msg(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ntlmMsg1 = localNtlmMsg1;
    }
    
    @Override
    public Request authenticate(Route route, Response response) {
        final List<String> wwwAuthenticate = response.headers().values("WWW-Authenticate");
        if (wwwAuthenticate.contains("NTLM")) {
            return response.request().newBuilder().header("Authorization", "NTLM " + ntlmMsg1).build();
        }
        String ntlmMsg3 = null;
        try {
            ntlmMsg3 = engine.generateType3Msg(username, password.toCharArray(), null, null, wwwAuthenticate.get(0).substring(5));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.request().newBuilder().header("Authorization", "NTLM " + ntlmMsg3).build();
    }
}