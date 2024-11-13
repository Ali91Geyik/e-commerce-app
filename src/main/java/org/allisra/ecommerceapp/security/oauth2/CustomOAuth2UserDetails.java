package org.allisra.ecommerceapp.security.oauth2;

import lombok.Getter;
import org.allisra.ecommerceapp.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomOAuth2UserDetails implements OAuth2User {

    @Getter
    private final User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2UserDetails(User user, Map<String, Object> attributes) {
    this.user=user;
    this.attributes=attributes;
    }

    @Override
    public Map<String,Object> getAttributes(){
        return attributes;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }
    @Override
    public String getName(){
        return user.getEmail();
    }
    // Ek yardımcı metodlar
    public String getEmail() {
        return user.getEmail();
    }

    public Long getUserId() {
        return user.getId();
    }

}
