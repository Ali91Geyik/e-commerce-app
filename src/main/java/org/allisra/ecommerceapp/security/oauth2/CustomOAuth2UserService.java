package org.allisra.ecommerceapp.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.dto.user.UserCreateDTO;
import org.allisra.ecommerceapp.service.UserService;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOauth2User(userRequest, oAuth2User);
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User processOauth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        Oauth2UserInfo userInfo = Oauth2UserInfo.create(
                userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes());
        if (!StringUtils.hasText(userInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from Oauth2 provider");
        }
        if (!userService.existByEmail(userInfo.getEmail())) {
            registerNewUser(userInfo);
        }
        return oAuth2User;
    }

    private void registerNewUser(Oauth2UserInfo userInfo) {
        //Yeni Kullanıcı oluştur
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail(userInfo.getEmail());
        //İsmi Parçala
        String[] nameParts = userInfo.getName().split(" ");
        createDTO.setFirstName(nameParts[0]);
        createDTO.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        //Rastgele şifre oluştur
        createDTO.setPassword(generateRandomPassword());
        //Varsayılan rol ata
        createDTO.setRoleNames(Set.of("ROLE_USER"));

        userService.createuser(createDTO);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }

}
