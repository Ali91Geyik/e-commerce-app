package org.allisra.ecommerceapp.repository;

import org.allisra.ecommerceapp.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.displayOrder")
    List<Category> findAllParentCategories();

    List<Category> findByParentIdOrderByDisplayOrder(Long parentId);

    List<Category> findByActiveTrue();

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.parent IS NULL")
    List<Category> findAllWithSubCategories();
}
