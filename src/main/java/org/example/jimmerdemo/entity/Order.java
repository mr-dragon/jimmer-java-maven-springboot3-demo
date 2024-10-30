package org.example.jimmerdemo.entity;

import org.babyfish.jimmer.sql.*;

import java.util.List;

/**
 * 订单表
 *
 * @create 2024-10-30 23:37
 */
@Entity
@Table(name = "test.order")
public interface Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id();

    @Key
    String name();

    // 一对多
    @OneToMany(mappedBy = "order")
    List<OrderDetail> details();
}
