package com.example.FakeCommerce.adapters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.dtos.OrderItemsResponseDto;
import com.example.FakeCommerce.repositories.OrderProductsRepository;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderProducts;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class OrderAdapter {
    public final OrderProductsRepository orderProductsRepository;
    
    public List<GetOrderResponseDto>mapToGetOrderResponseDtoList(List<Order>orders){
        return orders.stream()
            .map(this::mapToGetOrderResponseDto)
            .collect(Collectors.toList());
    }

    public GetOrderResponseDto mapToGetOrderResponseDto(Order order){

        List<OrderProducts> orderProducts = orderProductsRepository.findByOrderId(order.getId());
        List<OrderItemsResponseDto> items = mapToOrderItemsResponseDto(orderProducts);

        return GetOrderResponseDto.builder()
               .id(order.getId())
               .status(order.getStatus())
               .createdAt(order.getCreatedAt())
               .updatedAt(order.getUpdatedAt())
               .items(items)
               .build();
    }

    public List<OrderItemsResponseDto> mapToOrderItemsResponseDto(List<OrderProducts> orderProducts){
        return orderProducts.stream()
        .map(op -> OrderItemsResponseDto.builder()
             .productId(op.getProduct().getId())
             .productName(op.getProduct().getTitle())
             .productPrice(op.getProduct().getPrice())
             .productImage(op.getProduct().getImage())
             .quantity(op.getQuantity())
             .subTotal(op.getProduct().getPrice().multiply(BigDecimal.valueOf(op.getQuantity())))
             .build())
        .collect(Collectors.toList());
    }

}
