package com.example.FakeCommerce.schema;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="products")
@SQLDelete(sql = "UPDATE products SET deleted_at = CURRENT_TIMESTAMP WHERE id =?")
@SQLRestriction("deleted_at is NULL")
public class Product extends BaseEntity {
 

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id" , nullable = false)
    private Category category;

    private String image;

   

    private String rating;
}
