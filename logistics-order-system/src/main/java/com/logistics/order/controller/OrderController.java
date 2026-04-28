package com.logistics.order.controller;

import com.logistics.order.dto.request.CreateOrderRequest;
import com.logistics.order.dto.request.UpdateStatusRequest;
import com.logistics.order.dto.response.ApiResponse;
import com.logistics.order.dto.response.OrderResponse;
import com.logistics.order.enums.OrderStatus;
import com.logistics.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Logistics Order Management API")
public class OrderController {

    private final OrderService orderService;

    // ── POST /api/v1/orders ───────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a logistics order with customer, pickup, and delivery details.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate order exists")
    })
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/v1/orders - Creating order for {}", request.getCustomerEmail());
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully.", response));
    }

    // ── GET /api/v1/orders ────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "List all orders", description = "Returns all orders, optionally filtered by status.")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @Parameter(description = "Filter by order status", example = "IN_TRANSIT")
            @RequestParam(required = false) OrderStatus status) {
        log.info("GET /api/v1/orders - status filter: {}", status);
        List<OrderResponse> orders = orderService.getAllOrders(status);
        String message = orders.isEmpty()
                ? "No orders found."
                : String.format("Retrieved %d order(s).", orders.size());
        return ResponseEntity.ok(ApiResponse.success(message, orders));
    }

    // ── GET /api/v1/orders/{id} ───────────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Returns full details and status history for a specific order.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @Parameter(description = "Order UUID", required = true)
            @PathVariable String id) {
        log.info("GET /api/v1/orders/{}", id);
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully.", response));
    }

    // ── PATCH /api/v1/orders/{id}/status ─────────────────────────────────────

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update order status",
        description = "Advances an order's status following the strict lifecycle: " +
                      "CREATED → PICKED_UP → IN_TRANSIT → DELIVERED. No skipping or backward transitions allowed."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Invalid status transition")
    })
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "Order UUID", required = true)
            @PathVariable String id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("PATCH /api/v1/orders/{}/status → {}", id, request.getNewStatus());
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(
                "Order status updated to " + response.getStatus() + ".", response));
    }
}
