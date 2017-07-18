package technology.touchmars.wechat.client.config;

import java.util.Arrays;

import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import feign.RequestTemplate;

/**
 * Created by jhcao on 2017-03-28.
 */
public class WechatRequestInterceptor extends OAuth2FeignRequestInterceptor {

    private final OAuth2ClientContext oAuth2ClientContext;
    private final ClientCredentialResourceDetails details;

    private final AccessTokenProvider accessTokenProvider =
            new AccessTokenProviderChain(Arrays.asList(
                    new WechatClientCredentialAccessTokenProvider()
            )); //HttpComponentsClientHttpRequestFactory?

    public WechatRequestInterceptor(OAuth2ClientContext oAuth2ClientContext, ClientCredentialResourceDetails details) {
        super(oAuth2ClientContext, details, null, null);
        this.oAuth2ClientContext = oAuth2ClientContext;
        this.details = details;
//        if(accessTokenProvider instanceof AccessTokenProviderChain){
//            ((AccessTokenProviderChain)accessTokenProvider)
//                    .setRequestFactory(new HttpComponentsClientHttpRequestFactory());
//        }
    }

    @Override
    public void apply(RequestTemplate template) {
        template.query("access_token", extract());
    }

    protected String extract() {
        OAuth2AccessToken accessToken = getToken();
        return accessToken.getValue();
    }

    @Override
    protected OAuth2AccessToken acquireAccessToken() {
        return acquireAccessToken(details);
    }

    protected OAuth2AccessToken acquireAccessToken(OAuth2ProtectedResourceDetails resource)
            throws UserRedirectRequiredException {
        AccessTokenRequest tokenRequest = oAuth2ClientContext.getAccessTokenRequest();
        if (tokenRequest == null) {
            throw new AccessTokenRequiredException(
                    "Cannot find valid context on request for resource '"  + resource.getId() + "'.",
                    resource
            );
        }
        String stateKey = tokenRequest.getStateKey();
        if (stateKey != null) {
            tokenRequest.setPreservedState(oAuth2ClientContext.removePreservedState(stateKey));
        }
        OAuth2AccessToken existingToken = oAuth2ClientContext.getAccessToken();
        if (existingToken != null) {
            oAuth2ClientContext.setAccessToken(existingToken);
        }
        OAuth2AccessToken obtainableAccessToken;
        obtainableAccessToken = accessTokenProvider.obtainAccessToken(resource, tokenRequest);
        if (obtainableAccessToken == null || obtainableAccessToken.getValue() == null) {
            throw new IllegalStateException(
                    " Access token provider returned a null token, which is illegal according to the contract.");
        }
        oAuth2ClientContext.setAccessToken(obtainableAccessToken);
        return obtainableAccessToken;
    }

}
