package com.example.FakeCommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class OrderItemActionDto {
    private Long productId;
    private Integer quantity;
    private OrderItemAction action;
}
