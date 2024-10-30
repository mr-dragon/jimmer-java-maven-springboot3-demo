package org.example.jimmerdemo.entity;

import io.micrometer.common.lang.Nullable;
import org.babyfish.jimmer.sql.*;

/**
 * 订单详情表
 *
 * @create 2024-10-30 23:37
 */
@Entity
@Table(name = "test.order_detail")
public interface OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id();

    // 多对一
    @ManyToOne
    @JoinColumn(
            name = "order_id",
            foreignKeyType = ForeignKeyType.FAKE
    )
    @Key // 父级自然是一个业务键
    @OnDissociate(DissociateAction.DELETE) // 如果脱钩了，就把自身删除
    @Nullable
    Order order();

    @IdView("order")
    Integer orderId();

    @Key
        // 自己的核心数据自然就是第二个业务键
    String product();

    @Nullable
    Integer productCount();
}
