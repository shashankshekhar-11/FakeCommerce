package com.example.FakeCommerce.repositories;
import com.example.FakeCommerce.schema.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {

}
