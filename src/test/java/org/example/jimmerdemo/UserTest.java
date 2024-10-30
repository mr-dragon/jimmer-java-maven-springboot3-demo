package org.example.jimmerdemo;

import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.DeleteResult;
import org.babyfish.jimmer.sql.ast.tuple.Tuple2;
import org.example.jimmerdemo.entity.User;
import org.example.jimmerdemo.entity.UserDraft;
import org.example.jimmerdemo.entity.UserFetcher;
import org.example.jimmerdemo.entity.UserTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 1.单表基本操作
 * https://blog.csdn.net/m0_61477480/article/details/134839101
 */
@SpringBootTest
class UserTest {

    @Autowired
    JSqlClient sqlClient;

    @Test
    void insert() {
        User user = UserDraft.$.produce(draft -> {
            draft.setName("name");
            draft.setAge(11);
        });

        // 新增
        // sqlClient.insert(user)

        // 新增并拿到主键id
        int id = sqlClient.insert(user).getModifiedEntity().id();
//        SQL: insert into test.user(NAME, AGE)
//        values
//                (? /* name */, ? /* 11 */)

        // 修改，设置id
        User updateUser = UserDraft.$.produce(draft -> {
            draft.setId(id);
            draft.setName("b");
            draft.setAge(32);
        });

        sqlClient.update(updateUser);
//        SQL: update test.user
//                set
//        NAME = ? /* b */,
//        AGE = ? /* 32 */
//        where
//        ID = ? /* 2 */
//        Affected row count: 1

    }

    @Test
    void save() {
        User userWithoutId = UserDraft.$.produce(draft -> {
            draft.setName("name2");
            draft.setAge(22);
        });

        // 保存，增量更新
        User insertedUser = sqlClient.save(userWithoutId).getModifiedEntity();

        // 修改，设置id
        User updateUser = UserDraft.$.produce(draft -> {
            draft.setId(insertedUser.id());
            draft.setName("b");
            draft.setAge(32);
        });

        sqlClient.save(updateUser);
    }

    @Test
    void diffUpdate() {
        User user = UserDraft.$.produce(draft -> {
            draft.setName("a");
            draft.setAge(11);
        });

        int id = sqlClient.insert(user).getModifiedEntity().id();

        User updateUser = UserDraft.$.produce(draft -> {
            draft.setId(id);
            // 空出一些字段进行 update
            draft.setAge(null);
        });

        // update 时除了 id 这个查询条件，只设置了 age，sql 就也只更新了 age，这就是不完全更新
        sqlClient.update(updateUser);

        // set
        //    AGE = ? /* <null: Integer> */
    }

    @Test
    void query() {
        // Jimmer 的查询语法也同样直白，只是这里会再用到一个生成的辅助类型 表 DSL UserTable。
        UserTable table = UserTable.$;

        List<User> users = sqlClient.createQuery(table)
                .where(table.id().eq(1))
                .select(table)
                .execute();

        users.forEach(System.out::println);
//        SQL: select
//        tb_1_.ID,
//                tb_1_.NAME,
//                tb_1_.AGE
//        from test.user tb_1_
//        where
//        tb_1_.ID = ? /* 1 */
    }

    @Test
    void queryColumn() {
        UserTable table = UserTable.$;

        // 依靠 Tuple 类型作为承接，我们就可以获取对应的强类型列数据查询结果了
        List<Tuple2<Integer, String>> idAndNames = sqlClient.createQuery(table)
                .where(table.id().eq(1))
                // select 也支持对特定字段进行抓取
                .select(table.id(), table.name())
                .execute();

        idAndNames.forEach(System.out::println);
        // SQL: select
        //    tb_1_.ID,
        //    tb_1_.NAME
        //from test.user tb_1_
        //where
        //    tb_1_.ID = ? /* 1 */
    }

    @Test
    void fetchColumn() {
        UserTable table = UserTable.$;
        // 想要查询表中部分数据，但又希望用一整个实体接收，此时就可以用上另一个辅助类型 抓取器 UserFetcher
        UserFetcher nameFetcher = UserFetcher.$.name();

        List<User> onlyIdNameUser = sqlClient.createQuery(table)
                .where(table.id().eq(1))
                // 查询的结果就是只有 id 和 name 的 User
                .select(table.fetch(nameFetcher))
                .execute();

        onlyIdNameUser.forEach(System.out::println);
        //SQL: select
        //    tb_1_.ID,
        //    tb_1_.NAME
        //from test.user tb_1_
        //where
        //    tb_1_.ID = ? /* 1 */
        //{"id":1,"name":"name"}

        // @Nullable Integer age();
        // jimmer 需要实体以接口的形式出现，通过实现接口使字段多出一种 unload 状态表达缺失 / 未设置的含义，
        // 让属性中承载的 null 不再代表未加载的含义，而是真正完全和数据库中字段的 null 状态对应。

        // 没有 age = null 的原因：通过 Fetcher 抓取字段时，除了一定会存在的 id 和目标字段外，其他字段就都将不被查询，保持 unload 状态
    }

    @Test
    void delete() {
        UserTable table = UserTable.$;
        // 删除可以看作是查询的后置操作，因此简单修改查询的例子就可以得到删除的语句
        Integer count = sqlClient.createDelete(table)
                .where(table.id().eq(1))
                .execute();
        //SQL: delete tb_1_
        //from test.user tb_1_
        //where
        //    tb_1_.ID = ? /* 1 */
        System.out.println(count);
    }

    @Test
    void simpleDelete() {
        // 对于 id 删除自然也有简单的写法
        DeleteResult deleteResult = sqlClient.deleteById(User.class, 2);

        System.out.println(deleteResult.getTotalAffectedRowCount());
    }
}
