package com.example.FakeCommerce.schema;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted_at = CURRENT_TIMESTAMP WHERE id =?")
@SQLRestriction("deleted_at is NULL")
public class Order extends BaseEntity{
    private OrderStatus status;

    // @ManyToMany
    // @JoinTable(
    //     name = "order_products",
    //     joinColumns = @JoinColumn(name = "order_id"), //the FK belonging to same schema -- orders
    //     inverseJoinColumns =  @JoinColumn(name = "product_id") //FK belonging to the other schema --Product
    // )
    // private List<Product> products;
    
}
