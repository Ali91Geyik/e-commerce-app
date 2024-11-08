package org.allisra.ecommerceapp.mapper;

import org.allisra.ecommerceapp.model.dto.RoleDTO;
import org.allisra.ecommerceapp.model.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    RoleDTO entityToDto(Role role);

    Role DtoToEntity(RoleDTO roleDTO);

    List<RoleDTO> entitiesToDtos(List<Role> roles);

    List<Role> DtosToEntities(List<RoleDTO> roleDTOs);

    @Mapping(target = "id", ignore = true)
    Role createDtoToEntity(RoleDTO roleDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(RoleDTO roleDTO, @MappingTarget Role role);

}
