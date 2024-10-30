package org.example.jimmerdemo;

import org.babyfish.jimmer.sql.JSqlClient;
import org.example.jimmerdemo.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * 2.主子表基本操作
 *
 * @create 2024-10-30 23:38
 */
@SpringBootTest
public class OrderTest {

    @Autowired
    JSqlClient sqlClient;

    @Test
    void save() {
        Order baseOrder = OrderDraft.$.produce(draft -> {
            draft.setName("name");
        });

        OrderDetail mouse = OrderDetailDraft.$.produce(draft -> {
            draft.setProduct("鼠标");
            draft.setProductCount(1);
        });
        OrderDetail box = OrderDetailDraft.$.produce(draft -> {
            draft.setProduct("机箱");
            draft.setProductCount(1);
        });

        List<OrderDetail> details = new ArrayList<>();

        details.add(mouse);
        details.add(box);

        Order order = OrderDraft.$.produce(baseOrder, draft -> {
            draft.setDetails(details);
        });

        sqlClient.save(order);

        //SQL: insert into test.order(NAME)
        //values
        //    (? /* name */)
        //JDBC response status: success
        //Time cost: 9ms

        // 数据库没有数据
        //SQL: select
        //    tb_1_.ID,
        //    tb_1_.order_id,
        //    tb_1_.PRODUCT
        //from test.order_detail tb_1_
        //where
        //        tb_1_.order_id = ? /* 3 */
        //    and
        //        tb_1_.PRODUCT = ? /* 鼠标 */

        // 两个 Detail 最终都执行了 insert
        //SQL: insert into test.order_detail(order_id, PRODUCT, PRODUCT_COUNT)
        //values
        //    (? /* 3 */, ? /* 鼠标 */, ? /* 1 */)

        //SQL: select
        //    tb_1_.ID,
        //    tb_1_.order_id,
        //    tb_1_.PRODUCT
        //from test.order_detail tb_1_
        //where
        //        tb_1_.order_id = ? /* 3 */
        //    and
        //        tb_1_.PRODUCT = ? /* 机箱 */

        //SQL: insert into test.order_detail(order_id, PRODUCT, PRODUCT_COUNT)
        //values
        //    (? /* 3 */, ? /* 机箱 */, ? /* 1 */)
    }

    @Test
    void query() {
        OrderTable table = OrderTable.$;

        // 联合查询
        OrderDetailFetcher detailFetcher = OrderDetailFetcher.$
                .orderId()
                .product()
                .productCount();

        OrderFetcher OrderWithDetailsFetcher = OrderFetcher.$
                .name()
                .details(detailFetcher);

        List<Order> orders = sqlClient.createQuery(table)
                .where(table.name().like("name"))
                .select(table.fetch(OrderWithDetailsFetcher))
                .execute();

        orders.forEach(System.out::println);
        // {"id":1,"name":"name","details":[]}
        //{"id":2,"name":"name","details":[]}
        //{"id":3,"name":"name","details":[{"id":1,"orderId":3,"product":"鼠标","productCount":1},{"id":2,"orderId":3,"product":"机箱","productCount":1}]}

        OrderDetailFetcher.$
                // 全标量字段
                .allScalarFields(); // product、product_count

        OrderDetailFetcher.$
                // 全表字段
                .allTableFields(); // order、product、product_count

        OrderDetailFetcher.$
                .allTableFields()
                // 使用负属性去排除字段
                .productCount(false);
    }

    @Test
    void delete() {
        OrderTable table = OrderTable.$;

        Integer count = sqlClient.createDelete(table)
                .where(table.name().like("name"))
                .execute();

        System.out.println(count);

        // 最后的 count 统计了主子表删除的总数，因此是 2。此处级联删除的行为正是遵从于 OrderDetail 中脱钩操作的配置。
    }

    @Test
    void delete2() {
        OrderTable table = OrderTable.$;

        Integer count = sqlClient.createDelete(table)
                .where(table.name().like("name"))
                // 不需要脱钩也可以，但这样的风险会被无限放大，非常非常不推荐使用！。此处仅供参考
                .disableDissociation()
                .execute();

        System.out.println(count);
    }
}
