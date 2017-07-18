package technology.touchmars.wechat.client.config;

import org.springframework.security.oauth2.client.token.auth.ClientAuthenticationHandler;
import org.springframework.security.oauth2.client.token.auth.GenericClientAuthenticationHandler;

/**
 * Created by jhcao on 2017-03-27.
 */
public class WechatClientAuthenticationHandler extends GenericClientAuthenticationHandler implements ClientAuthenticationHandler {

    public WechatClientAuthenticationHandler() {
        super();
        setNameClientId("appid");
        setNameClientSecret("secret");
    }

}
