package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.dto.RoleDTO;
import org.allisra.ecommerceapp.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO){
        RoleDTO createdRole = roleService.createRole(roleDTO);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleByID(@PathVariable Long id){
        RoleDTO role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String name){
        RoleDTO roleDTO = roleService.getRoleByName(name);
        return ResponseEntity.ok(roleDTO);
    }
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles(){
        List<RoleDTO> roleDTOS = roleService.getAllRoles();
        return ResponseEntity.ok(roleDTOS);
    }
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole (
            @PathVariable Long id,
            @Valid @RequestBody RoleDTO roleDTO){
        if (!id.equals(roleDTO.getId())){
            return ResponseEntity.badRequest().build();
        }
        RoleDTO updatedRole = roleService.updateRole(roleDTO);
        return ResponseEntity.ok(updatedRole);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id){
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

}
