package com.example.FakeCommerce.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.FakeCommerce.dtos.CreateProductRequestDto;
import com.example.FakeCommerce.dtos.GetProductResponseDto;
import com.example.FakeCommerce.dtos.GetProductWithDetailsResponseDto;
import com.example.FakeCommerce.exceptions.BadRequestException;
import com.example.FakeCommerce.exceptions.ResourceDeletionException;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Category;
import com.example.FakeCommerce.schema.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService  {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public List<GetProductResponseDto> getAllProducts(){
         List<Product> products =  productRepository.findAll();
        // List<GetProductResponseDto> responseDtos = new ArrayList<>();
        // for(Product product: products){
        //     GetProductResponseDto responseDto = GetProductResponseDto.builder()
        //                                         .id(product.getId())
        //                                         .title(product.getTitle())
        //                                          .description(product.getDescription())
        //                                         .price(product.getPrice())
        //                                         .image(product.getImage())
        //                                         .rating(product.getRating())
        //                                         .build();
        //     responseDtos.add(responseDto);
        // }
        // return responseDtos;

        //stream apis
        return products.stream()
                .map(product -> GetProductResponseDto.builder()
                                .id(product.getId())
                        .title(product.getTitle())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .image(product.getImage())
                        .rating(product.getRating())
                        .build()
                    )
                    .collect(Collectors.toList());
                
    }

    public GetProductResponseDto getProductById(Long id){
         if (id == null || id <= 0) {
            throw new BadRequestException("Product id must be a positive number");
        }

         return productRepository.findById(id)
            .map(product -> GetProductResponseDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .image(product.getImage())
                .rating(product.getRating())
                .build())
            .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
    }

    public GetProductWithDetailsResponseDto getProductWithDetailsById(Long id){
        if (id == null || id <= 0) {
            throw new BadRequestException("Product id must be a positive number");
        }

        Product product = productRepository.findProductWithDetailsById(id).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return GetProductWithDetailsResponseDto.builder()
                                                .id(product.getId())
                                                .title(product.getTitle())
                                                .description(product.getDescription())
                                                .price(product.getPrice())
                                                .category(product.getCategory().getName())
                                                .image(product.getImage())
                                                .rating(product.getRating())
                                                .build();
    }

    public Product createProduct(CreateProductRequestDto requestDto){
        if (requestDto == null) {
            throw new BadRequestException("Request body is required");
        }
        if (requestDto.getCategoryId() == null || requestDto.getCategoryId() <= 0) {
            throw new BadRequestException("Category id must be a positive number");
        }
       
        Category category = categoryService.getCategoryById(
            requestDto.getCategoryId()
        );
       
        Product newProduct = Product.builder()
                            .title(requestDto.getTitle())
                            .description(requestDto.getDescription())
                            .image(requestDto.getImage())
                            .price(requestDto.getPrice())
                            .category(category)
                            .rating(requestDto.getRating())
                            .build();

        return productRepository.save(newProduct);
    } 
    public void deleteProduct(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Product id must be a positive number");
        }

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }

        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceDeletionException("Product with id " + id + " cannot be deleted because it is linked to other records");
        }
    }

    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new BadRequestException("categoryName query parameter is required");
        }
        return productRepository.findByCategory(category);
    }

    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
}
