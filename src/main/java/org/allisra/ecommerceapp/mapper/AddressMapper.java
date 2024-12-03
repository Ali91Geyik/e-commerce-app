package org.allisra.ecommerceapp.mapper;

import org.allisra.ecommerceapp.model.dto.address.AddressCreateDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Address;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Address createDtoToEntity(AddressCreateDTO dto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(AddressUpdateDTO dto, @MappingTarget Address address);

    AddressDTO entityToDto(Address address);

    List<AddressDTO> entitiesToDtos(List<Address> addresses);
}