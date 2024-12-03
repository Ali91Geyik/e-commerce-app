package org.allisra.ecommerceapp.repository;

import org.allisra.ecommerceapp.model.entity.Address;
import org.allisra.ecommerceapp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<Address> findByIdAndUser_Id(Long id, Long userId);

    Optional<Address> findByUser_IdAndDefaultShippingTrue(Long userId);

    Optional<Address> findByUser_IdAndDefaultBillingTrue(Long userId);

    long countByUser_Id(Long userId);

    boolean existsByIdAndUser_Id(Long id, Long userId);
    List<Address> findByUser(User user);

    List<Address> findByUserOrderByCreatedAtDesc(User user);

    Optional<Address> findByIdAndUser(Long id, User user);

    Optional<Address> findByUserAndDefaultShippingTrue(User user);

    Optional<Address> findByUserAndDefaultBillingTrue(User user);

    @Query("SELECT COUNT(a) FROM Address a WHERE a.user = :user")
    long countByUser(@Param("user") User user);

    boolean existsByIdAndUser(Long id, User user);
}