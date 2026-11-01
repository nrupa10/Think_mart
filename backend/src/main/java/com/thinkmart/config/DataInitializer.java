package com.thinkmart.config;

import com.thinkmart.entity.Category;
import com.thinkmart.entity.Product;
import com.thinkmart.entity.Role;
import com.thinkmart.entity.User;
import com.thinkmart.repository.CategoryRepository;
import com.thinkmart.repository.ProductRepository;
import com.thinkmart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initializeThinkMart(CategoryRepository categories, ProductRepository products,
                                          UserRepository users, PasswordEncoder passwordEncoder,
                                          @Value("${app.admin.email}") String adminEmail,
                                          @Value("${app.admin.password}") String adminPassword,
                                          @Value("${app.admin.name}") String adminName) {
        return args -> initialize(categories, products, users, passwordEncoder, adminEmail, adminPassword, adminName);
    }

    @Transactional
    void initialize(CategoryRepository categories, ProductRepository products, UserRepository users,
                    PasswordEncoder passwordEncoder, String adminEmail, String adminPassword, String adminName) {
        List<CategorySeed> categorySeeds = List.of(
                new CategorySeed("Laptops", "laptops", "Powerful tools for your next big idea."),
                new CategorySeed("Mobiles", "mobiles", "The latest phones, made for everyday life."),
                new CategorySeed("Watches", "watches", "Smart companions and timeless design."),
                new CategorySeed("Audio", "audio", "Sound that brings every moment closer."),
                new CategorySeed("Accessories", "accessories", "Thoughtful extras for your setup."));
        for (CategorySeed seed : categorySeeds) {
            if (categories.findBySlug(seed.slug()).isEmpty()) {
                Category category = new Category();
                category.setName(seed.name());
                category.setSlug(seed.slug());
                category.setDescription(seed.description());
                categories.save(category);
            }
        }

        List<ProductSeed> productSeeds = List.of(
                new ProductSeed("aero-14", "Aero 14 Pro Laptop", "Thin, quiet, and ready for demanding creative work. 14-inch display, 16 GB memory, 512 GB storage.", 1299, 1499, 4.8, 18, "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=1000&q=85", "laptops", true),
                new ProductSeed("studio-16", "Studio 16 Laptop", "A vivid 16-inch workspace with a fast processor and all-day battery life.", 1599, 1799, 4.7, 12, "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?auto=format&fit=crop&w=1000&q=85", "laptops", true),
                new ProductSeed("everyday-15", "Everyday 15 Laptop", "A dependable everyday laptop for study, work, and everything after.", 749, 899, 4.5, 25, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=1000&q=85", "laptops", false),
                new ProductSeed("nova-phone", "Nova X Smartphone", "A bright OLED display, pro-grade camera, and fast wireless charging.", 899, 999, 4.8, 30, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=1000&q=85", "mobiles", true),
                new ProductSeed("pixel-phone", "Pixel One Smartphone", "A pocket-sized powerhouse with a clean interface and all-day battery.", 699, 799, 4.6, 22, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=1000&q=85", "mobiles", true),
                new ProductSeed("fold-phone", "Fold Mini Smartphone", "A flexible screen that opens up more room for the things you love.", 1099, 1249, 4.4, 9, "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?auto=format&fit=crop&w=1000&q=85", "mobiles", false),
                new ProductSeed("orbit-watch", "Orbit Smartwatch", "Health insights, useful notifications, and a refined stainless-steel finish.", 279, 329, 4.7, 35, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=1000&q=85", "watches", true),
                new ProductSeed("field-watch", "Field Automatic Watch", "A crisp, easy-to-read dial with a comfortable everyday strap.", 189, 229, 4.5, 17, "https://images.unsplash.com/photo-1524805444758-089113d48a6d?auto=format&fit=crop&w=1000&q=85", "watches", false),
                new ProductSeed("sport-watch", "Pace Sport Watch", "Lightweight GPS tracking and recovery insights for your daily training.", 219, 269, 4.6, 20, "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?auto=format&fit=crop&w=1000&q=85", "watches", true),
                new ProductSeed("studio-headphones", "Studio Wireless Headphones", "Immersive noise cancellation with balanced, detailed sound.", 249, 299, 4.8, 40, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=1000&q=85", "audio", true),
                new ProductSeed("airbuds-pro", "Airbuds Pro Earbuds", "Clear calls, rich audio, and a compact case made for your commute.", 149, 179, 4.6, 50, "https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?auto=format&fit=crop&w=1000&q=85", "audio", true),
                new ProductSeed("desk-speaker", "Room Wireless Speaker", "Room-filling sound in a compact, beautifully simple enclosure.", 129, 159, 4.4, 14, "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=1000&q=85", "audio", false),
                new ProductSeed("arc-keyboard", "Arc Mechanical Keyboard", "A satisfying low-profile typing feel with wireless multi-device support.", 119, 149, 4.7, 28, "https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=1000&q=85", "accessories", true),
                new ProductSeed("everyday-backpack", "Everyday Tech Backpack", "Weather-resistant storage with a padded sleeve and quick-access pockets.", 89, 109, 4.5, 31, "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=85", "accessories", false),
                new ProductSeed("wireless-charger", "Magnetic Wireless Charger", "A compact, fast charger that keeps your desk and nightstand tidy.", 39, 49, 4.4, 64, "https://images.unsplash.com/photo-1609592806596-b43bada9f2b7?auto=format&fit=crop&w=1000&q=85", "accessories", true));

        Map<String, Category> categoryMap = categories.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(Category::getSlug, category -> category));
        for (ProductSeed seed : productSeeds) {
            if (!products.existsBySlug(seed.slug())) {
                Product product = new Product();
                product.setSlug(seed.slug());
                product.setName(seed.name());
                product.setDescription(seed.description());
                product.setPrice(BigDecimal.valueOf(seed.price()));
                product.setOriginalPrice(BigDecimal.valueOf(seed.originalPrice()));
                product.setRating(BigDecimal.valueOf(seed.rating()));
                product.setStock(seed.stock());
                product.setImageUrl(seed.imageUrl());
                product.setCategory(categoryMap.get(seed.category()));
                product.setFeatured(seed.featured());
                products.save(product);
            }
        }

        if (adminEmail != null && !adminEmail.isBlank() && adminPassword != null && !adminPassword.isBlank()
                && !users.existsByEmail(adminEmail.trim().toLowerCase())) {
            if (adminPassword.length() < 12) {
                throw new IllegalStateException("ADMIN_PASSWORD must contain at least 12 characters.");
            }
            User admin = new User();
            admin.setFullName(adminName == null || adminName.isBlank() ? "ThinkMart Admin" : adminName.trim());
            admin.setEmail(adminEmail.trim().toLowerCase());
            admin.setPhone("0000000000");
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            users.save(admin);
        }
    }

    private record CategorySeed(String name, String slug, String description) { }

    private record ProductSeed(String slug, String name, String description, double price, double originalPrice,
                               double rating, int stock, String imageUrl, String category, boolean featured) { }
}