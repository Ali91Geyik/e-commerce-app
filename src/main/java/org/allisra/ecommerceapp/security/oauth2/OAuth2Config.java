package org.allisra.ecommerceapp.security.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
public class OAuth2Config {

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver (
            ClientRegistrationRepository clientRegistrationRepository){
    return new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorize");
    }

    public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler(){
        SimpleUrlAuthenticationSuccessHandler successHandler= new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setDefaultTargetUrl("/oauth2/success");
        return successHandler;
    }

}
