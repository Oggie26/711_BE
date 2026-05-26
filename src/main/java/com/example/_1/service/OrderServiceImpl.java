package com.example._1.service;

import com.example._1.dto.request.OrderRequest;
import com.example._1.dto.response.OrderItemResponse;
import com.example._1.dto.response.OrderResponse;
import com.example._1.dto.response.PageResponse;
import com.example._1.entity.*;
import com.example._1.enums.EnumOrderStatus;
import com.example._1.enums.EnumPayment;
import com.example._1.enums.ErrorCode;
import com.example._1.exception.AppException;
import com.example._1.repository.CartRepository;
import com.example._1.repository.OrderRepository;
import com.example._1.repository.ProductRepository;
import com.example._1.repository.UserRepository;
import com.example._1.service.interfaces.OrderService;
import com.example._1.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final VNPayService vNPayService;

    @Override
    @Transactional
    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }
    @Override
    @Transactional
    public OrderResponse createOrder(Long cartId, EnumPayment paymentMethod, HttpServletRequest request) throws Exception {
        User user = userService.getCurrentUser();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Order order = buildOrder(cart,user);
        List<OrderItem> details = createOrderItemsFromCart(cart, order);
        order.setItems(details);

        if (paymentMethod.equals(EnumPayment.CASH)){
            order.setStatus(EnumOrderStatus.PAYMENT_SUCCESS);
            order.setPaymentMethod(paymentMethod);
            cart.getItems().clear();
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepository.save(cart);
        }
        orderRepository.save(order);

        if (paymentMethod.equals(EnumPayment.VNPAY)) {
            String paymentUrl = vNPayService.createPaymentUrl(
                    order.getId(),
                    order.getTotalPrice().doubleValue(),
                    request.getRemoteAddr()
            );
            OrderResponse response = mapToOrderResponse(order);
            response.setPaymentUrl(paymentUrl);
            return response;
        }

        return mapToOrderResponse(order);
    }
    private Order buildOrder(Cart cart, User user) {

        BigDecimal total = cart.getTotalPrice();

        if (total == null) {
            total = cart.getItems().stream()
                    .filter(item -> item.getPrice() != null && item.getQuantity() != null)
                    .map(item ->
                            item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    )
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return Order.builder()
                .totalPrice(total)
                .user(user)
                .orderDate(LocalDateTime.now())
                .build();
    }

    private List<OrderItem> createOrderItemsFromCart(Cart cart, Order order) {
        return cart.getItems().stream()
                .filter(item -> item.getPrice() != null && item.getQuantity() != null)
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrder(OrderRequest orderRequest) {
        throw new UnsupportedOperationException("Cần xác định logic update cụ thể");
    }

    @Override
    @Transactional
    public OrderResponse deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(EnumOrderStatus.CANCELLED);
        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::mapToOrderResponse).toList();
    }

    @Override
    public PageResponse<OrderResponse> searchOrders(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        String cleanKeyword = (keyword == null) ? "" : keyword.trim();

        Page<Order> orderPage = orderRepository.searchByKeywordNative(cleanKeyword, pageable);
        List<OrderResponse> data = orderPage.getContent().stream()
                .map(this::mapToOrderResponse)
                .toList();

        return PageResponse.<OrderResponse>builder()
                .data(data)
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional()
    public List<OrderResponse> getOrderBySelf() {
        User user = userService.getCurrentUser();

        return orderRepository.findAll().stream()
                .filter(order -> order.getUser() != null && order.getUser().getId().equals(user.getId()))
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional
    public void checkOut(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(EnumOrderStatus.PAYMENT_SUCCESS);
        orderRepository.save(order);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .payment(order.getPaymentMethod())
                .userId(order.getUser().getId())
                .fullName(userRepository.findById(order.getUser().getId()).map(User::getFullName).orElse(null))
                .items(itemResponses)
                .totalPrice(order.getTotalPrice())
                .build();
    }


}