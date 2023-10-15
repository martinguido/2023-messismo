package com.messismo.bar.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_order")
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productOrderId")
    private Long productOrderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product", referencedColumnName = "product_id")
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Override
    public String toString() {
        return "ProductOrder{" +
                "productOrderId=" + productOrderId +
                ", product=" + product +
                ", quantity=" + quantity +
                '}';
    }
}
