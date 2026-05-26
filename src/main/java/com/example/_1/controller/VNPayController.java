package com.example._1.controller;

import com.example._1.entity.*;
import com.example._1.enums.EnumOrderStatus;
import com.example._1.enums.ErrorCode;
import com.example._1.exception.AppException;
import com.example._1.repository.CartRepository;
import com.example._1.repository.OrderRepository;
import com.example._1.repository.ProductRepository;
import com.example._1.service.VNPayService;
import com.example._1.util.VNPayUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class VNPayController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @GetMapping("/vnpay-return")
    public void vnpayReturn(
            @RequestParam Map<String, String> vnpParams,
            HttpServletResponse response
    ) throws IOException {

        String secureHash = vnpParams.remove("vnp_SecureHash");
        vnpParams.remove("vnp_SecureHashType");

        String signValue = VNPayUtils.hashAllFields(vnpParams, hashSecret);

        String orderId = vnpParams.get("vnp_TxnRef");
        String responseCode = vnpParams.get("vnp_ResponseCode");

        String webUrl = "https://711-fe.vercel.app/payment-success";

        if (signValue.equalsIgnoreCase(secureHash)) {

            if ("00".equals(responseCode)) {

                try {
                    Order order = orderRepository.findById(Long.parseLong(orderId))
                            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

                    if (order.getStatus() != EnumOrderStatus.PAYMENT_SUCCESS) {
                        order.setStatus(EnumOrderStatus.PAYMENT_SUCCESS);

                        for (OrderItem orderItem : order.getItems()) {
                            Product product = orderItem.getProduct();
                            if (product.getStock() < orderItem.getQuantity()) {
                                throw new AppException(ErrorCode.OUT_OF_STOCK);
                            }
                            product.setStock(product.getStock() - orderItem.getQuantity());
                            productRepository.save(product);
                        }

                        Cart cart = cartRepository.findByUser(order.getUser())
                                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

                        cart.getItems().clear();
                        cart.setTotalPrice(BigDecimal.ZERO);
                        cartRepository.save(cart);

                        orderRepository.save(order);
                    }

                    response.sendRedirect(
                            webUrl
                                    + "?status=success"
                                    + "&orderId=" + URLEncoder.encode(orderId, StandardCharsets.UTF_8)
                    );

                } catch (Exception e) {

                    response.sendRedirect(
                            webUrl + "?status=error"
                    );
                }

            } else {

                response.sendRedirect(
                        webUrl
                                + "?status=failed"
                                + "&code=" + URLEncoder.encode(responseCode, StandardCharsets.UTF_8)
                );
            }

        } else {

            response.sendRedirect(
                    webUrl + "?status=invalid"
            );
        }
    }
}