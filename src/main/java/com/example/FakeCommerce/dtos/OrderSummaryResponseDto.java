package com.example.FakeCommerce.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.FakeCommerce.schema.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSummaryResponseDto {
    private Long orderId;
    private OrderStatus status;
    private Integer totalItems;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
