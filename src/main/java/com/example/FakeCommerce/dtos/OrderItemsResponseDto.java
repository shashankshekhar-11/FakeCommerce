package com.example.FakeCommerce.dtos;

import lombok.Data;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemsResponseDto {
    private Long productId;

    private String productName;

    private BigDecimal productPrice;
    
    private String productImage;

    private Integer quantity;

    private BigDecimal subTotal;
}
