package org.allisra.ecommerceapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.RoleMapper;
import org.allisra.ecommerceapp.model.dto.RoleDTO;
import org.allisra.ecommerceapp.model.entity.Role;
import org.allisra.ecommerceapp.repository.RoleRepository;
import org.allisra.ecommerceapp.service.RoleService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
       try{
           Role role = roleMapper.createDtoToEntity(roleDTO);
           Role savedRole =roleRepository.save(role);
           return roleMapper.entityToDto(savedRole);
       }
       catch (DataIntegrityViolationException e){
        throw new BadRequestException("Role name must be unique" + roleDTO.getName());
       }
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Role not found with id: "+id));
        return roleMapper.entityToDto(role);

    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleByName(String name) {
        Role role = roleRepository.findByName(name).orElseThrow(()-> new ResourceNotFoundException("Role not found with name: "+ name));
        return roleMapper.entityToDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        List<Role> roles =roleRepository.findAll();
        return roleMapper.entitiesToDtos(roles);
    }

    @Override
    public RoleDTO updateRole(RoleDTO roleDTO) {
        // kontrol
        Role existingRole = roleRepository.findById(roleDTO.getId()).orElseThrow(
                ()-> new ResourceNotFoundException("Role not found with id:" +roleDTO.getId())
        );
        // Mevcut role'ü güncelle
        try{
            roleMapper.updateEntityFromDto(roleDTO,existingRole);
            Role updatedRole = roleRepository.save(existingRole);
            return roleMapper.entityToDto(updatedRole);
        }
        catch (DataIntegrityViolationException e){
        throw new BadRequestException("Role name must be unique: " +roleDTO.getName());
        }
    }

    @Override
    public void deleteRole(Long id) {

        try{
        if(!roleRepository.existsById(id)){
            throw new ResourceNotFoundException("Role not found with id: "+id);
        }
        roleRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e){
        throw  new BadRequestException("Role can't be deleted because it is being used");
        }

    }
}
