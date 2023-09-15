CREATE TABLE second_house_info
(
    id               bigint COMMENT '贝壳id',
    total_price      DECIMAL(10, 2) COMMENT '房屋总价万元',
    room_info_str    VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '3室2厅',
    room_layout      VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '南 北',
    room_area        DECIMAL(20) COMMENT '面积',
    gua_pai_date     VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '挂牌时间',
    transaction_date VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '上次交易时间',
    room_age         VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '房屋年限',
    cell_name        VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '小区名称',
    area_name        VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '区名',
    street_name      VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '街道名称',
    href             varchar(2000) COMMENT '详情信息',
    create_time      timestamp NULL default CURRENT_TIMESTAMP,
    update_time      timestamp NULL default CURRENT_TIMESTAMP

);