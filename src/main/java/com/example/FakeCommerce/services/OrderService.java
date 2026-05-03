package com.example.FakeCommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.adapters.OrderAdapter;
import com.example.FakeCommerce.dtos.CreateOrderRequestDto;
import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.dtos.OrderItemActionDto;
import com.example.FakeCommerce.dtos.UpdateOrderRequestDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.OrderProductsRepository;
import com.example.FakeCommerce.repositories.OrderRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderProducts;
import com.example.FakeCommerce.schema.OrderStatus;
import com.example.FakeCommerce.schema.Product;

import jakarta.transaction.Transactional;
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

    @Transactional
    public GetOrderResponseDto createOrder (CreateOrderRequestDto createOrderRequestDto){
        // 1. We need to create new order instance
        Order order = Order
                    .builder()
                    .status(  OrderStatus.PENDING)
                    .build();
        orderRepository.save(order);
    
        // 2. If the payload(DTO) has same order products , add those in order as well
        //otherwise skip it
        if(createOrderRequestDto.getOrderItems() != null){
           List<Long> productIds = createOrderRequestDto.getOrderItems().stream().map(item -> item.getProductId()).collect(Collectors.toList());

          List<Product> products = productRepository.findAllById(productIds);
          Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
          
          for(Long id : productIds){
            if(!productMap.containsKey(id)){
                throw new ResourceNotFoundException("Product not found with id "+id);
            }

            List<OrderProducts> orderProducts = new ArrayList<>();

            for(var itemDto : createOrderRequestDto.getOrderItems()){
                Product product = productMap.get(itemDto.getProductId());

                orderProducts.add(OrderProducts.builder()
                .order(order)
                .product(product)
                .quantity(itemDto.getQuantity() !=null ?itemDto.getQuantity():1)
                .build());

                
            }
            orderProductsRepository.saveAll(orderProducts);
          }
        }  
        //return order
        return orderAdapter.mapToGetOrderResponseDto(order);
    }

    public GetOrderResponseDto updateOrder(Long id, UpdateOrderRequestDto updateOrderRequestDto){
        Order order = orderRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Order not found with id "+id));

        if(updateOrderRequestDto.getStatus() != null){
            order.setStatus(updateOrderRequestDto.getStatus());
            orderRepository.save(order);
        }

        if(updateOrderRequestDto.getOrderItems() != null){
            List<Long> productIds = updateOrderRequestDto.getOrderItems().stream().map(item -> item.getProductId()).collect(Collectors.toList());

            List<Product> products = productRepository.findAllById(productIds);

            Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

            for(var pid : productIds){
                if(!productMap.containsKey(pid)){
                    throw new ResourceNotFoundException("Product not found with id "+pid);
                }
            }

            List<OrderProducts> toSave = new ArrayList<>();
            List<OrderProducts> toDelete = new ArrayList<>();

            Map<Long,OrderProducts> existingItems = orderProductsRepository.findByOrderWithOroduct(order)
            .stream()
            .collect(Collectors.toMap(op -> op.getProduct().getId(), Function.identity()));

            for(OrderItemActionDto itemAction : updateOrderRequestDto.getOrderItems()){
                Product product = productMap.get(itemAction.getProductId());

                OrderProducts existing = existingItems.get(itemAction.getProductId());

                switch (itemAction.getAction()) {
                    case ADD ->{
                        if(existing == null){
                            int addQty = (itemAction.getQuantity() != null ? itemAction.getQuantity() : 1);
                            existing.setQuantity(existing.getQuantity()+addQty);
                            toSave.add(existing);
                        }else{
                            OrderProducts newItem = OrderProducts
                                                    .builder()
                                                    .order(order)
                                                    .product(product)
                                                    .quantity(itemAction.getQuantity() != null ? itemAction.getQuantity() : 1)
                                                    .build();

                            existingItems.put(product.getId(), newItem);
                            toSave.add(newItem);
                        }
                    }
                    case REMOVE ->{
                        if(existing == null) {
                            throw new ResourceNotFoundException("Product not found with id: " + product.getId());
                        }
                        toDelete.add(existing);
                        existingItems.remove(product.getId());
                    }
                    case INCREMENT -> {
                        if(existing == null) {
                            throw new ResourceNotFoundException("Product not found with id: " + product.getId());
                        }
                        existing.setQuantity(existing.getQuantity() + 1);
                        toSave.add(existing);
                    }
                    case DECREMENT ->{
                        if(existing == null) {
                            throw new ResourceNotFoundException("Product not found with id: " + product.getId());
                        }
                        if(existing.getQuantity() <= 1) {
                            toDelete.add(existing);
                            existingItems.remove(product.getId());
                        } else {
                            existing.setQuantity(existing.getQuantity() - 1);
                            toSave.add(existing);
                        }
                    }

                }
            }
            if(!toSave.isEmpty()) {
                orderProductsRepository.saveAll(toSave);
            }
            if(!toDelete.isEmpty()) {
                orderProductsRepository.deleteAll(toDelete);
            }

        }
        return orderAdapter.mapToGetOrderResponseDto(order);
    }
}

// User ->Cart ->Adds an item -> New Order(Pending)

//User ->adds more item in the cart -> Same order will be updated

// During checkout -> order Pending ->Sucess/Failure



