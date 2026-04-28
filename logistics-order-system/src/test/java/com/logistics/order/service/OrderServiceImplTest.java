package com.logistics.order.service;

import com.logistics.order.dto.request.CreateOrderRequest;
import com.logistics.order.dto.request.UpdateStatusRequest;
import com.logistics.order.dto.response.OrderResponse;
import com.logistics.order.entity.Order;
import com.logistics.order.entity.StatusHistory;
import com.logistics.order.enums.OrderStatus;
import com.logistics.order.exception.DuplicateOrderException;
import com.logistics.order.exception.InvalidStatusTransitionException;
import com.logistics.order.exception.OrderAlreadyDeliveredException;
import com.logistics.order.exception.OrderNotFoundException;
import com.logistics.order.mapper.OrderMapper;
import com.logistics.order.repository.OrderRepository;
import com.logistics.order.repository.StatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl Unit Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StatusHistoryRepository statusHistoryRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private CreateOrderRequest validRequest;
    private Order sampleOrder;
    private OrderResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validRequest = new CreateOrderRequest();
        validRequest.setCustomerName("Alice Smith");
        validRequest.setCustomerEmail("alice@example.com");
        validRequest.setPickupAddress("123 Pickup Lane, Mumbai");
        validRequest.setDeliveryAddress("456 Delivery Ave, Delhi");
        validRequest.setPriority("HIGH");
        validRequest.setWeightKg(3.5);

        sampleOrder = Order.builder()
                .id("order-uuid-001")
                .customerName("Alice Smith")
                .customerEmail("alice@example.com")
                .pickupAddress("123 Pickup Lane, Mumbai")
                .deliveryAddress("456 Delivery Ave, Delhi")
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .statusHistories(new ArrayList<>())
                .build();

        sampleResponse = OrderResponse.builder()
                .id("order-uuid-001")
                .customerName("Alice Smith")
                .status(OrderStatus.CREATED)
                .build();
    }

    // ── createOrder tests ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("createOrder()")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully when no duplicate exists")
        void shouldCreateOrderSuccessfully() {
            when(orderRepository.existsDuplicateOrder(anyString(), anyString(), anyString())).thenReturn(false);
            when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);
            when(statusHistoryRepository.save(any(StatusHistory.class))).thenReturn(new StatusHistory());
            when(orderMapper.toResponse(any(Order.class))).thenReturn(sampleResponse);

            OrderResponse result = orderService.createOrder(validRequest);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("order-uuid-001");
            verify(orderRepository, times(1)).save(any(Order.class));
            verify(statusHistoryRepository, times(1)).save(any(StatusHistory.class));
        }

        @Test
        @DisplayName("Should throw DuplicateOrderException when same order exists")
        void shouldThrowWhenDuplicateExists() {
            when(orderRepository.existsDuplicateOrder(anyString(), anyString(), anyString())).thenReturn(true);

            assertThatThrownBy(() -> orderService.createOrder(validRequest))
                    .isInstanceOf(DuplicateOrderException.class)
                    .hasMessageContaining("alice@example.com");

            verify(orderRepository, never()).save(any());
        }
    }

    // ── getOrderById tests ────────────────────────────────────────────────────

    @Nested
    @DisplayName("getOrderById()")
    class GetOrderByIdTests {

        @Test
        @DisplayName("Should return order when found")
        void shouldReturnOrderWhenFound() {
            when(orderRepository.findById("order-uuid-001")).thenReturn(Optional.of(sampleOrder));
            when(orderMapper.toResponse(sampleOrder)).thenReturn(sampleResponse);

            OrderResponse result = orderService.getOrderById("order-uuid-001");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("order-uuid-001");
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when order does not exist")
        void shouldThrowWhenNotFound() {
            when(orderRepository.findById("non-existent")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getOrderById("non-existent"))
                    .isInstanceOf(OrderNotFoundException.class)
                    .hasMessageContaining("non-existent");
        }
    }

    // ── getAllOrders tests ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAllOrders()")
    class GetAllOrdersTests {

        @Test
        @DisplayName("Should return all orders when no status filter provided")
        void shouldReturnAllOrders() {
            when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sampleOrder));
            when(orderMapper.toResponse(sampleOrder)).thenReturn(sampleResponse);

            List<OrderResponse> result = orderService.getAllOrders(null);

            assertThat(result).hasSize(1);
            verify(orderRepository).findAllByOrderByCreatedAtDesc();
        }

        @Test
        @DisplayName("Should return filtered orders when status filter provided")
        void shouldReturnFilteredOrders() {
            when(orderRepository.findByStatusOrderByCreatedAtDesc(OrderStatus.CREATED)).thenReturn(List.of(sampleOrder));
            when(orderMapper.toResponse(sampleOrder)).thenReturn(sampleResponse);

            List<OrderResponse> result = orderService.getAllOrders(OrderStatus.CREATED);

            assertThat(result).hasSize(1);
            verify(orderRepository).findByStatusOrderByCreatedAtDesc(OrderStatus.CREATED);
        }
    }

    // ── updateOrderStatus tests ───────────────────────────────────────────────

    @Nested
    @DisplayName("updateOrderStatus()")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Should update status CREATED → PICKED_UP successfully")
        void shouldTransitionCreatedToPickedUp() {
            UpdateStatusRequest req = new UpdateStatusRequest();
            req.setNewStatus(OrderStatus.PICKED_UP);
            req.setRemarks("Package collected");

            OrderResponse updatedResponse = OrderResponse.builder()
                    .id("order-uuid-001")
                    .status(OrderStatus.PICKED_UP)
                    .build();

            when(orderRepository.findById("order-uuid-001")).thenReturn(Optional.of(sampleOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);
            when(statusHistoryRepository.save(any(StatusHistory.class))).thenReturn(new StatusHistory());
            when(orderMapper.toResponse(any(Order.class))).thenReturn(updatedResponse);

            OrderResponse result = orderService.updateOrderStatus("order-uuid-001", req);

            assertThat(result.getStatus()).isEqualTo(OrderStatus.PICKED_UP);
        }

        @Test
        @DisplayName("Should throw InvalidStatusTransitionException for skipped step (CREATED → IN_TRANSIT)")
        void shouldThrowForSkippedStep() {
            UpdateStatusRequest req = new UpdateStatusRequest();
            req.setNewStatus(OrderStatus.IN_TRANSIT);

            when(orderRepository.findById("order-uuid-001")).thenReturn(Optional.of(sampleOrder));

            assertThatThrownBy(() -> orderService.updateOrderStatus("order-uuid-001", req))
                    .isInstanceOf(InvalidStatusTransitionException.class)
                    .hasMessageContaining("CREATED")
                    .hasMessageContaining("IN_TRANSIT");
        }

        @Test
        @DisplayName("Should throw InvalidStatusTransitionException for backward transition")
        void shouldThrowForBackwardTransition() {
            sampleOrder.setStatus(OrderStatus.IN_TRANSIT);

            UpdateStatusRequest req = new UpdateStatusRequest();
            req.setNewStatus(OrderStatus.CREATED);

            when(orderRepository.findById("order-uuid-001")).thenReturn(Optional.of(sampleOrder));

            assertThatThrownBy(() -> orderService.updateOrderStatus("order-uuid-001", req))
                    .isInstanceOf(InvalidStatusTransitionException.class);
        }

        @Test
        @DisplayName("Should throw OrderAlreadyDeliveredException when order is DELIVERED")
        void shouldThrowWhenAlreadyDelivered() {
            sampleOrder.setStatus(OrderStatus.DELIVERED);

            UpdateStatusRequest req = new UpdateStatusRequest();
            req.setNewStatus(OrderStatus.DELIVERED);

            when(orderRepository.findById("order-uuid-001")).thenReturn(Optional.of(sampleOrder));

            assertThatThrownBy(() -> orderService.updateOrderStatus("order-uuid-001", req))
                    .isInstanceOf(OrderAlreadyDeliveredException.class)
                    .hasMessageContaining("order-uuid-001");
        }
    }
}
