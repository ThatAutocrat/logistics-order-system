package com.logistics.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Request payload for creating a new logistics order")
public class CreateOrderRequest {

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    @Schema(description = "Full name of the customer", example = "John Doe")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address of the customer", example = "john.doe@example.com")
    private String customerEmail;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$", message = "Invalid phone number format")
    @Schema(description = "Contact phone number (optional)", example = "+91-9876543210")
    private String customerPhone;

    @NotBlank(message = "Pickup address is required")
    @Size(min = 5, max = 300, message = "Pickup address must be between 5 and 300 characters")
    @Schema(description = "Pickup address for the order", example = "123 Main Street, Mumbai, Maharashtra 400001")
    private String pickupAddress;

    @NotBlank(message = "Delivery address is required")
    @Size(min = 5, max = 300, message = "Delivery address must be between 5 and 300 characters")
    @Schema(description = "Delivery address for the order", example = "456 Park Avenue, Delhi 110001")
    private String deliveryAddress;

    @Pattern(regexp = "^(LOW|MEDIUM|HIGH|URGENT)$", message = "Priority must be one of: LOW, MEDIUM, HIGH, URGENT")
    @Schema(description = "Order priority level (optional)", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH", "URGENT"})
    private String priority;

    @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
    @DecimalMax(value = "10000.0", message = "Weight must not exceed 10,000 kg")
    @Schema(description = "Weight of the package in kilograms (optional)", example = "5.5")
    private Double weightKg;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Schema(description = "Additional notes or instructions (optional)", example = "Handle with care – fragile items")
    private String notes;
}
