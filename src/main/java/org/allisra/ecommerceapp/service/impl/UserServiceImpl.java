package org.allisra.ecommerceapp.service.impl;


import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.UserMapper;
import org.allisra.ecommerceapp.model.dto.PasswordUpdateDTO;
import org.allisra.ecommerceapp.model.dto.user.UserCreateDTO;
import org.allisra.ecommerceapp.model.dto.user.UserDTO;
import org.allisra.ecommerceapp.model.dto.user.UserUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Role;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.model.entity.VerificationToken;
import org.allisra.ecommerceapp.repository.RoleRepository;
import org.allisra.ecommerceapp.repository.UserRepository;
import org.allisra.ecommerceapp.repository.VerificationTokenRepository;
import org.allisra.ecommerceapp.service.TokenService;
import org.allisra.ecommerceapp.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    @Lazy
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final VerificationTokenRepository tokenRepository;
    @Override
    public UserDTO createuser(UserCreateDTO createDTO) {
        //e-mail check
        if (userRepository.existsByEmail(createDTO.getEmail())){
            throw new BadRequestException("Email Already Exists"+ createDTO.getEmail());
        }
        //Entity oluşturma
        User user= userMapper.createDtoToEntity(createDTO);

        //Şifre şifreleme
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));

        //Rolleri ayarlama
        Set<Role> roles = createDTO.getRoleNames().stream()
                .map(roleName ->roleRepository.findByName(roleName)
                        .orElseThrow(()-> new ResourceNotFoundException("Role not found " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        //Kaydetme ve DTO'ya çevirme
        User savedUser= userRepository.save(user);

        return userMapper.entityToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user= userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return userMapper.entityToDto(user);

    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user= userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found with e-mail "+ email));
        return userMapper.entityToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(UserUpdateDTO updateDTO) {
        User existingUser = userRepository.findById(updateDTO.getId()).orElseThrow(()->
                new ResourceNotFoundException("User not found with this id: "+ updateDTO.getId()));

        //DTO'dan entity'e güncelleme
        userMapper.updateEntityFromDto(updateDTO, existingUser);

        //Rolleri Güncelleme
        if(updateDTO.getRoleNames()!=null && !updateDTO.getRoleNames().isEmpty()){
            Set<Role> roles = updateDTO.getRoleNames().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(()-> new ResourceNotFoundException("Role not found! "+roleName)))
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles);
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.entityToDto(updatedUser);

    }

    @Override
    public void updatePassword(PasswordUpdateDTO passwordUpdateDTO) {
        User user = userRepository.findById(passwordUpdateDTO.getUserId()).orElseThrow(()
        -> new ResourceNotFoundException("User not found with id: " + passwordUpdateDTO.getUserId()));

        //mevcut şifre kontrolü
        if(!passwordEncoder.matches(passwordUpdateDTO.getCurrentPassword(), user.getPassword())){
            throw new BadRequestException("New password and confirmation don't match!");
        }
        //Şifreyi Güncelleme
        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        userRepository.save(user);

    }

    @Override
    public void resetPassword(String token, String newPassword) {
        VerificationToken verificationToken = tokenService.validateToken(
                token, VerificationToken.TokenType.PASSWORD_RESET);
        User user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }

    @Override
    public void verifyEmail(String token) {
    VerificationToken verificationToken = tokenService.validateToken(
            token, VerificationToken.TokenType.EMAIL_VERIFICATION);
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }

    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException("User not found with this email"+email));
        tokenService.createPasswordResetTokenAndSendEmail(user);

    }
    @Override
    @Transactional(readOnly = true)
    public User findUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }


    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User not found with this id: "+id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
