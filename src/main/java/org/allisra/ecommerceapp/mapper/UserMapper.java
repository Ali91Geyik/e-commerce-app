package org.allisra.ecommerceapp.mapper;

import org.allisra.ecommerceapp.model.dto.UserCreateDTO;
import org.allisra.ecommerceapp.model.dto.UserDTO;
import org.allisra.ecommerceapp.model.dto.UserUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Role;
import org.allisra.ecommerceapp.model.entity.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roleNames", source = "roles", qualifiedByName = "rolesToRoleNames")
    UserDTO entityToDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User createDtoToEntity(UserCreateDTO createDTO);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(UserUpdateDTO updateDTO, @MappingTarget User user);

    @Named("rolesToRoleNames")
    default Set<String> rolesToRoleNames(Set<Role> roles){
        if(roles==null) return null;
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @IterableMapping(elementTargetType = UserDTO.class)
    Set<UserDTO> entitiesToDtos(Set<User> users);

    @AfterMapping
    default void afterCreateDtoToEntity(@MappingTarget User user){
        //Gerekirse ek son-işlemler eklenebilir
        user.setActive(true);
    }
}
