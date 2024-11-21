package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.dto.PasswordUpdateDTO;
import org.allisra.ecommerceapp.model.dto.user.UserCreateDTO;
import org.allisra.ecommerceapp.model.dto.user.UserDTO;
import org.allisra.ecommerceapp.model.dto.user.UserUpdateDTO;
import org.allisra.ecommerceapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        UserDTO createdUser = userService.createuser(createDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email){
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser (
            @PathVariable Long id, @Valid @RequestBody UserUpdateDTO updateDTO)
    {
        if(!id.equals(updateDTO.getId())){
            return ResponseEntity.badRequest().build();
        }
        UserDTO updatedUser= userService.updateUser(updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id, @Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        if (!id.equals(passwordUpdateDTO.getUserId())){
            return ResponseEntity.badRequest().build();
        }
        userService.updatePassword(passwordUpdateDTO);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
    }


}
