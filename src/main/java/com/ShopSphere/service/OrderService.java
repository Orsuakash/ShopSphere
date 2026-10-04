package com.ShopSphere.service;

import com.ShopSphere.DTOS.CreateOrderRequest;
import com.ShopSphere.DTOS.OrderResponse;
import com.ShopSphere.DTOS.UpdateOrderStatusRequest;
import com.ShopSphere.Entity.*;
import com.ShopSphere.ExceptionHandling.ResourceNotFoundExpection;

import com.ShopSphere.Repository.CartRepository;
import com.ShopSphere.Repository.OrderRepository;
import com.ShopSphere.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final com.ShopSphere.mapper.OrderMapper orderMapper;

    // CREATE ORDER
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        User user = getCurrentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundExpection("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new com.ShopSphere.ExceptionHandling.BadRequestException("Cart is empty");
        }

        // Check stock
        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            if (product.isDeleted()) {
                throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                        "Product is not available: "
                                + product.getName()
                );
            }

            if (product.getStock() < cartItem.getQuantity()) {
                throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                        "Insufficient stock for: "
                                + product.getName()
                );
            }
        }

        // Create Order
        Order order = Order.builder()
                .user(user)
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Create OrderItems
        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            BigDecimal itemTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.getItems().add(orderItem);

            totalAmount = totalAmount.add(itemTotal);

            // Reduce stock
            product.setStock(
                    product.getStock()
                            - cartItem.getQuantity()
            );
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();

        return orderMapper.toResponse(savedOrder);
    }


    // GET MY ORDERS
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {

        User user = getCurrentUser();

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }


    // GET ORDER BY ID
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        User user = getCurrentUser();

        Order order = findOrder(orderId);

        if (!order.getUser().getId().equals(user.getId())
                && !isAdmin()) {

            throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                    "You are not allowed to access this order"
            );
        }

        return orderMapper.toResponse(order);
    }


    // CANCEL ORDER
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {

        User user = getCurrentUser();

        Order order = findOrder(orderId);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                    "You are not allowed to cancel this order"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                    "Order is already cancelled"
            );
        }

        if (order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED) {

            throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                    "Order cannot be cancelled now"
            );
        }

        // Restore stock
        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            product.setStock(
                    product.getStock()
                            + item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);

        return orderMapper.toResponse(
                orderRepository.save(order)
        );
    }


    // ADMIN UPDATE STATUS
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request) {

        Order order = findOrder(orderId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                    "Cancelled order cannot be updated"
            );
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                    "Delivered order cannot be updated"
            );
        }

        order.setStatus(request.getStatus());

        return orderMapper.toResponse(
                orderRepository.save(order)
        );
    }


    private Order findOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundExpection(
                                "Order not found with id: "
                                        + orderId
                        ));
    }


    private User getCurrentUser() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundExpection(
                                "User not found"
                        ));
    }


    private boolean isAdmin() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN"));
    }
}