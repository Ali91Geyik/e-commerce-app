package org.allisra.ecommerceapp.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.allisra.ecommerceapp.model.dto.category.CategoryCreateDTO;
import org.allisra.ecommerceapp.model.dto.category.CategoryDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductCreateDTO;
import org.allisra.ecommerceapp.model.entity.Role;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.repository.RoleRepository;
import org.allisra.ecommerceapp.repository.UserRepository;
import org.allisra.ecommerceapp.service.CategoryService;
import org.allisra.ecommerceapp.service.ProductService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test") // Test profili dışındaki profillerde çalışacak
@DependsOn({"securityConfig", "passwordEncoder"})
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService;
    private final ProductService productService;


    @PostConstruct
    @Transactional
    public void initialize() {
        try {
            log.info("Checking if database is initialized...");
            if (isInitialized()) {
                log.info("Database is already initialized, skipping...");
                return;
            }

            log.info("Starting data initialization...");

            log.info("Creating roles...");
            createRoles();

            log.info("Creating admin user...");
            createAdminUser();
            createSampleCategories();
            createSampleProducts();
            log.info("Data initialization completed successfully");
        } catch (Exception e) {
            log.error("Error during initialization: ", e);
            throw e;
        }
    }

    private boolean isInitialized() {
        long roleCount = roleRepository.count();
        log.info("Current role count: {}", roleCount);
        return roleCount > 0;
    }

    private void createRoles() {
        try {
            createRoleIfNotExists("ROLE_USER");
            createRoleIfNotExists("ROLE_ADMIN");
            log.info("Roles created successfully");
        } catch (Exception e) {
            log.error("Error creating roles: ", e);
            throw e;
        }
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            log.info("Created role: {}", roleName);
        } else {
            log.info("Role already exists: {}", roleName);
        }
    }

    private void createAdminUser() {
        String adminEmail = "geyik91al@gmail.com";
        try {
            if (!userRepository.findByEmail(adminEmail).isPresent()) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("Admin role not found"));

                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("Admin123!@#"));
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setEmailVerified(true);
                admin.setActive(true);
                admin.setRoles(Set.of(adminRole));

                User savedUser = userRepository.save(admin);
                log.info("Admin user created successfully with id: {}", savedUser.getId());
            } else {
                log.info("Admin user already exists");
            }
        } catch (Exception e) {
            log.error("Error creating admin user: ", e);
            throw e;
        }
    }

    private void createSampleCategories() {
        log.info("Creating sample categories...");
        createCategory("Electronics", "Electronic devices and accessories");
        createCategory("Books", "Books and e-books");
        createCategory("Clothing", "Men's and women's clothing");
    }

    private void createCategory(String name, String description) {
        try {
            CategoryCreateDTO dto = new CategoryCreateDTO();
            dto.setName(name);
            dto.setDescription(description);
            categoryService.createCategory(dto);
            log.info("Created category: {}", name);
        } catch (Exception e) {
            log.error("Error creating category {}: {}", name, e.getMessage());
        }
    }

    private void createSampleProducts() {
        log.info("Creating sample products...");
        // Electronics kategorisini bul ve ID'sini kullan
        CategoryDTO electronicsCategory = categoryService.getCategoryBySlug("electronics");

        if (electronicsCategory != null) {
            createProduct(
                    "Smartphone XYZ",
                    "Latest smartphone model",
                    new BigDecimal("999.99"),
                    50,
                    "PHONE-001",
                    electronicsCategory.getId()
            );
            createProduct(
                    "Laptop Pro",
                    "Professional laptop",
                    new BigDecimal("1499.99"),
                    30,
                    "LAPTOP-001",
                    electronicsCategory.getId()
            );
            createProduct(
                    "Wireless Earbuds",
                    "High-quality wireless earbuds",
                    new BigDecimal("199.99"),
                    100,
                    "EARBUD-001",
                    electronicsCategory.getId()
            );
        } else {
            log.error("Electronics category not found, skipping product creation");
        }
    }

    private void createProduct(
            String name,
            String description,
            BigDecimal price,
            Integer stock,
            String sku,
            Long categoryId
    ) {
        try {
            ProductCreateDTO dto = new ProductCreateDTO();
            dto.setName(name);
            dto.setDescription(description);
            dto.setPrice(price);
            dto.setStockQuantity(stock);
            dto.setSku(sku);
            dto.setCategoryId(categoryId);  // Category ID'sini set ediyoruz

            productService.createProduct(dto);
            log.info("Created product: {}", name);
        } catch (Exception e) {
            log.error("Error creating product {}: {}", name, e.getMessage());
        }
    }
}