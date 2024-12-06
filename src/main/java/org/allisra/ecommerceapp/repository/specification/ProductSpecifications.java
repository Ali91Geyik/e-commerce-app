package org.allisra.ecommerceapp.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.allisra.ecommerceapp.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
public class ProductSpecifications {
    public static Specification<Product> withFilters(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String brand,
            BigDecimal minRating,
            Long categoryId,
            Boolean active) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (brand != null && !brand.isEmpty()) {
                predicates.add(cb.equal(root.get("brand"), brand));
            }

            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), minRating));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
