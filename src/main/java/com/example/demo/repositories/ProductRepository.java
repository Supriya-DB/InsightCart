package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Find products by category
    List<Product> findByCategory_CategoryId(Integer categoryId);


    // Search products by name
    List<Product> findByNameContainingIgnoreCase(String name);


    // Get category name using product ID
    @Query(
        "SELECT p.category.categoryName " +
        "FROM Product p " +
        "WHERE p.productId = :productId"
    )
    String findCategoryNameByProductId(
            Integer productId
    );
}