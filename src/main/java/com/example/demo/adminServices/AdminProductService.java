package com.example.demo.adminServices;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entities.Category;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductImage;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.ProductImageRepository;
import com.example.demo.repositories.ProductRepository;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;

    public AdminProductService(
            ProductRepository productRepository,
            ProductImageRepository productImageRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
    }


    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }


    // =========================================================
    // ADD PRODUCT WITH IMAGE
    // =========================================================

    public Product addProductWithImage(
            String name,
            String description,
            Double price,
            Integer stock,
            Integer categoryId,
            String imageUrl) {

        // Validate category

        Optional<Category> category =
                categoryRepository.findById(categoryId);

        if (category.isEmpty()) {

            throw new IllegalArgumentException(
                    "Invalid category ID"
            );
        }


        // Validate product name

        if (name == null || name.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Product name cannot be empty"
            );
        }


        // Validate price

        if (price == null || price < 0) {

            throw new IllegalArgumentException(
                    "Invalid product price"
            );
        }


        // Validate stock

        if (stock == null || stock < 0) {

            throw new IllegalArgumentException(
                    "Invalid product stock"
            );
        }


        // Validate image URL

        if (imageUrl == null || imageUrl.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Product image URL cannot be empty"
            );
        }


        // =====================================================
        // CREATE PRODUCT
        // =====================================================

        Product product = new Product();

        product.setName(name);

        product.setDescription(description);

        product.setPrice(
                BigDecimal.valueOf(price)
        );

        product.setStock(stock);

        product.setCategory(
                category.get()
        );

        product.setCreatedAt(
                LocalDateTime.now()
        );

        product.setUpdatedAt(
                LocalDateTime.now()
        );


        // =====================================================
        // SAVE PRODUCT
        // =====================================================

        Product savedProduct =
                productRepository.save(product);


        // =====================================================
        // CREATE PRODUCT IMAGE
        // =====================================================

        ProductImage productImage =
                new ProductImage();

        productImage.setProduct(
                savedProduct
        );

        productImage.setImageUrl(
                imageUrl
        );


        // =====================================================
        // SAVE PRODUCT IMAGE
        // =====================================================

        productImageRepository.save(
                productImage
        );


        return savedProduct;
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    public void deleteProduct(Integer productId) {

        // Validate product ID

        if (productId == null) {

            throw new IllegalArgumentException(
                    "Product ID is required"
            );
        }


        // =====================================================
        // CHECK PRODUCT EXISTS
        // =====================================================

        if (!productRepository.existsById(productId)) {

            throw new IllegalArgumentException(
                    "Product not found"
            );
        }


        // =====================================================
        // DELETE ASSOCIATED PRODUCT IMAGES
        // =====================================================

        productImageRepository.deleteByProductId(
                productId
        );


        // =====================================================
        // DELETE PRODUCT
        // =====================================================

        productRepository.deleteById(
                productId
        );
    }
}