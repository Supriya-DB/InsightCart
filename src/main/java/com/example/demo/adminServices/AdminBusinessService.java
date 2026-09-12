package com.example.demo.adminServices;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.entities.Order;
import com.example.demo.entities.OrderItem;
import com.example.demo.entities.OrderStatus;
import com.example.demo.repositories.OrderItemRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.ProductRepository;

@Service
public class AdminBusinessService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public AdminBusinessService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    // =========================================================
    // MONTHLY BUSINESS
    // =========================================================

    public Map<String, Object> calculateMonthlyBusiness(
            int month,
            int year) {

        List<Order> successfulOrders =
                orderRepository.findSuccessfulOrdersByMonthAndYear(
                        month,
                        year
                );

        return calculateBusinessMetrics(successfulOrders);
    }

    // =========================================================
    // DAILY BUSINESS
    // =========================================================

    public Map<String, Object> calculateDailyBusiness(
            LocalDate date) {

        List<Order> successfulOrders =
                orderRepository.findSuccessfulOrdersByDate(
                        date
                );

        return calculateBusinessMetrics(successfulOrders);
    }

    // =========================================================
    // YEARLY BUSINESS
    // =========================================================

    public Map<String, Object> calculateYearlyBusiness(
            int year) {

        List<Order> successfulOrders =
                orderRepository.findSuccessfulOrdersByYear(
                        year
                );

        return calculateBusinessMetrics(successfulOrders);
    }

    // =========================================================
    // OVERALL BUSINESS
    // =========================================================

    public Map<String, Object> calculateOverallBusiness() {

    	List<Order> successfulOrders =
    	        orderRepository.findAllByStatus(OrderStatus.SUCCESS);

        BigDecimal totalBusiness =
                orderRepository.calculateOverallBusiness();

        Map<String, Object> response =
                calculateBusinessMetrics(successfulOrders);

        response.put(
                "totalBusiness",
                totalBusiness.doubleValue()
        );

        return response;
    }

    // =========================================================
    // BUSINESS METRICS
    // =========================================================

    private Map<String, Object> calculateBusinessMetrics(
            List<Order> orders) {

        double totalRevenue = 0.0;

        Map<String, Integer> categorySales =
                new HashMap<>();

        // Process every successful order
        for (Order order : orders) {

            // Add order revenue
            if (order.getTotalAmount() != null) {

                totalRevenue +=
                        order.getTotalAmount().doubleValue();
            }

            // Get order items
            List<OrderItem> items =
                    orderItemRepository.findByOrder_OrderId(
                            order.getOrderId()
                    );

            // Process every order item
            for (OrderItem item : items) {

                String categoryName =
                        productRepository.findCategoryNameByProductId(
                                item.getProductId()
                        );

                if (categoryName != null) {

                    categorySales.put(
                            categoryName,
                            categorySales.getOrDefault(
                                    categoryName,
                                    0
                            ) + item.getQuantity()
                    );
                }
            }
        }

        // Create response
        Map<String, Object> metrics =
                new HashMap<>();

        metrics.put(
                "totalRevenue",
                totalRevenue
        );

        metrics.put(
                "categorySales",
                categorySales
        );

        return metrics;
    }
}