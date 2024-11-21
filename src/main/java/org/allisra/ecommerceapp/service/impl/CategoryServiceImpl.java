package org.allisra.ecommerceapp.service.impl;

import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.CategoryMapper;
import org.allisra.ecommerceapp.model.dto.category.CategoryCreateDTO;
import org.allisra.ecommerceapp.model.dto.category.CategoryTreeDTO;
import org.allisra.ecommerceapp.model.dto.category.CategoryUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Category;
import org.allisra.ecommerceapp.repository.CategoryRepository;
import org.allisra.ecommerceapp.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.allisra.ecommerceapp.model.dto.category.CategoryDTO;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_CACHE = "categories";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final Slugify slugify;

    @Override
    @Transactional
    @CacheEvict(value = CATEGORY_CACHE, allEntries = true)
    public CategoryDTO createCategory(CategoryCreateDTO createDTO) {
        Category category = categoryMapper.createDtoToEntity(createDTO);

        if (createDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(createDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + createDTO.getParentId()));
            category.setParent(parent);
        }

        String baseSlug = slugify.slugify(createDTO.getName());
        String finalSlug = generateUniqueSlug(baseSlug);
        category.setSlug(finalSlug);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.entityToDto(savedCategory);
    }

    @Override
    @Cacheable(value = CATEGORY_CACHE, key = "#id")
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.entityToDto(category);
    }

    @Override
    @Cacheable(value = CATEGORY_CACHE, key = "#slug")
    public CategoryDTO getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return categoryMapper.entityToDto(category);
    }

    @Override
    @Cacheable(value = CATEGORY_CACHE, key = "'all'")
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.entitiesToDtos(categories);
    }

    @Override
    @Cacheable(value = CATEGORY_CACHE, key = "'active'")
    public List<CategoryDTO> getActiveCategories() {
        List<Category> categories = categoryRepository.findByActiveTrue();
        return categoryMapper.entitiesToDtos(categories);
    }

    @Override
    @Cacheable(value = CATEGORY_CACHE, key = "'sub:' + #parentId")
    public List<CategoryDTO> getSubCategories(Long parentId) {
        List<Category> subCategories = categoryRepository.findByParentIdOrderByDisplayOrder(parentId);
        return categoryMapper.entitiesToDtos(subCategories);
    }

    @Override
    @Cacheable(value = CATEGORY_CACHE, key = "'tree'")
    public List<CategoryTreeDTO> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findAllWithSubCategories();
        return categoryMapper.entitiesToTreeDtos(rootCategories);
    }

    @Override
    @Transactional
    @CacheEvict(value = CATEGORY_CACHE, allEntries = true)
    public CategoryDTO updateCategory(CategoryUpdateDTO updateDTO) {
        Category category = categoryRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + updateDTO.getId()));

        if (updateDTO.getParentId() != null &&
                !updateDTO.getParentId().equals(category.getParent() != null ? category.getParent().getId() : null)) {
            validateParentChange(category, updateDTO.getParentId());
            Category newParent = categoryRepository.findById(updateDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + updateDTO.getParentId()));
            category.setParent(newParent);
        }

        if (!category.getName().equals(updateDTO.getName())) {
            String baseSlug = slugify.slugify(updateDTO.getName());
            String finalSlug = generateUniqueSlug(baseSlug, category.getId());
            category.setSlug(finalSlug);
        }

        categoryMapper.updateEntityFromDto(updateDTO, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.entityToDto(updatedCategory);
    }

    @Override
    @Transactional
    @CacheEvict(value = CATEGORY_CACHE, allEntries = true)
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getSubCategories().isEmpty()) {
            throw new BadRequestException("Cannot delete category with subcategories");
        }

        if (!category.getProducts().isEmpty()) {
            throw new BadRequestException("Cannot delete category with associated products");
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    @CacheEvict(value = CATEGORY_CACHE, allEntries = true)
    public void reorderCategories(Long parentId, List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findByParentIdOrderByDisplayOrder(parentId);

        if (categories.size() != categoryIds.size() ||
                !categories.stream().map(Category::getId).collect(java.util.stream.Collectors.toSet())
                        .containsAll(categoryIds)) {
            throw new BadRequestException("Invalid category list provided for reordering");
        }

        IntStream.range(0, categoryIds.size()).forEach(i -> {
            Category category = categories.stream()
                    .filter(c -> c.getId().equals(categoryIds.get(i)))
                    .findFirst()
                    .orElseThrow();
            category.setDisplayOrder(i);
            categoryRepository.save(category);
        });
    }

    private String generateUniqueSlug(String baseSlug) {
        return generateUniqueSlug(baseSlug, null);
    }

    private String generateUniqueSlug(String baseSlug, Long excludeId) {
        String slug = baseSlug;
        int counter = 1;
        while (true) {
            boolean exists = categoryRepository.findBySlug(slug)
                    .map(category -> !category.getId().equals(excludeId))
                    .orElse(false);
            if (!exists) {
                return slug;
            }
            slug = baseSlug + "-" + counter++;
        }
    }

    private void validateParentChange(Category category, Long newParentId) {
        Category current = categoryRepository.findById(newParentId).orElse(null);
        while (current != null) {
            if (current.getId().equals(category.getId())) {
                throw new BadRequestException("Cannot set a subcategory as the parent category");
            }
            current = current.getParent();
        }
    }
}