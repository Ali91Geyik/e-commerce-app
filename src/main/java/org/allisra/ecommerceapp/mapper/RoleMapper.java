package org.allisra.ecommerceapp.mapper;

import org.allisra.ecommerceapp.model.dto.RoleDTO;
import org.allisra.ecommerceapp.model.entity.Role;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    RoleDTO entityToDto(Role role);

    @Mapping(target = "id", ignore = true)
    Role createDtoToEntity(RoleDTO roleDTO);

    List<RoleDTO> entitiesToDtos(List<Role> roles);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(RoleDTO roleDTO, @MappingTarget Role role);
}