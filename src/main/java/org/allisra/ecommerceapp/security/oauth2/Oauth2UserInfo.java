package org.allisra.ecommerceapp.security.oauth2;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Oauth2UserInfo {

    protected Map<String , Object> attributes;
    protected String provider;
    protected String id;
    protected String email;
    protected String name;
    protected String imageUrl;

    public static Oauth2UserInfo create(String registrationId, Map<String, Object> attributes){
        if (registrationId.equalsIgnoreCase("google")){
            return Oauth2UserInfo.builder()
                    .provider("google")
                    .attributes(attributes)
                    .id((String)attributes.get("sub"))
                    .email((String) attributes.get("email"))
                    .name((String) attributes.get("name"))
                    .imageUrl((String) attributes.get("picture"))
                    .build();
        }
        throw new IllegalArgumentException("Unsupported Oauth2 Provider : "+registrationId);
    }

}
