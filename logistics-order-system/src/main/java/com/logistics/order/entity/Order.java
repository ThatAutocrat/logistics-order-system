package com.logistics.order.entity;

import com.logistics.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "orders",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_order_customer_pickup_delivery",
            columnNames = {"customer_name", "customer_email", "pickup_address", "delivery_address"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Customer Details
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    // Addresses
    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    // Metadata
    @Column(name = "priority")
    private String priority;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Column(name = "notes", length = 1000)
    private String notes;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Status history (one-to-many)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("changedAt ASC")
    @Builder.Default
    private List<StatusHistory> statusHistories = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = OrderStatus.CREATED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
