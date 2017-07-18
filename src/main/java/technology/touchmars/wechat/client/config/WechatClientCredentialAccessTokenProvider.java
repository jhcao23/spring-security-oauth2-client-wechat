package technology.touchmars.wechat.client.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.GenericClientCredentialsAccessTokenProvider;

/**
 * Created by jhcao on 2017-03-27.
 */
public class WechatClientCredentialAccessTokenProvider extends GenericClientCredentialsAccessTokenProvider implements AccessTokenProvider {

    public WechatClientCredentialAccessTokenProvider() {
        super("client_credential", HttpMethod.GET);
        setAuthenticationHandler(new WechatClientAuthenticationHandler());
    }

}
