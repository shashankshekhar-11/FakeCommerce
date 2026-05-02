package com.example.FakeCommerce.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.adapters.OrderAdapter;
import com.example.FakeCommerce.dtos.CreateOrderRequestDto;
import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.OrderProductsRepository;
import com.example.FakeCommerce.repositories.OrderRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderProducts;
import com.example.FakeCommerce.schema.OrderStatus;
import com.example.FakeCommerce.schema.Product;

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

    public GetOrderResponseDto getOrderById(Long id){
        Order order = orderRepository.findById(id)
              .orElseThrow(()-> new ResourceNotFoundException("Order not founf with id "+id));
        return orderAdapter.mapToGetOrderResponseDto(order);
    }

    public void deleteOrder (Long id){
        Order order = orderRepository.findById(id)
              .orElseThrow(()-> new ResourceNotFoundException("Order not found with id "+id));
        orderRepository.delete(order);
    }

    public void createOrder (CreateOrderRequestDto createOrderRequestDto){
        // 1. We need to create new order instance
        Order order = Order
                    .builder()
                    .status(  OrderStatus.PENDING)
                    .build();
        orderRepository.save(order);
    
        // 2. If the payload(DTO) has same order products , add those in order as well
        //otherwise skip it
        if(createOrderRequestDto.getOrderItems() != null){
            for(var itemDto :createOrderRequestDto.getOrderItems()){
                Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(()->new ResourceNotFoundException("Resource not found with product id"+itemDto.getProductId()));

                OrderProducts orderProducts = OrderProducts.builder()
                .order(order)
                .product(product)
                .quantity(itemDto.getQuantity() !=null ?itemDto.getQuantity():1)
                .build();
                
                
                orderProductsRepository.save(orderProducts);
            }
        }
        
        //return order
    }
}

// User ->Cart ->Adds an item -> New Order(Pending)

//User ->adds more item in the cart -> Same order will be updated

// During checkout -> order Pending ->Sucess/Failure



