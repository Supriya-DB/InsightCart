package com.example.demo.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Order;
import com.example.demo.entities.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("""
        SELECT o
        FROM Order o
        WHERE MONTH(o.createdAt) = :month
        AND YEAR(o.createdAt) = :year
        AND o.status = com.example.demo.entities.OrderStatus.SUCCESS
        """)
    List<Order> findSuccessfulOrdersByMonthAndYear(
            @Param("month") int month,
            @Param("year") int year);

    @Query("""
        SELECT o
        FROM Order o
        WHERE DATE(o.createdAt) = :date
        AND o.status = com.example.demo.entities.OrderStatus.SUCCESS
        """)
    List<Order> findSuccessfulOrdersByDate(
            @Param("date") LocalDate date);

    @Query("""
        SELECT o
        FROM Order o
        WHERE YEAR(o.createdAt) = :year
        AND o.status = com.example.demo.entities.OrderStatus.SUCCESS
        """)
    List<Order> findSuccessfulOrdersByYear(
            @Param("year") int year);

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = com.example.demo.entities.OrderStatus.SUCCESS
        """)
    BigDecimal calculateOverallBusiness();

    List<Order> findAllByStatus(OrderStatus status);
}