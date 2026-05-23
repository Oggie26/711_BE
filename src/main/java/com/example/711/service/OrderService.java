package com.example._1.service;

import com.example._1.dto.request.OrderItemRequest;
import com.example._1.dto.request.OrderRequest;
import com.example._1.dto.response.OrderItemResponse;
import com.example._1.dto.response.OrderResponse;
import com.example._1.entity.*;
import com.example._1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final PromotionRepository promotionRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public OrderResponse createOrder(String username, OrderRequest request) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Order order = new Order();
        order.setAccount(account);

        if (request.getStoreId() != null) {
            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new RuntimeException("Store not found"));
            order.setStore(store);
        }

        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPriceAtPurchase(product.getPrice());
            item.setOrder(order);

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subTotal = subTotal.add(itemTotal);
            
            order.getItems().add(item);
        }

        order.setSubTotal(subTotal);
        BigDecimal discount = BigDecimal.ZERO;

        if (request.getPromotionCode() != null && !request.getPromotionCode().isEmpty()) {
            Promotion promo = promotionRepository.findByCode(request.getPromotionCode())
                    .orElseThrow(() -> new RuntimeException("Promotion not found"));
            
            if (!promo.isActive() || promo.getStartDate().isAfter(LocalDateTime.now()) || promo.getEndDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Promotion is invalid or expired");
            }
            order.setPromotion(promo);
            
            if (promo.getDiscountAmount() != null) {
                discount = promo.getDiscountAmount();
            } else if (promo.getDiscountPercentage() != null) {
                discount = subTotal.multiply(BigDecimal.valueOf(promo.getDiscountPercentage())).divide(BigDecimal.valueOf(100));
            }
        }

        BigDecimal total = subTotal.subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        order.setDiscountAmount(discount);
        order.setTotalPrice(total);

        // Earn 1 point per 10,000 VND spent
        Integer pointsEarned = total.divide(BigDecimal.valueOf(10000)).intValue();
        order.setLoyaltyPointsEarned(pointsEarned);

        if (account.getUserProfile() != null) {
            UserProfile profile = account.getUserProfile();
            profile.setLoyaltyPoints((profile.getLoyaltyPoints() == null ? 0 : profile.getLoyaltyPoints()) + pointsEarned);
            userProfileRepository.save(profile);
        }

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByUser(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return orderRepository.findByAccount(account).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public OrderResponse getOrderDetails(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToResponse(order);
    }

    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return mapToResponse(orderRepository.save(order));
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream().map(item ->
                OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .build()
        ).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .accountId(order.getAccount().getId().toString())
                .storeName(order.getStore() != null ? order.getStore().getName() : "Online Delivery")
                .status(order.getStatus())
                .subTotal(order.getSubTotal())
                .discountAmount(order.getDiscountAmount())
                .totalPrice(order.getTotalPrice())
                .loyaltyPointsEarned(order.getLoyaltyPointsEarned())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}
