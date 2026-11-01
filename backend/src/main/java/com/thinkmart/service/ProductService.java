package com.thinkmart.service;

import com.thinkmart.dto.CategoryResponse;
import com.thinkmart.dto.ProductRequest;
import com.thinkmart.dto.ProductResponse;
import com.thinkmart.entity.Category;
import com.thinkmart.entity.Product;
import com.thinkmart.exception.ApiException;
import com.thinkmart.repository.CategoryRepository;
import com.thinkmart.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ProductService {
    private final ProductRepository products;
    private final CategoryRepository categories;

    public ProductService(ProductRepository products, CategoryRepository categories) {
        this.products = products;
        this.categories = categories;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findProducts(String query, String categorySlug, String sort) {
        Specification<Product> specification = (root, criteriaQuery, builder) -> builder.conjunction();
        if (query != null && !query.isBlank()) {
            String pattern = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
            specification = specification.and((root, criteriaQuery, builder) -> builder.or(
                    builder.like(builder.lower(root.get("name")), pattern),
                    builder.like(builder.lower(root.get("description")), pattern)));
        }
        if (categorySlug != null && !categorySlug.isBlank()) {
            specification = specification.and((root, criteriaQuery, builder) ->
                    builder.equal(root.join("category").get("slug"), categorySlug.trim().toLowerCase(Locale.ROOT)));
        }
        Sort ordering = switch (sort == null ? "" : sort) {
            case "price-asc" -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "rating" -> Sort.by("rating").descending();
            case "newest" -> Sort.by("createdAt").descending();
            default -> Sort.by("featured").descending().and(Sort.by("id").ascending());
        };
        return products.findAll(specification, ordering).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return products.findWithCategoryById(id).map(this::toResponse)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found."));
    }

    @Transactional(readOnly = true)
    public ProductResponse findBySlug(String slug) {
        return products.findWithCategoryBySlug(slug).map(this::toResponse)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found."));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findCategories() {
        return categories.findAll(Sort.by("name")).stream().map(this::toCategoryResponse).toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (products.existsBySlug(request.slug().trim().toLowerCase(Locale.ROOT))) {
            throw new ApiException(HttpStatus.CONFLICT, "A product with this slug already exists.");
        }
        return toResponse(products.save(apply(new Product(), request)));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = products.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found."));
        if (products.existsBySlugAndIdNot(request.slug().trim().toLowerCase(Locale.ROOT), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "A product with this slug already exists.");
        }
        return toResponse(apply(product, request));
    }

    @Transactional
    public void delete(Long id) {
        if (!products.existsById(id)) throw new ApiException(HttpStatus.NOT_FOUND, "Product not found.");
        products.deleteById(id);
    }

    private Product apply(Product product, ProductRequest request) {
        if (request.price().compareTo(request.originalPrice()) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Sale price cannot exceed original price.");
        }
        Category category = categories.findById(request.categoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Category not found."));
        product.setSlug(request.slug().trim().toLowerCase(Locale.ROOT));
        product.setName(request.name().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        product.setOriginalPrice(request.originalPrice());
        product.setRating(request.rating());
        product.setStock(request.stock());
        product.setImageUrl(request.imageUrl().trim());
        product.setFeatured(request.featured());
        product.setCategory(category);
        return product;
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getSlug(), product.getName(), product.getDescription(),
                product.getPrice(), product.getOriginalPrice(), product.getRating(), product.getStock(),
                product.getImageUrl(), product.isFeatured(), toCategoryResponse(product.getCategory()));
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getSlug(), category.getDescription());
    }
}