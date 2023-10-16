CREATE TABLE second_house_info
(
    id int primary key  auto_increment,
    bei_ke_id         bigint COMMENT '贝壳id',
    total_price      DECIMAL(10, 2) COMMENT '房屋总价万元',
    bedroom_num int COMMENT '卧室数',
    live_room_num int COMMENT '客厅数',
    chao_xiang  VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '南北,朝南，朝东，朝北，朝西',
    floor_desc  VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '楼层描述：低楼层',
    total_floor_num int COMMENT '总楼层',
    room_area        DECIMAL(6,2) COMMENT '面积',
    pub_date_str     varchar(10) COMMENT '发布年份',
    cell_name        VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '小区名称',
    area_name        VARCHAR(20) COLLATE utf8mb4_unicode_ci COMMENT '区名',
    tags  VARCHAR(100) COLLATE utf8mb4_unicode_ci COMMENT '标签描述',
    create_time      timestamp NULL default CURRENT_TIMESTAMP,
    update_time      timestamp NULL default CURRENT_TIMESTAMP
);
CREATE TABLE second_house_info_bak
(
    id int primary key  auto_increment,
    beike_id         bigint COMMENT '贝壳id',
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