package org.allisra.ecommerceapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.exception.ResourceAlreadyExistsException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.UserMapper;
import org.allisra.ecommerceapp.model.dto.PasswordUpdateDTO;
import org.allisra.ecommerceapp.model.dto.user.UserCreateDTO;
import org.allisra.ecommerceapp.model.dto.user.UserDTO;
import org.allisra.ecommerceapp.model.dto.user.UserUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Role;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.repository.RoleRepository;
import org.allisra.ecommerceapp.repository.UserRepository;
import org.allisra.ecommerceapp.service.EmailService;
import org.allisra.ecommerceapp.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public UserDTO createuser(UserCreateDTO createDTO) {
        if (existByEmail(createDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = userMapper.createDtoToEntity(createDTO);
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setEmailVerified(false);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(user);
        return userMapper.entityToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.entityToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = findUserEntityByEmail(email);
        return userMapper.entityToDto(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return null;
    }

    @Override
    public UserDTO updateUser(UserUpdateDTO updateDTO) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public void updatePassword(PasswordUpdateDTO passwordUpdateDTO) {

    }

    @Override
    @Transactional(readOnly = true)
    public User findUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void verifyEmail(String email) {
        User user = findUserEntityByEmail(email);
        user.setEmailVerified(true);
        userRepository.save(user);
        emailService.sendWelcomeEmail(user);
    }

    @Override
    public void requestPasswordReset(String email) {

    }

    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = findUserEntityByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}