package com.example.FakeCommerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.adapters.ReviewAdapter;
import com.example.FakeCommerce.dtos.GetReviewResponseDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.ReviewRepository;
import com.example.FakeCommerce.schema.Review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewAdapter reviewAdapter;

    public List<GetReviewResponseDto> getAllReviews() {
        log.debug("Fetching all reviews");
        return reviewAdapter.mapToGetReviewResponseDtoList(reviewRepository.findAll());
    }

    public GetReviewResponseDto getReviewById(Long id) {
        log.debug("Fetching review with id {}", id);
        return reviewRepository.findById(id)
                .map(reviewAdapter::mapToGetReviewResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Review with id " + id + " not found"));
    }

    public List<GetReviewResponseDto> getReviewsByProductId(Long productId) {
        log.debug("Fetching reviews for product id {}", productId);
        return reviewAdapter.mapToGetReviewResponseDtoList(reviewRepository.findByProductId(productId));
    }

    public List<GetReviewResponseDto> getReviewsByOrderId(Long orderId) {
        log.debug("Fetching reviews for order id {}", orderId);
        return reviewAdapter.mapToGetReviewResponseDtoList(reviewRepository.findByOrderId(orderId));
    }

    public GetReviewResponseDto createReview(Review review) {
        GetReviewResponseDto savedReview = reviewAdapter.mapToGetReviewResponseDto(reviewRepository.save(review));
        log.info("Review created with id {}", savedReview.getId());
        return savedReview;
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review with id " + id + " not found"));
        reviewRepository.delete(review);
        log.info("Review with id {} deleted successfully", id);
    }
}
