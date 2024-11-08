package org.allisra.ecommerceapp.service;

import org.allisra.ecommerceapp.model.dto.RoleDTO;
import org.allisra.ecommerceapp.model.entity.Role;

import java.util.List;

public interface RoleService {
    RoleDTO createRole(RoleDTO roleDTO);
    RoleDTO getRoleById(Long id);
    RoleDTO getRoleByName(String name);
    List<RoleDTO> getAllRoles();
    RoleDTO updateRole(RoleDTO roleDTO);
    void deleteRole(Long id);

}
