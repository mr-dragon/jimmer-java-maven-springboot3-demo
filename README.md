# Jimmer

- Jimmer 是一款新兴的 Java / Kotlin ORM ，以及相关的一整套的集成方案，除了数据库业务实现，还包含多视角缓存、前端对接代码生成等功能。
- Jimmer 不仅有 Mybatis 的灵活性也有 Hibernate 的复用性
- Jimmer 能够带来流畅舒适的全新开发体验，用优雅的方案解决很多看起来繁琐的问题。无论是前端要的各种凌乱散碎的实体形状还是各种复杂表单保存，Jimmer
  都能给予你合适的解决方案。

> 本人非作者，代码只是记录，文章也非本人所写。
> 感谢所有巨人的肩膀。

## 官网、文档、教程

- [jimmer 仓库](https://github.com/babyfish-ct/jimmer)
- [Jimmer, 针对Java和Kotlin的革命性ORM。不只是ORM，还是一套集成化方案](https://babyfish-ct.github.io/jimmer-doc/zh/docs/overview/introduction)
- [起凡商城 使用了 Jimmer](https://www.jarcheng.top/blog/project/qifan-mall/#java%E6%9C%8D%E5%8A%A1%E7%AB%AF)
- [国产 ORM：Jimmer 快速上手（1） ](https://blog.csdn.net/m0_61477480/article/details/134839101)
- 插件
    - [Jimmer-Generator：为Jimmer框架生成实体类接口，支持Java、Kotlin](https://plugins.jetbrains.com/plugin/20156-jimmer-generator)
    - [JimmerDTO：为Jimmer框架的DTO语言提供语法支持](https://plugins.jetbrains.com/plugin/22618-jimmerdto)

## Getting Started

- 环境
    - spring-boot 3.x -> 3.2.11
    - jdk 17+
    - jimmer: 0.8.51
    - maven
- 修改后启动 UserTest
    - 修改数据库 ip 端口、帐号密码
    - 新建数据库： test 和 3个表

### 建表 SQL

```sql
-- jimmer-demo -- start

create table user
(
    id   int auto_increment,
    name varchar(50) not null,
    age  int,
    primary key (id)
);

CREATE TABLE `order`
(
    `id`   int         NOT NULL AUTO_INCREMENT,
    `name` varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `order_detail`
(
    `id`            int         NOT NULL AUTO_INCREMENT,
    `order_id`      int         NOT NULL,
    `product`       varchar(50) NOT NULL,
    `product_count` int DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- [23000][1062] Duplicate entry 'name' for key 'idx_order_name'
CREATE UNIQUE INDEX `idx_order_name` on `order` (`name`);

-- jimmer-demo -- end

```

## 生成插件

- 2024-10-31
    - IntelliJ IDEA Ultimate 2024.2.4 无法使用 0.3.11 版本 打不开生成输入框，环境：mac m1

1. 在 Database Tools and SQL 插件中选中要生成实体类接口的表、视图，可多选
2. 在选中的任一条目上单击鼠标右键
3. 在“+New”下方找到 **Entity Classes Generator**，鼠标左键单击
4. 在弹出的对话框中选择语言(Java, Kotlin)、模块、源码根路径、实体类接口存放包
5. 在命名设置中可对表名和列名进行调整，支持正则表达式
6. 最下面的表格可选择或取消选择表、列(级联)，并可进行如下操作：

- 预览生成的属性名称
- 控制某列是不是业务主键
- 查看列的可空性

## FAQ

### 项目报错找不到 UserDraft 等类

- mvn clean package
    - 无报错后，找到生成的目录，右键目录， `把生成的这个 annotations 标注成 生成的源代码 根目录`
    - target/generated-sources/annotations
