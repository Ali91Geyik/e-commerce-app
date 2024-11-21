package org.allisra.ecommerceapp.service;



import org.allisra.ecommerceapp.model.dto.category.CategoryCreateDTO;
import org.allisra.ecommerceapp.model.dto.category.CategoryDTO;
import org.allisra.ecommerceapp.model.dto.category.CategoryTreeDTO;
import org.allisra.ecommerceapp.model.dto.category.CategoryUpdateDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryCreateDTO createDTO);
    CategoryDTO getCategoryById(Long id);
    CategoryDTO getCategoryBySlug(String slug);
    List<CategoryDTO> getAllCategories();
    List<CategoryDTO> getActiveCategories();
    List<CategoryDTO> getSubCategories(Long parentId);
    List<CategoryTreeDTO> getCategoryTree();
    CategoryDTO updateCategory(CategoryUpdateDTO updateDTO);
    void deleteCategory(Long id);
    void reorderCategories(Long parentId, List<Long> categoryIds);
}