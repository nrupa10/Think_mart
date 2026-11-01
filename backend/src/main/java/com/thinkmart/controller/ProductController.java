package com.thinkmart.controller;

import com.thinkmart.dto.CategoryResponse;
import com.thinkmart.dto.ProductRequest;
import com.thinkmart.dto.ProductResponse;
import com.thinkmart.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getProducts(@RequestParam(required = false) String query,
                                             @RequestParam(required = false) String category,
                                             @RequestParam(required = false) String sort) {
        return productService.findProducts(query, category, sort);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> getCategories() {
        return productService.findCategories();
    }

    @GetMapping("/{id:\\d+}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/slug/{slug}")
    public ProductResponse getProductBySlug(@PathVariable String slug) {
        return productService.findBySlug(slug);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }
}