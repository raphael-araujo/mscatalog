package com.raphael.mscatalog.repositories;

import com.raphael.mscatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
            "WHERE (:query IS NULL OR lower(p.name) LIKE %:query% " +
            " OR lower(p.description) LIKE %:query%)" +
            " AND (:minPrice IS NULL OR p.price >= :minPrice)" +
            " AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> findByNameOrDescriptionAndPrice(String query, Double minPrice, Double maxPrice);
}
