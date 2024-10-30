package org.example.jimmerdemo.entity;

import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

/**
 * 用户表
 *
 * @create 2024-10-30 22:32
 */
@Entity
@Table(name = "test.user") // 对应寻找唯一 table，user 这个名字可太容易重名了，所以要加上数据库名/模式名进行约束
public interface User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        // id 自增
    int id();

    String name();

    @Nullable
    Integer age();
}
