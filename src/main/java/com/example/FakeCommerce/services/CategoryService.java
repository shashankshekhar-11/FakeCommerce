package com.example.FakeCommerce.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.FakeCommerce.dtos.CreateCategoryRequestDto;
import com.example.FakeCommerce.exceptions.BadRequestException;
import com.example.FakeCommerce.exceptions.ResourceDeletionException;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.CategoryRepository;
import com.example.FakeCommerce.schema.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
     private final CategoryRepository categoryRepository;

    public Category createCategory(CreateCategoryRequestDto requestDto){
        if (requestDto == null) {
            throw new BadRequestException("Request body is required");
        }
        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Category name is required");
        }

        Category newCategory = Category.builder()
                    .name(requestDto.getName())
                    .build();
        return categoryRepository.save(newCategory);
    }

     public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id){
        if (id == null || id <= 0) {
            throw new BadRequestException("Category id must be a positive number");
        }

        return categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
    }

    public void deleteCategory(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Category id must be a positive number");
        }

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceDeletionException("Category with id " + id + " cannot be deleted because it is linked to other records");
        }
    }
}
