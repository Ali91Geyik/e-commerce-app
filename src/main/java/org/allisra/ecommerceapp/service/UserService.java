package org.allisra.ecommerceapp.service;

import org.allisra.ecommerceapp.model.dto.PasswordUpdateDTO;
import org.allisra.ecommerceapp.model.dto.UserCreateDTO;
import org.allisra.ecommerceapp.model.dto.UserDTO;
import org.allisra.ecommerceapp.model.dto.UserUpdateDTO;
import org.allisra.ecommerceapp.model.entity.User;

import java.util.List;

public interface UserService {

    UserDTO createuser(UserCreateDTO createDTO);
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(UserUpdateDTO updateDTO);
    void deleteUser(Long id);
    void updatePassword(PasswordUpdateDTO passwordUpdateDTO);
    void resetPassword(String token, String newPassword);
    void verifyEmail(String token);
    void requestPasswordReset(String email);
    User findUserEntityByEmail(String email);
    boolean existByEmail(String email);
}
