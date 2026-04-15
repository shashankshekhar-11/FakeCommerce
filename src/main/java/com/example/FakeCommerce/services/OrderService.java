package com.example.FakeCommerce.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.adapters.OrderAdapter;
import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.repositories.OrderProductsRepository;
import com.example.FakeCommerce.repositories.OrderRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Order;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    public final OrderRepository orderRepository;
    public final OrderProductsRepository orderProductsRepository;
    public final ProductRepository productRepository;
    public final OrderAdapter orderAdapter;

    public List<GetOrderResponseDto> getAllOrders(){

        List<Order> orders = orderRepository.findAll();
        return orderAdapter.mapToGetOrderResponseDtoList(orders);
    }
}