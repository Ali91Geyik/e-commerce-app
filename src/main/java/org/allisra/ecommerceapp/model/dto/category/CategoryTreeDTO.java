package org.allisra.ecommerceapp.model.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeDTO {
    private Long id;
    private String name;
    private String slug;
    private List<CategoryTreeDTO> children;
}
