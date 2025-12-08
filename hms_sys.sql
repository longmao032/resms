/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80407 (8.4.7)
 Source Host           : localhost:3306
 Source Schema         : hms_sys

 Target Server Type    : MySQL
 Target Server Version : 80407 (8.4.7)
 File Encoding         : 65001

 Date: 07/12/2025 16:37:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_commission
-- ----------------------------
DROP TABLE IF EXISTS `tb_commission`;
CREATE TABLE `tb_commission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '佣金ID',
  `transaction_id` int NOT NULL COMMENT '交易ID（关联tb_transaction表）',
  `sales_id` int NOT NULL COMMENT '销售ID（关联tb_user表）',
  `commission_rate` decimal(5, 2) NOT NULL COMMENT '提成比例（%）',
  `amount` decimal(10, 2) NOT NULL COMMENT '佣金金额（元）',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0=待核算，1=已核算，2=已发放',
  `calculate_time` datetime NULL DEFAULT NULL COMMENT '核算时间',
  `issue_time` datetime NULL DEFAULT NULL COMMENT '发放时间',
  `finance_id` int NULL DEFAULT NULL COMMENT '核算财务ID（关联tb_user表）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_transaction_sales`(`transaction_id` ASC, `sales_id` ASC) USING BTREE,
  INDEX `idx_sales_id`(`sales_id` ASC) USING BTREE,
  INDEX `fk_commission_finance`(`finance_id` ASC) USING BTREE,
  CONSTRAINT `fk_commission_finance` FOREIGN KEY (`finance_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_commission_sales` FOREIGN KEY (`sales_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_commission_trans` FOREIGN KEY (`transaction_id`) REFERENCES `tb_transaction` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售佣金表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_commission
-- ----------------------------
INSERT INTO `tb_commission` VALUES (1, 1, 5, 2.50, 127500.00, 2, '2024-11-16 10:00:00', '2024-11-20 14:00:00', 7, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_commission` VALUES (2, 2, 4, 2.00, 55000.00, 2, '2024-11-21 11:00:00', '2024-11-25 15:00:00', 8, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_commission` VALUES (3, 3, 4, 2.50, 111250.00, 1, '2024-10-23 09:00:00', NULL, 7, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_commission` VALUES (4, 4, 5, 2.00, 135000.00, 1, '2024-10-31 13:00:00', NULL, 8, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_commission` VALUES (5, 5, 6, 2.50, 79500.00, 1, '2024-10-27 10:00:00', NULL, 7, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_commission` VALUES (6, 6, 4, 1.80, 151200.00, 1, '2024-11-06 14:00:00', NULL, 8, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_commission` VALUES (7, 7, 5, 2.00, 35600.00, 0, NULL, NULL, NULL, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_commission` VALUES (8, 8, 6, 2.20, 75900.00, 1, '2024-11-11 15:00:00', NULL, 7, '2025-10-30 00:31:05', '2025-10-30 00:31:05');

-- ----------------------------
-- Table structure for tb_customer
-- ----------------------------
DROP TABLE IF EXISTS `tb_customer`;
CREATE TABLE `tb_customer`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  `customer_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户编号（唯一，如\"KH20240001\"）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户电话（唯一）',
  `id_card` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号（脱敏存储）',
  `demand_area` decimal(10, 2) NULL DEFAULT NULL COMMENT '意向面积（㎡）',
  `demand_price` decimal(12, 2) NULL DEFAULT NULL COMMENT '意向价格（元）',
  `demand_layout` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '意向户型',
  `demand_area_region` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '意向区域',
  `intention_level` tinyint NULL DEFAULT 2 COMMENT '意向等级：1=高，2=中，3=低',
  `sales_id` int NULL DEFAULT NULL COMMENT '对接销售ID（关联tb_user表）',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户来源（如\"门店接待\"\"线上咨询\"）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_customer_no`(`customer_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_customer_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_sales_id`(`sales_id` ASC) USING BTREE,
  INDEX `idx_intention_level`(`intention_level` ASC) USING BTREE,
  CONSTRAINT `fk_customer_sales` FOREIGN KEY (`sales_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_customer
-- ----------------------------
INSERT INTO `tb_customer` VALUES (1, 'KH20240001', '张三', '13600000001', '110101199001011234', 90.00, 4500000.00, '3室2厅', '朝阳区', 1, 4, '门店接待', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_customer` VALUES (2, 'KH20240002', '李四', '13600000002', '110101199002022345', 120.00, 7000000.00, '4室2厅', '浦东新区', 1, 5, '线上咨询', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_customer` VALUES (3, 'KH20240003', '王五', '13600000003', '110101199003033456', 95.00, 3200000.00, '3室1厅', '天河区', 2, 6, '朋友推荐', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_customer` VALUES (4, 'KH20240004', '赵六', '13600000004', '110101199004044567', 80.00, 3000000.00, '2室1厅', '南山区', 2, 4, '门店接待', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_customer` VALUES (5, 'KH20240005', '刘七', '13600000005', '110101199005055678', 130.00, 5000000.00, '4室2厅', '西湖区', 1, 5, '线上咨询', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_customer` VALUES (6, 'KH20240006', '陈八', '13600000006', '110101199006066789', 110.00, 3500000.00, '3室2厅', '武侯区', 3, 6, '广告投放', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_customer` VALUES (7, 'KH20240007', '杨九', '13600000007', '110101199007077890', 150.00, 8000000.00, '5室2厅', '市南区', 1, 4, '朋友推荐', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_customer` VALUES (8, 'KH20240008', '黄十', '13600000008', '110101199008088901', 85.00, 2000000.00, '2室1厅', '高新区', 3, 5, '线上咨询', '2025-10-30 00:31:05', '2025-10-30 00:31:05');

-- ----------------------------
-- Table structure for tb_favorites
-- ----------------------------
DROP TABLE IF EXISTS `tb_favorites`;
CREATE TABLE `tb_favorites`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `target_type` enum('project','building','layout','unit') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收藏类型',
  `target_id` int NOT NULL COMMENT '收藏对象ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_favorite`(`user_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_favorites
-- ----------------------------

-- ----------------------------
-- Table structure for tb_house
-- ----------------------------
DROP TABLE IF EXISTS `tb_house`;
CREATE TABLE `tb_house`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '房源ID',
  `house_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房源编号（唯一，如\"FC20240001\"）',
  `area` decimal(10, 2) NOT NULL COMMENT '建筑面积（㎡）',
  `unit_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '户型名称（如\"A户型\"）',
  `layout` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '户型（如\"3室2厅\"）',
  `floor` int NOT NULL COMMENT '所在楼层',
  `total_floor` int NOT NULL COMMENT '总楼层',
  `orientation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '朝向（如\"南北通透\"）',
  `decoration` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '装修情况（如\"精装修\"）',
  `price` decimal(12, 2) NOT NULL COMMENT '挂牌价（元）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0=待审核，1=在售，2=已预订，3=已成交，4=下架',
  `sales_id` int NULL DEFAULT NULL COMMENT '负责销售ID（关联tb_user表）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '房源描述（如\"近地铁、学区房\"）',
  `house_type` tinyint NOT NULL DEFAULT 1 COMMENT '房源类型：1=二手房，2=新房，3=租房',
  `building_no` int NOT NULL COMMENT '楼栋号',
  `project_id` int NOT NULL COMMENT '所属项目',
  `room_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房号（如\"101\"）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_house_no`(`house_no` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sales_id`(`sales_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 80 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房源信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_house
-- ----------------------------
INSERT INTO `tb_house` VALUES (1, 'FC20240001', 89.50, 'A户型', '3室2厅1卫', 5, 18, '南北通透', '精装修', 4500000.00, 1, 4, '近地铁，学区房，采光好', 1, 1, 1, '501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (2, 'FC20240002', 120.00, 'B户型', '4室2厅2卫', 15, 32, '南向', '豪华装修', 6800000.00, 1, 5, '江景房，视野开阔，高端配置', 1, 2, 2, '1501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (3, 'FC20240003', 95.00, 'C户型', '3室1厅1卫', 8, 25, '东南向', '简装', 3200000.00, 1, 6, '市中心，交通便利，生活配套完善', 1, 3, 3, '803', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (4, 'FC20240004', 78.00, 'D户型', '2室1厅1卫', 3, 18, '南向', '精装修', 2800000.00, 1, 4, '小户型，总价低，适合刚需', 1, 1, 1, '302', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (5, 'FC20240005', 150.00, 'E户型', '5室2厅2卫', 25, 32, '南北通透', '豪华装修', 8500000.00, 1, 5, '顶层复式，全景江景', 1, 2, 2, '2501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (6, 'FC20240006', 110.00, 'F户型', '3室2厅1卫', 12, 25, '西南向', '精装修', 3500000.00, 1, 6, '中间楼层，采光充足', 1, 3, 3, '1202', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (7, 'FC20240007', 135.00, 'G户型', '4室2厅2卫', 10, 18, '南北通透', '精装修', 5800000.00, 1, 4, '大平层，户型方正', 1, 1, 1, '1001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (8, 'FC20240008', 85.00, 'H户型', '2室1厅1卫', 6, 32, '北向', '简装', 1800000.00, 1, 5, '投资首选，租金回报率高', 1, 2, 2, '602', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (9, 'FC20240009', 95.00, 'A新品', '3室2厅2卫', 12, 35, '南北通透', '毛坯', 8500000.00, 1, 4, '科技园核心，地铁上盖，智能家居', 2, 1, 4, '1201', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (10, 'FC20240010', 125.00, 'B新品', '4室2厅2卫', 20, 35, '南向', '毛坯', 11500000.00, 1, 4, '视野开阔，看深圳湾海景', 2, 1, 4, '2001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (11, 'FC20240011', 88.00, 'C洋房', '3室2厅1卫', 6, 8, '东南向', '精装修', 6500000.00, 1, 5, '西湖边低密度洋房，得房率高', 2, 2, 5, '601', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (12, 'FC20240012', 145.00, 'D楼王', '4室2厅2卫', 8, 8, '南北通透', '豪华装修', 9800000.00, 1, 5, '楼王位置，私家花园，稀缺资源', 2, 2, 5, '801', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (13, 'FC20240013', 180.00, 'E天际', '5室3厅3卫', 45, 60, '全景落地窗', '毛坯', 22000000.00, 1, 6, '金融城地标，360度城市景观', 2, 3, 6, '4501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (14, 'FC20240014', 75.00, 'F投资', '2室1厅1卫', 25, 60, '北向', '精装修', 6800000.00, 1, 6, '投资首选，总价低，回报稳定', 2, 3, 6, '2501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (15, 'FC20240015', 150.00, 'G复式', '4室3厅3卫', 30, 35, '南北通透', '毛坯', 14500000.00, 1, 4, '顶层复式，赠送露台，稀缺户型', 2, 1, 4, '3001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (16, 'FC20240016', 110.00, 'H标准', '3室2厅2卫', 4, 8, '南向', '精装修', 7500000.00, 1, 5, '经典三房，户型方正，性价比高', 2, 2, 5, '401', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (17, 'FC20240017', 105.00, 'I户型', '3室2厅2卫', 15, 28, '南向', '精装修', 4042500.00, 1, 4, '市中心稀缺房源，交通便利', 1, 1, 7, '1501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (18, 'FC20240018', 88.00, 'J户型', '2室2厅1卫', 8, 28, '东南向', '简装', 2508000.00, 1, 5, '小户型，总价低，适合投资', 1, 1, 7, '802', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (19, 'FC20240019', 125.00, 'K户型', '4室2厅2卫', 20, 32, '南北通透', '豪华装修', 3562500.00, 1, 6, '一线湖景，视野开阔', 1, 2, 8, '2001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (20, 'FC20240020', 95.00, 'L户型', '3室2厅1卫', 12, 32, '南向', '精装修', 2707500.00, 1, 4, '中间楼层，采光充足', 1, 2, 8, '1203', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (21, 'FC20240021', 140.00, 'M户型', '4室2厅2卫', 25, 28, '全景落地窗', '豪华装修', 5390000.00, 1, 5, '顶层视野，稀缺户型', 1, 1, 7, '2501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (22, 'FC20240022', 78.00, 'N户型', '2室1厅1卫', 5, 32, '北向', '简装', 2223000.00, 1, 6, '投资首选，租金回报高', 1, 2, 8, '501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (23, 'FC20240023', 115.00, 'O户型', '3室2厅2卫', 18, 28, '南北通透', '精装修', 4427500.00, 1, 4, '户型方正，得房率高', 1, 1, 7, '1802', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (24, 'FC20240024', 135.00, 'P户型', '4室2厅2卫', 22, 32, '南向', '豪华装修', 3847500.00, 1, 5, '大平层，舒适度高', 1, 2, 8, '2201', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (25, 'FC20240025', 92.00, 'Q户型', '3室1厅1卫', 10, 28, '东南向', '精装修', 3542000.00, 1, 6, '紧凑三房，功能齐全', 1, 1, 7, '1001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (26, 'FC20240026', 160.00, 'R户型', '5室2厅2卫', 28, 32, '南北通透', '豪华装修', 4560000.00, 1, 4, '楼王位置，私家花园', 1, 2, 8, '2801', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (27, 'FC20250001', 95.00, 'A户型', '3室2厅2卫', 8, 25, '南北通透', '毛坯', 4940000.00, 1, 4, '西湖边稀缺新房，一线湖景', 2, 1, 9, '801', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (28, 'FC20250002', 125.00, 'B户型', '4室2厅2卫', 15, 25, '南向', '毛坯', 6500000.00, 1, 5, '大平层设计，视野开阔', 2, 1, 9, '1501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (29, 'FC20250003', 150.00, 'C户型', '5室2厅3卫', 20, 25, '全景落地窗', '毛坯', 7800000.00, 1, 6, '顶层复式，赠送露台', 2, 1, 9, '2001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (30, 'FC20250004', 88.00, 'D户型', '3室2厅1卫', 5, 25, '东南向', '毛坯', 4576000.00, 1, 4, '紧凑三房，功能齐全', 2, 2, 9, '502', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (31, 'FC20250005', 110.00, 'E户型', '3室2厅2卫', 12, 32, '南北通透', '毛坯', 1980000.00, 1, 5, '山景房，空气清新', 2, 1, 10, '1201', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (32, 'FC20250006', 135.00, 'F户型', '4室2厅2卫', 18, 32, '南向', '毛坯', 2430000.00, 1, 6, '大四房，适合改善型需求', 2, 1, 10, '1801', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (33, 'FC20250007', 160.00, 'G户型', '5室2厅3卫', 25, 32, '南北通透', '毛坯', 2880000.00, 1, 4, '楼王位置，私家花园', 2, 2, 10, '2501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (34, 'FC20250008', 92.00, 'H户型', '3室1厅1卫', 8, 32, '西南向', '毛坯', 1656000.00, 1, 5, '经济实用，总价低', 2, 2, 10, '802', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (35, 'FC20250009', 180.00, 'I户型', '5室3厅3卫', 22, 25, '全景视野', '毛坯', 9360000.00, 1, 6, '豪华大平层，稀缺资源', 2, 3, 9, '2201', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (36, 'FC20250010', 75.00, 'J户型', '2室1厅1卫', 6, 32, '北向', '毛坯', 1350000.00, 1, 4, '小户型投资首选', 2, 3, 10, '601', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (37, 'FC20250011', 98.00, 'A科技户型', '3室2厅2卫', 15, 30, '南北通透', '毛坯', 8330000.00, 1, 4, '中关村核心，科技精英首选', 2, 1, 11, '1501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (38, 'FC20250012', 125.00, 'B创新户型', '4室2厅2卫', 20, 30, '南向', '毛坯', 10625000.00, 1, 5, '大平层设计，视野开阔', 2, 1, 11, '2001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (39, 'FC20250013', 88.00, 'C精英户型', '3室2厅1卫', 12, 30, '东南向', '毛坯', 7480000.00, 1, 6, '紧凑三房，功能齐全', 2, 2, 11, '1201', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (40, 'FC20250014', 135.00, 'A江景户型', '4室2厅2卫', 25, 35, '全景落地窗', '毛坯', 10125000.00, 1, 4, '一线珠江景观，稀缺资源', 2, 1, 12, '2501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (41, 'FC20250015', 110.00, 'B豪华户型', '3室2厅2卫', 18, 35, '南北通透', '毛坯', 8250000.00, 1, 5, '豪华装修标准，拎包入住', 2, 1, 12, '1801', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (42, 'FC20250016', 160.00, 'C楼王户型', '5室2厅3卫', 30, 35, '270度景观', '毛坯', 12000000.00, 1, 6, '楼王位置，私家空中花园', 2, 2, 12, '3001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (43, 'FC20250017', 105.00, 'A东盟户型', '3室2厅2卫', 12, 25, '南北通透', '毛坯', 1575000.00, 1, 4, '东盟商务区核心，投资首选', 2, 1, 13, '1201', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (44, 'FC20250018', 95.00, 'B商务户型', '3室2厅1卫', 8, 25, '南向', '毛坯', 1425000.00, 1, 5, '商务精英首选，交通便利', 2, 1, 13, '801', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (45, 'FC20250019', 120.00, 'C豪华户型', '4室2厅2卫', 15, 25, '全景视野', '毛坯', 1800000.00, 1, 6, '大四房，改善型需求', 2, 2, 13, '1501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (46, 'FC20250020', 150.00, 'A金融户型', '4室2厅2卫', 35, 50, '全景江景', '毛坯', 18000000.00, 1, 4, '陆家嘴地标，金融精英聚集', 2, 1, 14, '3501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (47, 'FC20250021', 180.00, 'B总裁户型', '5室2厅3卫', 40, 50, '360度环幕', '毛坯', 21600000.00, 1, 5, '顶层复式，私人会所', 2, 1, 14, '4001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (48, 'FC20250022', 125.00, 'C精英户型', '3室2厅2卫', 25, 50, '南向江景', '毛坯', 15000000.00, 1, 6, '精致三房，性价比高', 2, 2, 14, '2501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (49, 'FC20250023', 95.00, 'A凤岭户型', '3室2厅2卫', 12, 28, '南北通透', '毛坯', 1710000.00, 1, 4, '凤岭北高端学区房，教育资源优质', 2, 1, 15, '1201', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (50, 'FC20250024', 128.00, 'B豪华户型', '4室2厅2卫', 18, 28, '南向观景', '毛坯', 2304000.00, 1, 5, '大平层设计，视野开阔', 2, 1, 15, '1801', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (51, 'FC20250025', 88.00, 'A朝阳户型', '3室2厅1卫', 8, 25, '东南向', '毛坯', 1056000.00, 1, 6, '市中心稀缺房源，商业配套完善', 2, 1, 16, '801', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (52, 'FC20250026', 110.00, 'B中心户型', '3室2厅2卫', 15, 25, '南北通透', '毛坯', 1320000.00, 1, 4, '黄金地段，投资自住两相宜', 2, 2, 16, '1501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (53, 'FC20250027', 105.00, 'A学府户型', '3室2厅2卫', 10, 22, '南向', '毛坯', 997500.00, 1, 5, '大学城周边，学术氛围浓厚', 2, 1, 17, '1001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (54, 'FC20250028', 135.00, 'B书香户型', '4室2厅2卫', 16, 22, '南北通透', '毛坯', 1282500.00, 1, 6, '改善型四房，适合教师家庭', 2, 1, 17, '1601', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (55, 'FC20250029', 92.00, 'A江南户型', '3室2厅1卫', 6, 20, '东南向', '毛坯', 782000.00, 1, 4, '江南区核心，生活便利', 2, 1, 18, '601', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (56, 'FC20250030', 118.00, 'B府邸户型', '4室2厅2卫', 12, 20, '南向', '毛坯', 1003000.00, 1, 5, '大四房，舒适度高', 2, 2, 18, '1201', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (57, 'FC20250031', 145.00, 'A五象户型', '4室2厅2卫', 22, 35, '全景落地窗', '毛坯', 2175000.00, 1, 6, '五象新区CBD，投资潜力大', 2, 1, 19, '2201', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (58, 'FC20250032', 180.00, 'B国际户型', '5室2厅3卫', 28, 35, '270度景观', '毛坯', 2700000.00, 1, 4, '楼王位置，稀缺资源', 2, 1, 19, '2801', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (59, 'FC20250033', 125.00, 'C精英户型', '3室2厅2卫', 18, 35, '南向江景', '毛坯', 1875000.00, 1, 5, '江景房，性价比高', 2, 2, 19, '1801', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (60, 'FC20250034', 98.00, 'A龙岗户型', '3室2厅2卫', 10, 26, '南北通透', '毛坯', 784000.00, 1, 6, '龙岗新区生态住宅', 2, 1, 20, '1001', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (61, 'FC20250035', 115.00, 'B江景户型', '4室2厅2卫', 15, 26, '江景视野', '毛坯', 920000.00, 1, 4, '一线江景，居住舒适', 2, 1, 20, '1501', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (62, 'FC20250036', 85.00, 'A学府户型', '2室2厅1卫', 8, 18, '南向', '毛坯', 552500.00, 1, 5, '教育园区配套，适合投资', 2, 1, 21, '801', '2025-10-30 00:31:05', '2025-11-30 16:29:52');
INSERT INTO `tb_house` VALUES (63, 'FC20250037', 105.00, 'B书香户型', '3室2厅2卫', 12, 18, '南北通透', '毛坯', 682500.00, 1, 6, '教师家庭首选，环境安静', 2, 2, 21, '1201', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house` VALUES (64, 'FC20250038', 98.50, '天河精品', '3室2厅2卫', 15, 32, '南向', '毛坯', 7850000.00, 1, 4, '天河CBD核心地段，地铁上盖，高端配置', 2, 1, 22, '1502', '2025-11-28 21:53:23', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (65, 'FC20250039', 118.00, '越秀雅居', '4室2厅2卫', 12, 28, '南北通透', '毛坯', 8850000.00, 1, 5, '越秀老城区，学区房优势，配套成熟', 2, 1, 23, '1201', '2025-11-28 21:53:23', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (66, 'FC20250040', 135.00, '海珠江景', '4室2厅2卫', 20, 35, '江景视野', '毛坯', 9800000.00, 1, 6, '一线江景房，海珠区稀缺资源', 2, 1, 24, '2003', '2025-11-28 21:53:23', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (67, 'FC20250041', 88.00, '荔湾经典', '3室2厅1卫', 8, 25, '东南向', '毛坯', 6500000.00, 1, 4, '荔湾老城区，文化底蕴深厚', 2, 1, 25, '802', '2025-11-28 21:53:23', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (68, 'FC20250042', 155.00, '白云山景', '5室2厅3卫', 18, 30, '山景视野', '毛坯', 8500000.00, 1, 5, '白云山脚下，生态宜居，空气清新', 2, 1, 26, '1801', '2025-11-28 21:53:23', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (70, 'FC20250043', 125.00, '天河豪华', '4室2厅2卫', 25, 32, '南向', '毛坯', 10500000.00, 1, 5, '天河国际中心楼王位置，全景城市景观', 2, 1, 22, '2501', '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (71, 'FC20250044', 88.00, '天河精英', '3室2厅1卫', 12, 32, '东南向', '毛坯', 6800000.00, 1, 6, '紧凑三房，总价相对较低，投资自住两宜', 2, 2, 22, '1203', '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (72, 'FC20250045', 105.00, '越秀经典', '3室2厅2卫', 8, 28, '南北通透', '毛坯', 7800000.00, 1, 4, '越秀文化府经典户型，得房率高', 2, 1, 23, '802', '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (73, 'FC20250046', 140.00, '越秀尊享', '4室2厅2卫', 15, 28, '南向', '毛坯', 9800000.00, 1, 5, '大四房设计，改善型需求首选', 2, 2, 23, '1502', '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (74, 'FC20250047', 95.00, '海珠精品', '3室2厅2卫', 10, 35, '江景视野', '毛坯', 6800000.00, 1, 6, '精致江景三房，性价比高', 2, 1, 24, '1002', '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (75, 'FC20250048', 165.00, '海珠天际', '5室2厅3卫', 30, 35, '270度江景', '毛坯', 12800000.00, 1, 4, '顶层复式，私家空中花园', 2, 2, 24, '3001', '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (76, 'FC20250049', 78.00, '荔湾雅居', '2室2厅1卫', 6, 25, '南向', '毛坯', 5000000.00, 1, 5, '小户型精品，适合年轻家庭', 2, 1, 25, '602', '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (77, 'FC20250050', 115.00, '荔湾世家', '4室2厅2卫', 12, 25, '南北通透', '毛坯', 7400000.00, 1, 6, '西关大院稀缺四房，传世之选', 2, 2, 25, '1202', '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (78, 'FC20250051', 135.00, '白云观山', '4室2厅2卫', 18, 30, '山景视野', '毛坯', 7400000.00, 1, 4, '白云山语国际观山楼栋，视野开阔', 2, 1, 26, '1802', '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_house` VALUES (79, 'FC20250052', 95.00, '白云清新', '3室2厅1卫', 8, 30, '东南向', '毛坯', 5200000.00, 1, 5, '经济实用三房，生态宜居', 2, 2, 26, '803', '2025-11-28 21:57:11', '2025-11-28 21:57:11');

-- ----------------------------
-- Table structure for tb_house_image
-- ----------------------------
DROP TABLE IF EXISTS `tb_house_image`;
CREATE TABLE `tb_house_image`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `house_id` int NOT NULL COMMENT '关联房源ID（对应tb_house表的id）',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL/本地路径（如\"FC2024001/cover.jpg\"或在线链接）',
  `image_type` tinyint NOT NULL COMMENT '图片类型：1=封面图，2=室内图，3=户型图，4=环境图',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序序号（数字越小越靠前，用于前端展示顺序）',
  `upload_user_id` int NOT NULL COMMENT '上传人ID（关联tb_user表，记录操作人）',
  `audit_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态：0=待审核，1=已通过，2=已驳回',
  `audit_user_id` int NULL DEFAULT NULL COMMENT '审核人ID（关联tb_user表，可为空）',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_house_id`(`house_id` ASC) USING BTREE COMMENT '按房源ID查询所有图片',
  INDEX `idx_image_type`(`image_type` ASC) USING BTREE COMMENT '按图片类型筛选',
  INDEX `fk_image_upload_user`(`upload_user_id` ASC) USING BTREE,
  INDEX `fk_image_audit_user`(`audit_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_image_audit_user` FOREIGN KEY (`audit_user_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_image_house` FOREIGN KEY (`house_id`) REFERENCES `tb_house` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_image_upload_user` FOREIGN KEY (`upload_user_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 139 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房源图片表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_house_image
-- ----------------------------
INSERT INTO `tb_house_image` VALUES (1, 1, 'FC20240001/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (2, 1, 'FC20240001/living_room1.jpg', 2, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (3, 1, 'FC20240001/bedroom1.jpg', 2, 3, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (4, 1, 'FC20240001/layout.jpg', 3, 4, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (5, 2, 'FC20240002/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (6, 2, 'FC20240002/river_view.jpg', 4, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (7, 2, 'FC20240002/layout.jpg', 3, 3, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (8, 3, 'FC20240003/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (9, 3, 'FC20240003/layout.jpg', 3, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (10, 4, 'FC20240004/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (11, 9, 'FC20240009/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (12, 9, 'FC20240009/sample_room1.jpg', 2, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (13, 9, 'FC20240009/layout.jpg', 3, 3, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (14, 9, 'FC20240009/surrounding1.jpg', 4, 4, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (15, 10, 'FC20240010/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (16, 10, 'FC20240010/view1.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (17, 11, 'FC20240011/cover.jpg', 1, 1, 5, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (18, 11, 'FC20240011/garden.jpg', 4, 2, 5, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (19, 12, 'FC20240012/cover.jpg', 1, 1, 5, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (20, 13, 'FC20240013/cover.jpg', 1, 1, 6, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (21, 13, 'FC20240013/city_view.jpg', 4, 2, 6, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (22, 17, 'FC20240017/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (23, 17, 'FC20240017/living_room1.jpg', 2, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (24, 17, 'FC20240017/layout.jpg', 3, 3, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (25, 18, 'FC20240018/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (26, 18, 'FC20240018/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (27, 19, 'FC20240019/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (28, 19, 'FC20240019/lake_view.jpg', 4, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (29, 19, 'FC20240019/layout.jpg', 3, 3, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (30, 20, 'FC20240020/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (31, 20, 'FC20240020/layout.jpg', 3, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (32, 21, 'FC20240021/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (33, 21, 'FC20240021/view1.jpg', 4, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (34, 22, 'FC20240022/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (35, 22, 'FC20240022/layout.jpg', 3, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (36, 23, 'FC20240023/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (37, 23, 'FC20240023/layout.jpg', 3, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (38, 24, 'FC20240024/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (39, 24, 'FC20240024/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (40, 25, 'FC20240025/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (41, 25, 'FC20240025/layout.jpg', 3, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (42, 26, 'FC20240026/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (43, 26, 'FC20240026/garden.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (44, 26, 'FC20240026/layout.jpg', 3, 3, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (45, 27, 'FC20250001/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (46, 27, 'FC20250001/lake_view.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (47, 27, 'FC20250001/layout.jpg', 3, 3, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (48, 28, 'FC20250002/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (49, 28, 'FC20250002/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (50, 29, 'FC20250003/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (51, 29, 'FC20250003/terrace.jpg', 4, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (52, 30, 'FC20250004/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (53, 30, 'FC20250004/layout.jpg', 3, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (54, 31, 'FC20250005/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (55, 31, 'FC20250005/mountain_view.jpg', 4, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (56, 32, 'FC20250006/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (57, 32, 'FC20250006/layout.jpg', 3, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (58, 33, 'FC20250007/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (59, 33, 'FC20250007/garden.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (60, 34, 'FC20250008/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (61, 34, 'FC20250008/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (62, 35, 'FC20250009/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (63, 35, 'FC20250009/city_view.jpg', 4, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (64, 36, 'FC20250010/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (65, 36, 'FC20250010/layout.jpg', 3, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (66, 37, 'FC20250011/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (67, 37, 'FC20250011/tech_view.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (68, 37, 'FC20250011/layout.jpg', 3, 3, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (69, 38, 'FC20250012/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (70, 38, 'FC20250012/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (71, 39, 'FC20250013/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (72, 39, 'FC20250013/layout.jpg', 3, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (73, 40, 'FC20250014/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (74, 40, 'FC20250014/river_view.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (75, 40, 'FC20250014/layout.jpg', 3, 3, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (76, 41, 'FC20250015/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (77, 41, 'FC20250015/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (78, 42, 'FC20250016/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (79, 42, 'FC20250016/garden_view.jpg', 4, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (80, 43, 'FC20250017/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (81, 43, 'FC20250017/asean_view.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (82, 43, 'FC20250017/layout.jpg', 3, 3, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (83, 44, 'FC20250018/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (84, 44, 'FC20250018/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (85, 45, 'FC20250019/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (86, 45, 'FC20250019/city_view.jpg', 4, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (87, 46, 'FC20250020/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (88, 46, 'FC20250020/financial_view.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (89, 46, 'FC20250020/layout.jpg', 3, 3, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (90, 47, 'FC20250021/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (91, 47, 'FC20250021/penthouse_view.jpg', 4, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (92, 48, 'FC20250022/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (93, 48, 'FC20250022/river_view.jpg', 4, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (94, 48, 'FC20250022/layout.jpg', 3, 3, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (95, 49, 'FC20250023/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (96, 49, 'FC20250023/fengling_view.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (97, 49, 'FC20250023/layout.jpg', 3, 3, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (98, 50, 'FC20250024/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (99, 50, 'FC20250024/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (100, 51, 'FC20250025/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (101, 51, 'FC20250025/chaoyang_view.jpg', 4, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (102, 52, 'FC20250026/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (103, 52, 'FC20250026/layout.jpg', 3, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (104, 53, 'FC20250027/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (105, 53, 'FC20250027/university_view.jpg', 4, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (106, 54, 'FC20250028/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (107, 54, 'FC20250028/layout.jpg', 3, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (108, 55, 'FC20250029/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (109, 55, 'FC20250029/river_view.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (110, 56, 'FC20250030/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (111, 56, 'FC20250030/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (112, 57, 'FC20250031/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (113, 57, 'FC20250031/wuxiang_view.jpg', 4, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (114, 58, 'FC20250032/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (115, 58, 'FC20250032/city_view.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (116, 59, 'FC20250033/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (117, 59, 'FC20250033/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (118, 60, 'FC20250034/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (119, 60, 'FC20250034/longgang_view.jpg', 4, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (120, 61, 'FC20250035/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (121, 61, 'FC20250035/river_scene.jpg', 4, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (122, 62, 'FC20250036/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (123, 62, 'FC20250036/education_view.jpg', 4, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (124, 63, 'FC20250037/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (125, 63, 'FC20250037/layout.jpg', 3, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_house_image` VALUES (126, 64, 'FC20250038/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-11-28 21:53:23', '2025-11-28 21:53:23');
INSERT INTO `tb_house_image` VALUES (127, 64, 'FC20250038/layout.jpg', 3, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-11-28 21:53:23', '2025-11-28 21:53:23');
INSERT INTO `tb_house_image` VALUES (128, 65, 'FC20250039/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-11-28 21:53:23', '2025-11-28 21:53:23');
INSERT INTO `tb_house_image` VALUES (129, 65, 'FC20250039/layout.jpg', 3, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-11-28 21:53:23', '2025-11-28 21:53:23');
INSERT INTO `tb_house_image` VALUES (130, 66, 'FC20250040/cover.jpg', 1, 1, 6, 1, 3, '2025-10-30 09:00:00', '2025-11-28 21:53:23', '2025-11-28 21:53:23');
INSERT INTO `tb_house_image` VALUES (131, 66, 'FC20250040/river_view.jpg', 4, 2, 6, 1, 3, '2025-10-30 09:00:00', '2025-11-28 21:53:23', '2025-11-28 21:53:23');
INSERT INTO `tb_house_image` VALUES (132, 67, 'FC20250041/cover.jpg', 1, 1, 4, 1, 2, '2025-10-30 09:00:00', '2025-11-28 21:53:23', '2025-11-28 21:53:23');
INSERT INTO `tb_house_image` VALUES (133, 67, 'FC20250041/layout.jpg', 3, 2, 4, 1, 2, '2025-10-30 09:00:00', '2025-11-28 21:53:23', '2025-11-28 21:53:23');
INSERT INTO `tb_house_image` VALUES (134, 68, 'FC20250042/cover.jpg', 1, 1, 5, 1, 2, '2025-10-30 09:00:00', '2025-11-28 21:53:23', '2025-11-28 21:53:23');
INSERT INTO `tb_house_image` VALUES (135, 68, 'FC20250042/mountain_view.jpg', 4, 2, 5, 1, 2, '2025-10-30 09:00:00', '2025-11-28 21:53:23', '2025-11-28 21:53:23');

-- ----------------------------
-- Table structure for tb_new_house_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_new_house_info`;
CREATE TABLE `tb_new_house_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `house_id` int NOT NULL COMMENT '房源ID',
  `pre_sale_license_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预售许可证号',
  `record_price` decimal(12, 2) NULL DEFAULT NULL COMMENT '备案价',
  `property_right_years` int NULL DEFAULT 70 COMMENT '产权年限',
  `estimated_delivery_time` date NULL DEFAULT NULL COMMENT '预计交房时间',
  `delivery_standard` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '毛坯' COMMENT '交付标准',
  `elevator_ratio` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '梯户比',
  `actual_area_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '得房率(%)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_house_id`(`house_id` ASC) USING BTREE,
  CONSTRAINT `fk_new_house_info_house` FOREIGN KEY (`house_id`) REFERENCES `tb_house` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '新房扩展信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_new_house_info
-- ----------------------------
INSERT INTO `tb_new_house_info` VALUES (1, 9, 'YSXK202400001', 8800000.00, 70, '2026-12-31', '毛坯', '2梯4户', 78.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (2, 10, 'YSXK202400001', 12000000.00, 70, '2026-12-31', '毛坯', '2梯3户', 80.20, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (3, 11, 'YSXK202400002', 6800000.00, 70, '2026-06-30', '精装修', '1梯2户', 85.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (4, 12, 'YSXK202400002', 10500000.00, 70, '2026-06-30', '豪华装修', '1梯1户', 88.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (5, 13, 'YSXK202400003', 23000000.00, 70, '2027-03-31', '毛坯', '3梯2户', 76.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (6, 14, 'YSXK202400003', 7200000.00, 70, '2027-03-31', '精装修', '2梯6户', 75.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (7, 15, 'YSXK202400001', 15500000.00, 70, '2026-12-31', '毛坯', '2梯2户', 82.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (8, 16, 'YSXK202400002', 8000000.00, 70, '2026-06-30', '精装修', '1梯2户', 84.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (9, 27, 'YSXK202400004', 5200000.00, 70, '2026-12-31', '毛坯', '2梯3户', 79.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (10, 28, 'YSXK202400004', 6800000.00, 70, '2026-12-31', '毛坯', '2梯2户', 81.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (11, 29, 'YSXK202400004', 8200000.00, 70, '2026-12-31', '毛坯', '1梯1户', 83.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (12, 30, 'YSXK202400004', 4800000.00, 70, '2026-12-31', '毛坯', '2梯4户', 77.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (13, 31, 'YSXK202400005', 2100000.00, 70, '2027-03-31', '毛坯', '2梯3户', 78.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (14, 32, 'YSXK202400005', 2600000.00, 70, '2027-03-31', '毛坯', '2梯2户', 80.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (15, 33, 'YSXK202400005', 3200000.00, 70, '2027-03-31', '毛坯', '1梯1户', 82.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (16, 34, 'YSXK202400005', 1800000.00, 70, '2027-03-31', '毛坯', '2梯4户', 76.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (17, 35, 'YSXK202400004', 9800000.00, 70, '2026-12-31', '毛坯', '2梯2户', 84.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (18, 37, 'YSXK202400006', 8800000.00, 70, '2026-12-31', '毛坯', '2梯3户', 78.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (19, 38, 'YSXK202400006', 11500000.00, 70, '2026-12-31', '毛坯', '2梯2户', 81.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (20, 39, 'YSXK202400006', 8000000.00, 70, '2026-12-31', '毛坯', '2梯4户', 77.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (21, 40, 'YSXK202400007', 11000000.00, 70, '2027-03-31', '毛坯', '2梯2户', 79.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (22, 41, 'YSXK202400007', 9000000.00, 70, '2027-03-31', '精装修', '2梯3户', 82.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (23, 42, 'YSXK202400007', 13000000.00, 70, '2027-03-31', '毛坯', '1梯1户', 85.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (24, 43, 'YSXK202400008', 1700000.00, 70, '2026-10-31', '毛坯', '2梯4户', 76.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (25, 44, 'YSXK202400008', 1550000.00, 70, '2026-10-31', '毛坯', '2梯3户', 78.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (26, 45, 'YSXK202400008', 2000000.00, 70, '2026-10-31', '毛坯', '2梯2户', 80.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (27, 46, 'YSXK202400009', 19000000.00, 70, '2027-06-30', '毛坯', '3梯2户', 77.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (28, 47, 'YSXK202400009', 23000000.00, 70, '2027-06-30', '毛坯', '2梯1户', 83.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (29, 48, 'YSXK202400009', 16500000.00, 70, '2027-06-30', '毛坯', '2梯2户', 79.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (30, 49, 'YSXK202400010', 1850000.00, 70, '2026-08-31', '毛坯', '2梯3户', 78.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (31, 50, 'YSXK202400010', 2500000.00, 70, '2026-08-31', '毛坯', '2梯2户', 81.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (32, 51, 'YSXK202400011', 1150000.00, 70, '2026-09-30', '毛坯', '2梯4户', 76.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (33, 52, 'YSXK202400011', 1450000.00, 70, '2026-09-30', '毛坯', '2梯3户', 79.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (34, 53, 'YSXK202400012', 1100000.00, 70, '2026-10-31', '毛坯', '2梯4户', 75.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (35, 54, 'YSXK202400012', 1400000.00, 70, '2026-10-31', '毛坯', '2梯2户', 78.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (36, 55, 'YSXK202400013', 850000.00, 70, '2026-11-30', '毛坯', '2梯4户', 74.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (37, 56, 'YSXK202400013', 1150000.00, 70, '2026-11-30', '毛坯', '2梯3户', 77.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (38, 57, 'YSXK202400014', 2400000.00, 70, '2027-06-30', '毛坯', '2梯2户', 79.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (39, 58, 'YSXK202400014', 3000000.00, 70, '2027-06-30', '毛坯', '1梯1户', 83.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (40, 59, 'YSXK202400014', 2100000.00, 70, '2027-06-30', '毛坯', '2梯3户', 78.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (41, 60, 'YSXK202400015', 850000.00, 70, '2027-01-31', '毛坯', '2梯4户', 75.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (42, 61, 'YSXK202400015', 1000000.00, 70, '2027-01-31', '毛坯', '2梯2户', 78.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (43, 62, 'YSXK202400016', 600000.00, 70, '2027-02-28', '毛坯', '2梯3户', 76.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (44, 63, 'YSXK202400016', 750000.00, 70, '2027-02-28', '毛坯', '2梯2户', 79.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (45, 64, 'YSXK202400017', 8200000.00, 70, '2027-06-30', '毛坯', '2梯3户', 78.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (46, 65, 'YSXK202400018', 9200000.00, 70, '2027-03-31', '毛坯', '2梯2户', 81.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (47, 66, 'YSXK202400019', 10500000.00, 70, '2027-08-31', '毛坯', '2梯2户', 79.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (48, 67, 'YSXK202400020', 6800000.00, 70, '2027-09-30', '毛坯', '2梯4户', 76.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (49, 68, 'YSXK202400021', 9000000.00, 70, '2027-05-31', '毛坯', '2梯2户', 80.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (50, 70, 'YSXK202400017', 11000000.00, 70, '2027-06-30', '毛坯', '1梯1户', 83.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (51, 71, 'YSXK202400017', 7100000.00, 70, '2027-06-30', '毛坯', '2梯4户', 77.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (52, 72, 'YSXK202400018', 8200000.00, 70, '2027-03-31', '毛坯', '2梯3户', 79.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (53, 73, 'YSXK202400018', 10500000.00, 70, '2027-03-31', '毛坯', '2梯2户', 82.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (54, 74, 'YSXK202400019', 7200000.00, 70, '2027-08-31', '毛坯', '2梯3户', 78.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (55, 75, 'YSXK202400019', 13500000.00, 70, '2027-08-31', '毛坯', '1梯1户', 85.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (56, 76, 'YSXK202400020', 5300000.00, 70, '2027-09-30', '毛坯', '2梯4户', 75.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (57, 77, 'YSXK202400020', 7800000.00, 70, '2027-09-30', '毛坯', '2梯2户', 79.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (58, 78, 'YSXK202400021', 7800000.00, 70, '2027-05-31', '毛坯', '2梯2户', 80.00, '2025-12-01 20:14:54', '2025-12-01 20:14:54');
INSERT INTO `tb_new_house_info` VALUES (59, 79, 'YSXK202400021', 5500000.00, 70, '2027-05-31', '毛坯', '2梯4户', 76.50, '2025-12-01 20:14:54', '2025-12-01 20:14:54');

-- ----------------------------
-- Table structure for tb_notice_read
-- ----------------------------
DROP TABLE IF EXISTS `tb_notice_read`;
CREATE TABLE `tb_notice_read`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '阅读记录ID',
  `notice_id` int NOT NULL COMMENT '通知ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `read_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_notice_user`(`notice_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_notice_read_notice` FOREIGN KEY (`notice_id`) REFERENCES `tb_work_notice` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_notice_read_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知阅读记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_notice_read
-- ----------------------------
INSERT INTO `tb_notice_read` VALUES (1, 1, 4, '2025-10-30 09:30:00');
INSERT INTO `tb_notice_read` VALUES (2, 1, 5, '2025-10-30 10:00:00');
INSERT INTO `tb_notice_read` VALUES (3, 1, 6, '2025-10-30 11:00:00');
INSERT INTO `tb_notice_read` VALUES (4, 2, 2, '2025-10-30 10:35:00');
INSERT INTO `tb_notice_read` VALUES (5, 3, 4, '2025-10-30 14:30:00');
INSERT INTO `tb_notice_read` VALUES (6, 4, 1, '2025-10-30 16:10:00');
INSERT INTO `tb_notice_read` VALUES (7, 4, 2, '2025-10-30 16:15:00');

-- ----------------------------
-- Table structure for tb_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_operation_log`;
CREATE TABLE `tb_operation_log`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作模块',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `operation_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作描述',
  `user_id` int NOT NULL COMMENT '操作人ID',
  `user_real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人姓名',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求URL',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `response_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '响应结果',
  `status` tinyint NOT NULL COMMENT '操作状态：0=失败，1=成功',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `execute_time` bigint NULL DEFAULT NULL COMMENT '执行时间（毫秒）',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE,
  INDEX `idx_operation_time`(`operation_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_operation_log
-- ----------------------------
INSERT INTO `tb_operation_log` VALUES (1, '用户管理', '新增用户', '新增销售顾问：王销售', 1, '系统管理员', '192.168.1.100', '/api/user', 'POST', '{\"username\":\"sales1\",\"realName\":\"王销售\"}', '{\"code\":200,\"message\":\"success\"}', 1, NULL, 120, '2025-10-30 00:31:05');
INSERT INTO `tb_operation_log` VALUES (2, '房源管理', '审核房源', '审核房源FC20240001：通过', 2, '张经理', '192.168.1.101', '/api/house/audit', 'POST', '{\"houseId\":1,\"status\":1}', '{\"code\":200,\"message\":\"success\"}', 1, NULL, 85, '2025-10-30 00:31:05');
INSERT INTO `tb_operation_log` VALUES (3, '交易管理', '创建交易', '为客户张三创建交易JY20240001', 4, '王销售', '192.168.1.102', '/api/transaction', 'POST', '{\"customerId\":1,\"houseId\":1}', '{\"code\":200,\"message\":\"success\"}', 1, NULL, 200, '2025-10-30 00:31:05');
INSERT INTO `tb_operation_log` VALUES (4, '佣金管理', '核算佣金', '核算交易JY20240001的佣金', 7, '陈财务', '192.168.1.103', '/api/commission/calculate', 'POST', '{\"transactionId\":1}', '{\"code\":200,\"message\":\"success\"}', 1, NULL, 150, '2025-10-30 00:31:05');
INSERT INTO `tb_operation_log` VALUES (5, '用户管理', '修改角色', '修改用户 [sales1] 的角色为 销售经理', 1, '系统管理员', NULL, '/api/user/change-role', 'POST', '{\"userId\":4,\"newRoleId\":2}', NULL, 1, NULL, NULL, '2025-12-06 01:26:14');
INSERT INTO `tb_operation_log` VALUES (6, '用户管理', '修改角色', '修改用户 [sales1] 的角色为 销售顾问', 1, '系统管理员', NULL, '/api/user/change-role', 'POST', '{\"userId\":4,\"newRoleId\":3}', NULL, 1, NULL, NULL, '2025-12-06 01:26:14');

-- ----------------------------
-- Table structure for tb_payment
-- ----------------------------
DROP TABLE IF EXISTS `tb_payment`;
CREATE TABLE `tb_payment`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '收款ID',
  `transaction_id` int NOT NULL COMMENT '交易ID（关联tb_transaction表）',
  `payment_type` tinyint NOT NULL COMMENT '款项类型：1=定金，2=首付款，3=尾款，4=中介费',
  `amount` decimal(12, 2) NOT NULL COMMENT '收款金额（元）',
  `payment_time` datetime NOT NULL COMMENT '收款时间',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付方式（如\"微信\"\"银行卡\"）',
  `receipt_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收据/发票编号',
  `finance_id` int NOT NULL COMMENT '财务人员ID（关联tb_user表）',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注（如\"客户刷卡支付\"）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_transaction_id`(`transaction_id` ASC) USING BTREE,
  INDEX `idx_finance_id`(`finance_id` ASC) USING BTREE,
  CONSTRAINT `fk_payment_finance` FOREIGN KEY (`finance_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_payment_trans` FOREIGN KEY (`transaction_id`) REFERENCES `tb_transaction` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收款记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_payment
-- ----------------------------
INSERT INTO `tb_payment` VALUES (1, 1, 1, 100000.00, '2024-10-18 10:00:00', '银行卡', 'SK20240001', 7, '定金支付', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_payment` VALUES (2, 1, 2, 2000000.00, '2024-10-25 14:00:00', '银行转账', 'SK20240002', 7, '首付款支付', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_payment` VALUES (3, 2, 1, 50000.00, '2024-10-20 11:00:00', '微信', 'SK20240003', 8, '定金支付', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_payment` VALUES (4, 2, 2, 1000000.00, '2024-10-27 15:00:00', '银行卡', 'SK20240004', 8, '首付款支付', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_payment` VALUES (5, 3, 1, 80000.00, '2024-10-22 09:30:00', '支付宝', 'SK20240005', 7, '定金支付', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_payment` VALUES (6, 4, 1, 150000.00, '2024-10-24 13:00:00', '银行卡', 'SK20240006', 8, '定金支付', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_payment` VALUES (7, 4, 2, 2500000.00, '2024-10-30 16:00:00', '银行转账', 'SK20240007', 7, '首付款支付', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_payment` VALUES (8, 5, 1, 60000.00, '2024-10-26 10:30:00', '微信', 'SK20240008', 8, '定金支付', '2025-10-30 00:31:05', '2025-10-30 00:31:05');

-- ----------------------------
-- Table structure for tb_permission
-- ----------------------------
DROP TABLE IF EXISTS `tb_permission`;
CREATE TABLE `tb_permission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限代码（唯一）',
  `permission_type` tinyint NOT NULL COMMENT '权限类型：1=菜单权限，2=按钮权限，3=数据权限',
  `parent_id` int NULL DEFAULT 0 COMMENT '父权限ID（0表示顶级权限）',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序序号',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_permission_code`(`permission_code` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 169 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_permission
-- ----------------------------
INSERT INTO `tb_permission` VALUES (1, '系统管理', 'system:manage', 1, 0, '/system', 'Layout', 'setting', 100, '系统管理菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (2, '用户管理', 'user:manage', 1, 1, '/system/user', 'system/user/index', 'user', 101, '用户管理菜单', 1, '2025-12-06 22:50:16', '2025-12-07 00:26:07');
INSERT INTO `tb_permission` VALUES (3, '角色管理', 'role:manage', 1, 1, '/system/role', 'system/role/index', 'team', 102, '角色管理菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (4, '菜单管理', 'menu:manage', 1, 1, '/system/menu', 'system/menu/index', 'menu', 103, '菜单管理菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (5, '操作日志', 'operation:log:manage', 1, 1, '/system/log', 'system/log/index', 'file-text', 104, '操作日志查看', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (6, '房源管理', 'house:manage', 1, 0, '/house', 'Layout', 'home', 200, '房源管理菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (7, '房源列表', 'house:list:manage', 1, 6, '/house/list', 'house/list/index', 'table', 201, '房源列表管理', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (8, '新增房源', 'house:add:page', 1, 6, '/house/add', 'house/add/index', 'plus', 202, '新增房源页面', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (9, '房源审核', 'house:audit:manage', 1, 6, '/house/audit', 'house/audit/index', 'audit', 203, '房源审核菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (10, '房源统计', 'house:statistics:manage', 1, 6, '/house/statistics', 'house/statistics/index', 'bar-chart', 204, '房源统计菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (11, '客户管理', 'customer:manage', 1, 0, '/customer', 'Layout', 'customer-service', 300, '客户管理菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (12, '客户列表', 'customer:list:manage', 1, 11, '/customer/list', 'customer/list/index', 'form', 301, '客户列表菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (13, '客户跟进', 'customer:follow:manage', 1, 11, '/customer/follow', 'customer/follow/index', 'interaction', 302, '客户跟进菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (14, '客户统计', 'customer:statistics:manage', 1, 11, '/customer/statistics', 'customer/statistics/index', 'bar-chart', 303, '客户统计菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (15, '交易管理', 'transaction:manage', 1, 0, '/transaction', 'Layout', 'transaction', 400, '交易管理菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (16, '交易列表', 'transaction:list:manage', 1, 15, '/transaction/list', 'transaction/list/index', 'form', 401, '交易列表菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (17, '交易审核', 'transaction:audit:manage', 1, 15, '/transaction/audit', 'transaction/audit/index', 'audit', 402, '交易审核菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (18, '交易统计', 'transaction:statistics:manage', 1, 15, '/transaction/statistics', 'transaction/statistics/index', 'bar-chart', 403, '交易统计菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (19, '佣金管理', 'commission:manage', 1, 0, '/commission', 'Layout', 'money-collect', 500, '佣金管理菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (20, '佣金列表', 'commission:list:manage', 1, 19, '/commission/list', 'commission/list/index', 'table', 501, '佣金列表菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (21, '佣金核算', 'commission:calculate:manage', 1, 19, '/commission/calculate', 'commission/calculate/index', 'calculator', 502, '佣金核算菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (22, '佣金统计', 'commission:statistics:manage', 1, 19, '/commission/statistics', 'commission/statistics/index', 'bar-chart', 503, '佣金统计菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (23, '团队管理', 'team:manage', 1, 0, '/team', 'Layout', 'team', 600, '团队管理菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (24, '团队列表', 'team:list:manage', 1, 23, '/team/list', 'team/list/index', 'table', 601, '团队列表菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (25, '团队业绩', 'team:performance:manage', 1, 23, '/team/performance', 'team/performance/index', 'trophy', 602, '团队业绩菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (26, '带看记录', 'view:record:manage', 1, 0, '/view-record', 'Layout', 'eye', 700, '带看记录菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (27, '带看记录列表', 'view:record:list:manage', 1, 26, '/view-record/list', 'view-record/list/index', 'table', 701, '带看记录列表菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (28, '带看统计', 'view:record:statistics:manage', 1, 26, '/view-record/statistics', 'view-record/statistics/index', 'bar-chart', 702, '带看统计菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (29, '收款记录', 'payment:manage', 1, 0, '/payment', 'Layout', 'dollar', 800, '收款记录菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (30, '收款记录列表', 'payment:list:manage', 1, 29, '/payment/list', 'payment/list/index', 'table', 801, '收款记录列表菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (31, '收款统计', 'payment:statistics:manage', 1, 29, '/payment/statistics', 'payment/statistics/index', 'bar-chart', 802, '收款统计菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (32, '工作通知', 'work:notice:manage', 1, 0, '/work-notice', 'Layout', 'notification', 900, '工作通知菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (33, '通知列表', 'work:notice:list:manage', 1, 32, '/work-notice/list', 'work-notice/list/index', 'table', 901, '通知列表菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (34, '发送通知', 'work:notice:send:manage', 1, 32, '/work-notice/send', 'work-notice/send/index', 'form', 902, '发送通知菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (35, '报表管理', 'report:manage', 1, 0, '/report', 'Layout', 'bar-chart', 1000, '报表管理菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (36, '销售报表', 'report:sale:manage', 1, 35, '/report/sale', 'report/sale/index', 'table', 1001, '销售报表菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (37, '财务报表', 'report:financial:manage', 1, 35, '/report/financial', 'report/financial/index', 'dollar', 1002, '财务报表菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (38, '客户报表', 'report:customer:manage', 1, 35, '/report/customer', 'report/customer/index', 'customer-service', 1003, '客户报表菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (39, '个人中心', 'profile:manage', 1, 0, '/profile', 'Layout', 'user', 1100, '个人中心菜单', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (40, '个人资料', 'profile:info:manage', 1, 39, '/profile/info', 'profile/info/index', 'table', 1101, '个人资料菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (41, '修改密码', 'profile:password:manage', 1, 39, '/profile/password', 'profile/password/index', 'lock', 1102, '修改密码菜单', 1, '2025-12-06 22:50:16', '2025-12-06 23:20:34');
INSERT INTO `tb_permission` VALUES (42, '房源编辑页面', 'house:edit:page', 2, 4, NULL, NULL, NULL, 80, '进入房源编辑页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (43, '房源详情页面', 'house:detail:page', 2, 4, NULL, NULL, NULL, 81, '进入房源详情页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (44, '图片管理页面', 'house:image:manage:page', 2, 4, NULL, NULL, NULL, 82, '进入图片管理页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (45, '图片上传页面', 'house:image:upload:page', 2, 4, NULL, NULL, NULL, 83, '进入图片上传页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (46, '图片审核页面', 'house:image:audit:page', 2, 4, NULL, NULL, NULL, 84, '进入图片审核页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (47, '新房管理页面', 'house:new:manage:page', 2, 4, NULL, NULL, NULL, 85, '进入新房管理页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (48, '新房项目页面', 'house:new:project:page', 2, 4, NULL, NULL, NULL, 86, '进入新房项目页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (49, '新房统计页面', 'house:new:statistics:page', 2, 4, NULL, NULL, NULL, 87, '进入新房统计页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (50, '二手房管理页面', 'house:second:manage:page', 2, 4, NULL, NULL, NULL, 88, '进入二手房管理页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (51, '小区管理页面', 'house:community:manage:page', 2, 4, NULL, NULL, NULL, 89, '进入小区管理页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (52, '二手房标签页面', 'house:second:tag:page', 2, 4, NULL, NULL, NULL, 90, '进入二手房标签页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (53, '二手房统计页面', 'house:second:statistics:page', 2, 4, NULL, NULL, NULL, 91, '进入二手房统计页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (54, '高级搜索页面', 'house:search:advanced:page', 2, 4, NULL, NULL, NULL, 92, '进入高级搜索页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (55, '地图找房页面', 'house:map:page', 2, 4, NULL, NULL, NULL, 93, '进入地图找房页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (56, '智能推荐页面', 'house:recommend:page', 2, 4, NULL, NULL, NULL, 94, '进入智能推荐页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (57, '房源分配页面', 'house:assign:page', 2, 4, NULL, NULL, NULL, 95, '进入房源分配页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (58, '审核记录页面', 'house:audit:record:page', 2, 129, NULL, NULL, NULL, 96, '进入审核记录页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (59, '房源概览页面', 'house:statistics:overview:page', 2, 130, NULL, NULL, NULL, 60, '进入房源概览页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (60, '区域分布页面', 'house:statistics:region:page', 2, 130, NULL, NULL, NULL, 61, '进入区域分布页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (61, '销售统计页面', 'house:statistics:sales:page', 2, 130, NULL, NULL, NULL, 62, '进入销售统计页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (62, '价格走势页面', 'house:statistics:price:page', 2, 130, NULL, NULL, NULL, 63, '进入价格走势页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (63, '客户编辑页面', 'customer:edit:page', 2, 5, NULL, NULL, NULL, 210, '进入客户编辑页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (64, '客户详情页面', 'customer:detail:page', 2, 5, NULL, NULL, NULL, 211, '进入客户详情页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (65, '交易编辑页面', 'transaction:edit:page', 2, 6, NULL, NULL, NULL, 310, '进入交易编辑页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (66, '交易详情页面', 'transaction:detail:page', 2, 6, NULL, NULL, NULL, 311, '进入交易详情页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (67, '佣金详情页面', 'commission:detail:page', 2, 7, NULL, NULL, NULL, 410, '进入佣金详情页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (68, '团队成员详情页面', 'team:member:detail:page', 2, 8, NULL, NULL, NULL, 510, '进入团队成员详情页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (69, '用户详情页面', 'user:detail:page', 2, 1, NULL, NULL, NULL, 710, '进入用户详情页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (70, '角色详情页面', 'role:detail:page', 2, 1, NULL, NULL, NULL, 711, '进入角色详情页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (71, '权限详情页面', 'permission:detail:page', 2, 1, NULL, NULL, NULL, 712, '进入权限详情页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (72, '编辑资料页面', 'profile:edit:page', 2, 158, NULL, NULL, NULL, 1110, '进入编辑个人资料页面权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (73, '查看房源', 'house:view', 2, 4, NULL, NULL, NULL, 1, '查看房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (74, '查看个人房源', 'house:view:own', 2, 4, NULL, NULL, NULL, 2, '查看个人房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (75, '新增房源', 'house:add', 2, 4, NULL, NULL, NULL, 3, '新增房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (76, '编辑房源', 'house:edit', 2, 4, NULL, NULL, NULL, 4, '编辑房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (77, '编辑个人房源', 'house:edit:own', 2, 4, NULL, NULL, NULL, 5, '编辑个人房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (78, '审核房源', 'house:audit', 2, 4, NULL, NULL, NULL, 6, '审核房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (79, '浏览公开房源', 'house:browse', 2, 4, NULL, NULL, NULL, 7, '浏览公开房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (80, '删除房源', 'house:delete', 2, 4, NULL, NULL, NULL, 8, '删除房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (81, '删除个人房源', 'house:delete:own', 2, 4, NULL, NULL, NULL, 9, '删除个人房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (82, '查看房源列表', 'house:list:view', 2, 4, NULL, NULL, NULL, 10, '查看房源列表权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (83, '查看自己房源', 'house:list:view:own', 2, 4, NULL, NULL, NULL, 11, '仅查看自己的房源', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (84, '新增房源按钮', 'house:btn:add', 2, 4, NULL, NULL, NULL, 12, '新增房源按钮权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (85, '编辑房源按钮', 'house:btn:edit', 2, 4, NULL, NULL, NULL, 13, '编辑房源按钮权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (86, '编辑自己房源按钮', 'house:btn:edit:own', 2, 4, NULL, NULL, NULL, 14, '仅编辑自己房源', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (87, '删除房源按钮', 'house:btn:delete', 2, 4, NULL, NULL, NULL, 15, '删除房源按钮权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (88, '删除自己房源按钮', 'house:btn:delete:own', 2, 4, NULL, NULL, NULL, 16, '仅删除自己房源', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (89, '房源详情查看', 'house:detail:view', 2, 4, NULL, NULL, NULL, 17, '查看房源详情权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (90, '房源状态变更', 'house:status:change', 2, 4, NULL, NULL, NULL, 18, '修改房源状态权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (91, '房源上架', 'house:online', 2, 4, NULL, NULL, NULL, 19, '房源上架权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (92, '房源下架', 'house:offline', 2, 4, NULL, NULL, NULL, 20, '房源下架权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (93, '房源导出', 'house:export', 2, 4, NULL, NULL, NULL, 21, '导出房源数据权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (94, '房源导入', 'house:import', 2, 4, NULL, NULL, NULL, 22, '导入房源数据权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (95, '查看房源图片', 'house:image:view', 2, 4, NULL, NULL, NULL, 30, '查看房源图片权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (96, '上传房源图片', 'house:image:upload', 2, 4, NULL, NULL, NULL, 31, '上传房源图片权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (97, '删除房源图片', 'house:image:delete', 2, 4, NULL, NULL, NULL, 32, '删除房源图片权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (98, '设置封面图', 'house:image:set:cover', 2, 4, NULL, NULL, NULL, 33, '设置封面图权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (99, '审核房源图片', 'house:image:audit', 2, 4, NULL, NULL, NULL, 34, '审核房源图片权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (100, '批量上传图片', 'house:image:batch:upload', 2, 4, NULL, NULL, NULL, 35, '批量上传图片权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (101, '查看新房信息', 'house:new:view', 2, 4, NULL, NULL, NULL, 40, '查看新房扩展信息', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (102, '编辑新房信息', 'house:new:edit', 2, 4, NULL, NULL, NULL, 41, '编辑新房扩展信息', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (103, '关联项目', 'house:new:project:bind', 2, 4, NULL, NULL, NULL, 42, '关联新房项目权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (104, '解绑项目', 'house:new:project:unbind', 2, 4, NULL, NULL, NULL, 43, '解绑新房项目权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (105, '查看新房统计', 'house:new:statistics:view', 2, 4, NULL, NULL, NULL, 44, '查看新房统计数据', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (106, '查看二手房信息', 'house:second:view', 2, 4, NULL, NULL, NULL, 50, '查看二手房扩展信息', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (107, '编辑二手房信息', 'house:second:edit', 2, 4, NULL, NULL, NULL, 51, '编辑二手房扩展信息', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (108, '查看小区信息', 'house:community:view', 2, 4, NULL, NULL, NULL, 52, '查看小区信息权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (109, '编辑小区信息', 'house:community:edit', 2, 4, NULL, NULL, NULL, 53, '编辑小区信息权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (110, '新增小区', 'house:community:add', 2, 4, NULL, NULL, NULL, 54, '新增小区权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (111, '删除小区', 'house:community:delete', 2, 4, NULL, NULL, NULL, 55, '删除小区权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (112, '管理房源标签', 'house:second:tag:manage', 2, 4, NULL, NULL, NULL, 56, '管理二手房标签', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (113, '查看二手房统计', 'house:second:statistics:view', 2, 4, NULL, NULL, NULL, 57, '查看二手房统计数据', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (114, '高级搜索操作', 'house:search:advanced:btn', 2, 4, NULL, NULL, NULL, 70, '使用高级搜索功能', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (115, '地图找房操作', 'house:map:view:btn', 2, 4, NULL, NULL, NULL, 71, '使用地图找房功能', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (116, '智能推荐操作', 'house:recommend:view:btn', 2, 4, NULL, NULL, NULL, 72, '查看智能推荐房源', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (117, '保存搜索条件', 'house:search:save', 2, 4, NULL, NULL, NULL, 73, '保存搜索条件权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (118, '查看房源概览操作', 'house:statistics:overview:view:btn', 2, 130, NULL, NULL, NULL, 80, '查看房源数据概览', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (119, '查看区域分布操作', 'house:statistics:region:view:btn', 2, 130, NULL, NULL, NULL, 81, '查看区域分布统计', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (120, '查看销售统计操作', 'house:statistics:sales:view:btn', 2, 130, NULL, NULL, NULL, 82, '查看销售统计数据', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (121, '查看价格走势操作', 'house:statistics:price:view:btn', 2, 130, NULL, NULL, NULL, 83, '查看价格走势分析', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (122, '导出统计报表', 'house:statistics:export', 2, 130, NULL, NULL, NULL, 84, '导出统计报表权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (123, '分配房源操作', 'house:assign:operate', 2, 4, NULL, NULL, NULL, 90, '分配房源给销售人员', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (124, '批量分配房源', 'house:assign:batch', 2, 4, NULL, NULL, NULL, 91, '批量分配房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (125, '审核房源操作权限', 'house:audit:operate:btn', 2, 4, NULL, NULL, NULL, 92, '审核房源操作权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (126, '批量审核房源', 'house:audit:batch:btn', 2, 4, NULL, NULL, NULL, 93, '批量审核房源权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (127, '查看审核记录操作', 'house:audit:record:view:btn', 2, 129, NULL, NULL, NULL, 94, '查看房源审核记录', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (128, '撤销审核', 'house:audit:revoke', 2, 4, NULL, NULL, NULL, 95, '撤销房源审核权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (129, '查看客户', 'customer:view', 2, 5, NULL, NULL, NULL, 1, '查看客户权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (130, '查看个人客户', 'customer:view:own', 2, 5, NULL, NULL, NULL, 2, '查看个人客户权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (131, '新增客户', 'customer:add', 2, 5, NULL, NULL, NULL, 3, '新增客户权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (132, '编辑客户', 'customer:edit', 2, 5, NULL, NULL, NULL, 4, '编辑客户权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (133, '编辑个人客户', 'customer:edit:own', 2, 5, NULL, NULL, NULL, 5, '编辑个人客户权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (134, '分配客户', 'customer:assign', 2, 5, NULL, NULL, NULL, 6, '分配客户权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (135, '删除客户', 'customer:delete', 2, 5, NULL, NULL, NULL, 7, '删除客户权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (136, '删除个人客户', 'customer:delete:own', 2, 5, NULL, NULL, NULL, 8, '删除个人客户权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (137, '查看交易', 'transaction:view', 2, 6, NULL, NULL, NULL, 1, '查看交易权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (138, '查看个人交易', 'transaction:view:own', 2, 6, NULL, NULL, NULL, 2, '查看个人交易权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (139, '创建交易', 'transaction:create', 2, 6, NULL, NULL, NULL, 3, '创建交易权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (140, '编辑交易', 'transaction:edit', 2, 6, NULL, NULL, NULL, 4, '编辑交易权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (141, '编辑个人交易', 'transaction:edit:own', 2, 6, NULL, NULL, NULL, 5, '编辑个人交易权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (142, '审核交易', 'transaction:audit', 2, 6, NULL, NULL, NULL, 6, '审核交易权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (143, '删除交易', 'transaction:delete', 2, 6, NULL, NULL, NULL, 7, '删除交易权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (144, '删除个人交易', 'transaction:delete:own', 2, 6, NULL, NULL, NULL, 8, '删除个人交易权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (145, '查看佣金', 'commission:view', 2, 7, NULL, NULL, NULL, 1, '查看佣金权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (146, '查看个人佣金', 'commission:view:own', 2, 7, NULL, NULL, NULL, 2, '查看个人佣金权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (147, '核算佣金', 'commission:calculate', 2, 7, NULL, NULL, NULL, 3, '核算佣金权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (148, '发放佣金', 'commission:issue', 2, 7, NULL, NULL, NULL, 4, '发放佣金权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (149, '查看团队', 'team:view', 2, 8, NULL, NULL, NULL, 1, '查看团队权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (150, '分配团队成员', 'team:assign', 2, 8, NULL, NULL, NULL, 3, '分配团队成员权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (151, '查看带看记录', 'view:record:view', 2, 145, NULL, NULL, NULL, 1, '查看带看记录权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (152, '查看个人带看记录', 'view:record:view:own', 2, 145, NULL, NULL, NULL, 2, '查看个人带看记录权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (153, '添加带看记录', 'view:record:add', 2, 145, NULL, NULL, NULL, 3, '添加带看记录权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (154, '查看收款记录', 'payment:view', 2, 148, NULL, NULL, NULL, 1, '查看收款记录权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (155, '添加收款记录', 'payment:add', 2, 148, NULL, NULL, NULL, 2, '添加收款记录权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (156, '编辑收款记录', 'payment:edit', 2, 148, NULL, NULL, NULL, 3, '编辑收款记录权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (157, '查看工作通知', 'work:notice:view', 2, 151, NULL, NULL, NULL, 1, '查看工作通知权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (158, '发送工作通知', 'work:notice:send', 2, 151, NULL, NULL, NULL, 2, '发送工作通知权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (159, '查看报表', 'report:view', 2, 154, NULL, NULL, NULL, 1, '查看报表权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (160, '查看财务报表', 'report:financial:view', 2, 154, NULL, NULL, NULL, 2, '查看财务报表权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (161, '查看个人资料', 'profile:view', 2, 158, NULL, NULL, NULL, 1, '查看个人资料权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (162, '编辑个人资料', 'profile:edit', 2, 158, NULL, NULL, NULL, 2, '编辑个人资料权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (163, '管理收藏', 'favorites:manage', 2, 158, NULL, NULL, NULL, 3, '管理收藏权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (164, '导出数据', 'data:export', 2, 1, NULL, NULL, NULL, 800, '导出数据权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (165, '导入数据', 'data:import', 2, 1, NULL, NULL, NULL, 801, '导入数据权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (166, '房源列表按钮', 'house:list:button', 2, 4, NULL, NULL, NULL, 100, '房源列表按钮权限', 1, '2025-12-06 22:50:16', '2025-12-06 22:50:16');
INSERT INTO `tb_permission` VALUES (167, '楼盘列表', 'project:list:page', 1, 6, '/project/list', 'project/ProjectList', 'office-building', 205, '楼盘列表页面', 1, '2025-12-07 01:03:31', '2025-12-07 01:03:31');
INSERT INTO `tb_permission` VALUES (168, '小区列表', 'community:list:page', 1, 6, '/community/list', 'community/CommunityList', 'house', 206, '小区列表页面', 1, '2025-12-07 01:06:32', '2025-12-07 01:06:32');

-- ----------------------------
-- Table structure for tb_project
-- ----------------------------
DROP TABLE IF EXISTS `tb_project`;
CREATE TABLE `tb_project`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `project_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目编号',
  `project_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `developer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '开发商',
  `property_company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物业',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地址',
  `total_area` decimal(10, 2) NOT NULL COMMENT '总建筑面积',
  `plot_ratio` decimal(5, 2) NOT NULL COMMENT '容积率',
  `greening_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '绿化率',
  `total_households` int NULL DEFAULT NULL COMMENT '总户数',
  `parking_ratio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '车位',
  `price_avg` int NULL DEFAULT NULL COMMENT '平均价格',
  `property_fee` decimal(8, 2) NULL DEFAULT NULL COMMENT '物业费（元/㎡/月）',
  `property_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物业类型：住宅/公寓/别墅等',
  `opening_time` date NOT NULL COMMENT '开盘时间',
  `completion_time` datetime NOT NULL COMMENT '交房时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '项目描述',
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区县',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `metro_distance` int NULL DEFAULT NULL COMMENT '最近地铁距离(米）',
  `metro_station` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地铁名称',
  `school_district` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属学区',
  `business_district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属商圈',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '默认封面图',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1=在售，2=售罄，3=待售',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_project_no`(`project_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_project
-- ----------------------------
INSERT INTO `tb_project` VALUES (1, 'PJ20240001', '万科城市花园', '万科地产', '万科物业', '北京市朝阳区建国路88号', 50000.00, 2.50, 35.00, 123, '1:1.2', 5712, 3.80, '住宅', '2023-03-15', '2025-12-31 00:00:00', '高品质住宅小区，配套完善', '北京市', '北京', '朝阳区', 116.483200, 39.913800, 500, '大望路站', '朝阳实验小学学区', 'CBD商圈', '/uploads/project/3.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (2, 'PJ20240002', '保利江景豪庭', '保利发展', '保利物业', '上海市浦东新区陆家嘴金融区', 80000.00, 3.20, 30.00, 234, '1:1.5', 12323, 4.50, '住宅', '2023-06-01', '2026-06-30 00:00:00', '高端江景住宅，视野开阔', '上海市', '上海', '浦东新区', 121.509100, 31.239700, 800, '陆家嘴站', '浦东外国语学校附属小学', '陆家嘴金融商圈', '/uploads/project/1.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (3, 'PJ20240003', '碧桂园天河府', '碧桂园', '碧桂园服务', '广州市天河区珠江新城', 60000.00, 2.80, 32.00, 546, '1:1.3', 12316, 3.50, '住宅', '2023-01-10', '2025-08-31 00:00:00', '市中心稀缺地段，交通便利', '广东省', '广州', '天河区', 113.321300, 23.119700, 300, '珠江新城站', '天河第一实验小学', '天河CBD商圈', '/uploads/project/14.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (4, 'PJ20240004', '华润科技新城', '华润置地', '华润物业', '深圳市南山区科技园', 120000.00, 3.50, 40.00, 654, '1:1.8', 8400, 5.20, '住宅', '2024-01-20', '2026-12-31 00:00:00', '科技园核心地段，高端智能住宅', '广东省', '深圳', '南山区', 113.953600, 22.533300, 200, '高新园站', '南山外国语学校', '科技园商圈', '/uploads/project/1.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (5, 'PJ20240005', '龙湖西湖雅筑', '龙湖集团', '龙湖智慧服务', '杭州市西湖区文教区', 80000.00, 2.20, 38.00, 243, '1:1.4', 6788, 4.80, '住宅', '2024-03-10', '2026-06-30 00:00:00', '西湖边稀缺地块，低密度洋房', '浙江省', '杭州', '西湖区', 120.100000, 30.270000, 600, '文新站', '学军小学学区', '文教区商圈', '/uploads/project/17.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (6, 'PJ20240006', '中海金融中心', '中海地产', '中海物业', '成都市高新区金融城', 150000.00, 4.00, 35.00, 2334, '1:2.0', 4903, 6.00, '住宅', '2024-02-15', '2027-03-31 00:00:00', '金融城地标建筑，超高层豪宅', '四川省', '成都', '高新区', 104.066500, 30.572800, 400, '金融城站', '七中初中部', '金融城商圈', '/uploads/project/16.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (7, 'PJ20240007', '招商蛇口金陵府', '招商蛇口', '招商物业', '南京市鼓楼区中山北路', 70000.00, 2.80, 36.00, 2342, '1:1.3', 38500, 4.20, '住宅', '2024-04-01', '2026-08-31 00:00:00', '市中心稀缺地块，配套成熟', '江苏省', '南京', '鼓楼区', 118.783200, 32.066700, 400, '鼓楼站', '南京师范大学附属小学', '新街口商圈', '/uploads/project/5.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (8, 'PJ20240008', '金地东湖壹号', '金地集团', '金地物业', '武汉市武昌区东湖路', 90000.00, 3.00, 38.00, 252, '1:1.5', 28500, 3.90, '住宅', '2024-05-15', '2026-10-31 00:00:00', '东湖风景区，湖景住宅', '湖北省', '武汉', '武昌区', 114.356800, 30.545500, 600, '东亭站', '武汉小学学区', '东湖商圈', '/uploads/project/1.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (9, 'PJ20240009', '绿城西湖雅苑', '绿城中国', '绿城物业', '杭州市西湖区龙井路', 85000.00, 2.50, 42.00, 234, '1:1.4', 52000, 4.50, '住宅', '2024-06-01', '2026-12-31 00:00:00', '西湖景区内稀缺地块，低密度高端住宅', '浙江省', '杭州', '西湖区', 120.125600, 30.235600, 800, '龙井站', '西湖小学学区', '西湖景区商圈', '/uploads/project/18.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (10, 'PJ20240010', '融创照母山国际社区', '融创中国', '融创物业', '重庆市渝北区照母山', 120000.00, 3.20, 38.00, 75, '1:1.8', 18000, 3.80, '住宅', '2024-07-15', '2027-03-31 00:00:00', '照母山森林公园旁，生态宜居大盘', '重庆市', '重庆', '渝北区', 106.498700, 29.612300, 1000, '照母山站', '人民小学学区', '照母山商圈', '/uploads/project/1.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (11, 'PJ20240011', '北京城建中关村壹号', '北京城建集团', '北京城建物业', '北京市海淀区中关村大街', 95000.00, 3.00, 35.00, 567, '1:1.5', 85000, 5.50, '住宅', '2024-08-01', '2026-12-31 00:00:00', '中关村核心地段，科技人才聚集区', '北京市', '北京', '海淀区', 116.316200, 39.983500, 300, '中关村站', '中关村一小学区', '中关村商圈', '/uploads/project/4.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (12, 'PJ20240012', '越秀珠江新城国际', '广州越秀地产', '越秀物业', '广州市天河区珠江新城花城大道', 110000.00, 3.50, 40.00, 985, '1:1.8', 75000, 6.20, '住宅', '2024-09-01', '2027-03-31 00:00:00', '珠江新城CBD核心，一线江景豪宅', '广东省', '广州', '天河区', 113.321500, 23.119800, 200, '珠江新城站', '华阳小学学区', '珠江新城商圈', '/uploads/project/15.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (13, 'PJ20240013', '建发东盟商务中心', '南宁建发集团', '建发物业', '南宁市青秀区东盟商务区', 80000.00, 2.80, 42.00, 765, '1:1.3', 15000, 3.20, '住宅', '2024-10-01', '2026-10-31 00:00:00', '东盟商务区核心，国际化社区', '广西壮族自治区', '南宁', '青秀区', 108.366500, 22.816700, 500, '东盟商务区站', '滨湖路小学学区', '东盟商务区商圈', '/uploads/project/6.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (14, 'PJ20240014', '陆家嘴金融壹号', '上海陆家嘴集团', '陆家嘴物业', '上海市浦东新区陆家嘴金融贸易区', 120000.00, 4.00, 38.00, 234, '1:2.0', 120000, 7.50, '住宅', '2024-11-01', '2027-06-30 00:00:00', '陆家嘴金融核心，顶级江景豪宅', '上海市', '上海', '浦东新区', 121.509200, 31.239800, 150, '陆家嘴站', '福山外国语小学学区', '陆家嘴金融商圈', '/uploads/project/2.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (15, 'PJ20240015', '华润凤岭北壹号', '华润置地', '华润物业', '南宁市青秀区凤岭北', 85000.00, 2.80, 38.00, 467, '1:1.4', 18000, 3.80, '住宅', '2024-08-15', '2026-08-31 00:00:00', '凤岭北高端住宅区，学区房优势明显', '广西壮族自治区', '南宁', '青秀区', 108.366800, 22.823500, 400, '凤岭北站', '民主路小学学区', '凤岭北商圈', '/uploads/project/7.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (16, 'PJ20240016', '万科朝阳中心', '万科地产', '万科物业', '南宁市兴宁区朝阳广场', 70000.00, 3.20, 35.00, 214, '1:1.3', 12000, 3.20, '住宅', '2024-09-01', '2026-09-30 00:00:00', '市中心黄金地段，商业配套完善', '广西壮族自治区', '南宁', '兴宁区', 108.320500, 22.819800, 200, '朝阳广场站', '朝阳路小学学区', '朝阳商圈', '/uploads/project/8.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (17, 'PJ20240017', '龙光大学城', '龙光地产', '龙光物业', '南宁市西乡塘区大学东路', 90000.00, 2.50, 36.00, 234, '1:1.5', 9500, 2.80, '住宅', '2024-10-01', '2026-10-31 00:00:00', '大学城周边，教育资源丰富', '广西壮族自治区', '南宁', '西乡塘区', 108.292300, 22.838200, 300, '广西大学站', '广西大学附属中学学区', '大学路商圈', '/uploads/project/9.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (18, 'PJ20240018', '碧桂园江南府', '碧桂园', '碧桂园服务', '南宁市江南区白沙大道', 75000.00, 2.80, 34.00, 712, '1:1.2', 8500, 2.60, '住宅', '2024-11-01', '2026-11-30 00:00:00', '江南区核心位置，交通便利', '广西壮族自治区', '南宁', '江南区', 108.310200, 22.793600, 500, '白沙大道站', '白沙路小学学区', '江南商圈', '/uploads/project/10.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (19, 'PJ20240019', '恒大五象国际', '恒大地产', '恒大物业', '南宁市良庆区五象新区', 120000.00, 3.50, 42.00, 521, '1:1.8', 15000, 4.20, '住宅', '2024-12-01', '2027-06-30 00:00:00', '五象新区CBD，未来发展潜力巨大', '广西壮族自治区', '南宁', '良庆区', 108.381200, 22.758900, 600, '五象大道站', '五象实验一小学区', '五象新区商圈', '/uploads/project/11.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (20, 'PJ20240020', '融创龙岗壹号', '融创中国', '融创物业', '南宁市邕宁区龙岗新区', 80000.00, 2.60, 37.00, 132, '1:1.4', 8000, 2.90, '住宅', '2025-01-01', '2027-01-31 00:00:00', '龙岗新区生态住宅，江景资源优越', '广西壮族自治区', '南宁', '邕宁区', 108.487600, 22.756300, 800, '龙岗站', '邕宁高中附属小学学区', '龙岗新区商圈', '/uploads/project/12.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (21, 'PJ20240021', '保利武鸣学府', '保利发展', '保利物业', '南宁市武鸣区教育园区', 60000.00, 2.20, 40.00, 8765, '1:1.1', 6500, 2.40, '住宅', '2025-02-01', '2027-02-28 00:00:00', '教育园区配套住宅，学术氛围浓厚', '广西壮族自治区', '南宁', '武鸣区', 108.277900, 23.158600, 1000, '教育园区站', '武鸣实验小学校区', '教育园区商圈', '/uploads/project/13.jpg', 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_project` VALUES (22, 'PJ20240022', '天河国际中心', '广州地产集团', '天河物业', '广州市天河区珠江新城花城大道188号', 120000.00, 3.50, 42.00, 324, '1:1.8', 85000, 6.50, '住宅', '2024-11-01', '2027-06-30 00:00:00', '天河CBD核心地标，超甲级写字楼与高端住宅综合体', '广东省', '广州', '天河区', 113.321800, 23.119900, 200, '珠江新城站', '华阳小学学区', '珠江新城商圈', '/uploads/project/tianhe_center.jpg', 1, '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_project` VALUES (23, 'PJ20240023', '越秀文化府', '越秀地产', '越秀物业', '广州市越秀区中山五路68号', 85000.00, 2.80, 38.00, 324, '1:1.4', 75000, 4.80, '住宅', '2024-10-15', '2027-03-31 00:00:00', '越秀老城区稀缺地块，百年文化底蕴，优质学区资源', '广东省', '广州', '越秀区', 113.260500, 23.125800, 300, '公园前站', '东风东路小学学区', '北京路商圈', '/uploads/project/yuexiu_culture.jpg', 1, '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_project` VALUES (24, 'PJ20240024', '海珠江景壹号', '保利发展', '保利物业', '广州市海珠区滨江东路288号', 95000.00, 3.20, 40.00, 234, '1:1.6', 72000, 5.20, '住宅', '2024-12-01', '2027-08-31 00:00:00', '一线珠江景观，海珠区稀缺江景豪宅，270度视野', '广东省', '广州', '海珠区', 113.278900, 23.102300, 500, '鹭江站', '中山大学附属小学', '滨江东路商圈', '/uploads/project/haizhu_river.jpg', 1, '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_project` VALUES (25, 'PJ20240025', '荔湾西关大院', '万科地产', '万科物业', '广州市荔湾区龙津西路156号', 78000.00, 2.50, 36.00, 2134, '1:1.3', 65000, 4.20, '住宅', '2025-01-15', '2027-09-30 00:00:00', '西关文化发源地，传统与现代融合，配套完善', '广东省', '广州', '荔湾区', 113.238700, 23.118600, 400, '长寿路站', '荔湾实验小学学区', '上下九商圈', '/uploads/project/liwan_courtyard.jpg', 1, '2025-11-28 21:57:11', '2025-11-28 21:57:11');
INSERT INTO `tb_project` VALUES (26, 'PJ20240026', '白云山语国际', '碧桂园', '碧桂园服务', '广州市白云区白云大道北888号', 110000.00, 2.60, 45.00, 1234, '1:1.5', 55000, 3.90, '住宅', '2024-11-20', '2027-05-31 00:00:00', '白云山脚下生态大盘，低密度社区，天然氧吧', '广东省', '广州', '白云区', 113.285600, 23.182300, 800, '白云公园站', '广园小学学区', '白云新城商圈', '/uploads/project/baiyun_mountain.jpg', 1, '2025-11-28 21:57:11', '2025-11-28 21:57:11');

-- ----------------------------
-- Table structure for tb_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_role`;
CREATE TABLE `tb_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称（唯一）',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色代码（唯一）',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `data_scope` tinyint NOT NULL DEFAULT 1 COMMENT '数据权限范围：1=全部数据，2=本部门数据，3=本部门及以下数据，4=仅本人数据',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_name`(`role_name` ASC) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_role
-- ----------------------------
INSERT INTO `tb_role` VALUES (1, '系统管理员', 'admin', '系统超级管理员，拥有所有权限', 1, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_role` VALUES (2, '销售经理', 'sales_manager', '销售团队经理，管理团队和审核交易', 3, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_role` VALUES (3, '销售顾问', 'sales_consultant', '一线销售人员，负责客户和房源', 4, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_role` VALUES (4, '财务人员', 'finance_staff', '财务部门人员，负责收款和佣金核算', 2, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_role` VALUES (5, '普通用户', 'normal_user', '普通注册用户，查看公开房源', 4, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');

-- ----------------------------
-- Table structure for tb_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `tb_role_permission`;
CREATE TABLE `tb_role_permission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  `permission_id` int NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE,
  CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `tb_permission` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 683 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_role_permission
-- ----------------------------
INSERT INTO `tb_role_permission` VALUES (281, 1, 1, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (282, 1, 2, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (283, 1, 3, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (284, 1, 4, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (285, 1, 5, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (286, 1, 6, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (287, 1, 7, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (288, 1, 8, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (289, 1, 9, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (290, 1, 10, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (291, 1, 11, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (292, 1, 12, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (293, 1, 13, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (294, 1, 14, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (295, 1, 15, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (296, 1, 16, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (297, 1, 17, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (298, 1, 18, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (299, 1, 19, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (300, 1, 20, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (301, 1, 21, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (302, 1, 22, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (303, 1, 23, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (304, 1, 24, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (305, 1, 25, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (306, 1, 26, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (307, 1, 27, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (308, 1, 28, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (309, 1, 29, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (310, 1, 30, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (311, 1, 31, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (312, 1, 32, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (313, 1, 33, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (314, 1, 34, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (315, 1, 35, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (316, 1, 36, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (317, 1, 37, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (318, 1, 38, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (319, 1, 39, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (320, 1, 40, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (321, 1, 41, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (322, 1, 42, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (323, 1, 43, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (324, 1, 44, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (325, 1, 45, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (326, 1, 46, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (327, 1, 47, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (328, 1, 48, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (329, 1, 49, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (330, 1, 50, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (331, 1, 51, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (332, 1, 52, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (333, 1, 53, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (334, 1, 54, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (335, 1, 55, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (336, 1, 56, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (337, 1, 57, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (338, 1, 58, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (339, 1, 59, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (340, 1, 60, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (341, 1, 61, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (342, 1, 62, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (343, 1, 63, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (344, 1, 64, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (345, 1, 65, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (346, 1, 66, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (347, 1, 67, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (348, 1, 68, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (349, 1, 69, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (350, 1, 70, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (351, 1, 71, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (352, 1, 72, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (353, 1, 73, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (354, 1, 74, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (355, 1, 75, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (356, 1, 76, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (357, 1, 77, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (358, 1, 78, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (359, 1, 79, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (360, 1, 80, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (361, 1, 81, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (362, 1, 82, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (363, 1, 83, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (364, 1, 84, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (365, 1, 85, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (366, 1, 86, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (367, 1, 87, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (368, 1, 88, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (369, 1, 89, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (370, 1, 90, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (371, 1, 91, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (372, 1, 92, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (373, 1, 93, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (374, 1, 94, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (375, 1, 95, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (376, 1, 96, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (377, 1, 97, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (378, 1, 98, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (379, 1, 99, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (380, 1, 100, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (381, 1, 101, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (382, 1, 102, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (383, 1, 103, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (384, 1, 104, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (385, 1, 105, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (386, 1, 106, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (387, 1, 107, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (388, 1, 108, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (389, 1, 109, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (390, 1, 110, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (391, 1, 111, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (392, 1, 112, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (393, 1, 113, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (394, 1, 114, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (395, 1, 115, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (396, 1, 116, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (397, 1, 117, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (398, 1, 118, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (399, 1, 119, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (400, 1, 120, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (401, 1, 121, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (402, 1, 122, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (403, 1, 123, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (404, 1, 124, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (405, 1, 125, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (406, 1, 126, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (407, 1, 127, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (408, 1, 128, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (409, 1, 129, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (410, 1, 130, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (411, 1, 131, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (412, 1, 132, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (413, 1, 133, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (414, 1, 134, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (415, 1, 135, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (416, 1, 136, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (417, 1, 137, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (418, 1, 138, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (419, 1, 139, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (420, 1, 140, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (421, 1, 141, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (422, 1, 142, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (423, 1, 143, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (424, 1, 144, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (425, 1, 145, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (426, 1, 146, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (427, 1, 147, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (428, 1, 148, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (429, 1, 149, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (430, 1, 150, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (431, 1, 151, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (432, 1, 152, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (433, 1, 153, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (434, 1, 154, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (435, 1, 155, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (436, 1, 156, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (437, 1, 157, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (438, 1, 158, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (439, 1, 159, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (440, 1, 160, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (441, 1, 161, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (442, 1, 162, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (443, 1, 163, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (444, 1, 164, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (445, 1, 165, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (446, 1, 166, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (536, 2, 1, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (537, 2, 2, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (538, 2, 5, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (539, 2, 6, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (540, 2, 7, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (541, 2, 8, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (542, 2, 9, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (543, 2, 10, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (544, 2, 42, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (545, 2, 43, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (546, 2, 73, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (547, 2, 75, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (548, 2, 76, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (549, 2, 78, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (550, 2, 80, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (551, 2, 82, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (552, 2, 84, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (553, 2, 85, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (554, 2, 87, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (555, 2, 89, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (556, 2, 90, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (557, 2, 91, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (558, 2, 92, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (559, 2, 11, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (560, 2, 12, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (561, 2, 13, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (562, 2, 14, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (563, 2, 63, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (564, 2, 64, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (565, 2, 129, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (566, 2, 131, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (567, 2, 132, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (568, 2, 134, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (569, 2, 135, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (570, 2, 15, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (571, 2, 16, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (572, 2, 17, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (573, 2, 18, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (574, 2, 65, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (575, 2, 66, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (576, 2, 137, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (577, 2, 139, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (578, 2, 140, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (579, 2, 142, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (580, 2, 143, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (581, 2, 19, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (582, 2, 20, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (583, 2, 67, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (584, 2, 145, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (585, 2, 23, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (586, 2, 24, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (587, 2, 25, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (588, 2, 68, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (589, 2, 149, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (590, 2, 150, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (591, 2, 26, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (592, 2, 27, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (593, 2, 151, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (594, 2, 153, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (595, 2, 39, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (596, 2, 40, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (597, 2, 41, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (598, 2, 72, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (599, 2, 161, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (600, 2, 162, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (601, 3, 6, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (602, 3, 7, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (603, 3, 43, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (604, 3, 74, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (605, 3, 75, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (606, 3, 77, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (607, 3, 79, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (608, 3, 81, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (609, 3, 83, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (610, 3, 86, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (611, 3, 88, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (612, 3, 89, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (613, 3, 11, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (614, 3, 12, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (615, 3, 64, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (616, 3, 130, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (617, 3, 131, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (618, 3, 133, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (619, 3, 136, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (620, 3, 15, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (621, 3, 16, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (622, 3, 66, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (623, 3, 138, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (624, 3, 139, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (625, 3, 141, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (626, 3, 144, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (627, 3, 19, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (628, 3, 20, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (629, 3, 67, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (630, 3, 146, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (631, 3, 26, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (632, 3, 27, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (633, 3, 152, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (634, 3, 153, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (635, 3, 39, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (636, 3, 40, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (637, 3, 41, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (638, 3, 72, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (639, 3, 161, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (640, 3, 162, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (641, 4, 19, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (642, 4, 20, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (643, 4, 21, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (644, 4, 22, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (645, 4, 67, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (646, 4, 145, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (647, 4, 147, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (648, 4, 148, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (649, 4, 29, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (650, 4, 30, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (651, 4, 31, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (652, 4, 154, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (653, 4, 155, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (654, 4, 156, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (655, 4, 15, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (656, 4, 16, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (657, 4, 66, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (658, 4, 137, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (659, 4, 35, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (660, 4, 37, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (661, 4, 159, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (662, 4, 160, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (663, 4, 39, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (664, 4, 40, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (665, 4, 41, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (666, 4, 72, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (667, 4, 161, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (668, 4, 162, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (669, 5, 6, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (670, 5, 7, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (671, 5, 43, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (672, 5, 79, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (673, 5, 89, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (674, 5, 39, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (675, 5, 40, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (676, 5, 41, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (677, 5, 72, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (678, 5, 161, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (679, 5, 162, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (680, 5, 163, '2025-12-06 23:15:55');
INSERT INTO `tb_role_permission` VALUES (681, 1, 167, '2025-12-07 01:08:53');
INSERT INTO `tb_role_permission` VALUES (682, 1, 168, '2025-12-07 01:09:03');

-- ----------------------------
-- Table structure for tb_second_house_community
-- ----------------------------
DROP TABLE IF EXISTS `tb_second_house_community`;
CREATE TABLE `tb_second_house_community`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `community_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '小区名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `province` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区县',
  `build_year` int NULL DEFAULT NULL COMMENT '建成年代',
  `developer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开发商',
  `total_households` int NULL DEFAULT NULL COMMENT '总户数',
  `property_fee` decimal(8, 2) NULL DEFAULT NULL COMMENT '物业费',
  `metro_station` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最近地铁站',
  `school_district` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属学区',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_community_name_district`(`community_name` ASC, `district` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '二手房小区信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_second_house_community
-- ----------------------------
INSERT INTO `tb_second_house_community` VALUES (1, '万科城市花园', '北京市朝阳区建国路88号', '北京市', '北京', '朝阳区', 2018, '万科地产', 1200, 3.80, '大望路站', '朝阳实验小学学区', '2025-12-01 20:14:33');
INSERT INTO `tb_second_house_community` VALUES (2, '保利江景豪庭', '上海市浦东新区陆家嘴金融区', '上海市', '上海', '浦东新区', 2020, '保利发展', 2134, 4.50, '陆家嘴站', '浦东外国语学校附属小学', '2025-12-01 20:14:33');
INSERT INTO `tb_second_house_community` VALUES (3, '碧桂园天河府', '广州市天河区珠江新城', '广东省', '广州', '天河区', 2015, '碧桂园', 1231, 3.50, '珠江新城站', '天河第一实验小学', '2025-12-01 20:14:33');
INSERT INTO `tb_second_house_community` VALUES (4, '招商蛇口金陵府', '南京市鼓楼区中山北路', '江苏省', '南京', '鼓楼区', 2019, '招商蛇口', 543, 4.20, '鼓楼站', '南京师范大学附属小学', '2025-12-01 20:14:33');
INSERT INTO `tb_second_house_community` VALUES (5, '金地东湖壹号', '武汉市武昌区东湖路', '湖北省', '武汉', '武昌区', 2018, '金地集团', 1231, 3.90, '东亭站', '武汉小学学区', '2025-12-01 20:14:33');

-- ----------------------------
-- Table structure for tb_second_house_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_second_house_info`;
CREATE TABLE `tb_second_house_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `house_id` int NOT NULL COMMENT '关联房源ID',
  `build_year` int NULL DEFAULT NULL COMMENT '建筑年代（如2010）',
  `decoration_time` datetime NULL DEFAULT NULL COMMENT '装修时间',
  `last_transaction_time` datetime NULL DEFAULT NULL COMMENT '上次交易时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '房源描述（如\"满五唯一，税费低\"）',
  `is_only_house` tinyint NULL DEFAULT NULL COMMENT '是否唯一住房：0=否，1=是',
  `is_over_two` tinyint NULL DEFAULT NULL COMMENT '是否满二：0=否，1=是',
  `is_over_five` tinyint NULL DEFAULT NULL COMMENT '是否满五：0=否，1=是',
  `house_usage` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房屋用途：住宅/商住等',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `community_id` int NULL DEFAULT NULL COMMENT '小区ID',
  `mortgage_status` tinyint NULL DEFAULT NULL COMMENT '抵押状态：0=无抵押，1=有抵押',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_house_id`(`house_id` ASC) USING BTREE,
  CONSTRAINT `fk_second_house_house` FOREIGN KEY (`house_id`) REFERENCES `tb_house` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '二手房扩展信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_second_house_info
-- ----------------------------
INSERT INTO `tb_second_house_info` VALUES (1, 1, 2018, '2022-05-15 00:00:00', '2019-03-20 00:00:00', '满五唯一税费低，南北通透双阳台', 1, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:42', 1, 0);
INSERT INTO `tb_second_house_info` VALUES (2, 2, 2020, '2023-08-10 00:00:00', '2021-01-15 00:00:00', '户型方正采光足，花园洋房带产权车位', 0, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 2, 0);
INSERT INTO `tb_second_house_info` VALUES (3, 3, 2015, '2021-12-05 00:00:00', '2017-06-30 00:00:00', '地铁口 500 米，简装刚需优选首付低', 1, 1, 1, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 3, 0);
INSERT INTO `tb_second_house_info` VALUES (4, 4, 2018, '2020-03-20 00:00:00', '2019-08-10 00:00:00', '学区房近名校，全景落地窗视野无遮挡', 1, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 1, 1);
INSERT INTO `tb_second_house_info` VALUES (5, 5, 2020, '2023-10-01 00:00:00', '2022-02-28 00:00:00', '黄金中层西南向，配套成熟近商超', 0, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 2, 0);
INSERT INTO `tb_second_house_info` VALUES (6, 6, 2015, '2022-06-15 00:00:00', '2018-04-10 00:00:00', '满二唯一免增值税，电梯房朝南采光好', 1, 1, 1, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 3, 0);
INSERT INTO `tb_second_house_info` VALUES (7, 7, 2018, '2021-09-01 00:00:00', '2020-01-20 00:00:00', '顶复带超大露台，精装修拎包入住', 0, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 1, 1);
INSERT INTO `tb_second_house_info` VALUES (8, 8, 2020, '2023-03-10 00:00:00', '2021-11-05 00:00:00', '刚需小三居，总价低压力小，交通便利', 1, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 2, 0);
INSERT INTO `tb_second_house_info` VALUES (9, 17, 2019, '2022-08-15 00:00:00', '2020-05-20 00:00:00', '次新小区环境好，动静分离布局合理', 1, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 4, 0);
INSERT INTO `tb_second_house_info` VALUES (10, 18, 2020, '2023-03-10 00:00:00', '2021-02-15 00:00:00', ' \r\n满两年 南北通透 精装 楼层好 视野无遮挡', 0, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 4, 1);
INSERT INTO `tb_second_house_info` VALUES (11, 19, 2018, '2021-12-20 00:00:00', '2019-08-30 00:00:00', '满五唯一无个税，中装可按需改造', 1, 1, 1, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 5, 0);
INSERT INTO `tb_second_house_info` VALUES (12, 20, 2019, '2022-06-05 00:00:00', '2020-11-10 00:00:00', '老小区配套全，步行 10 分钟到地铁站', 1, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 5, 0);
INSERT INTO `tb_second_house_info` VALUES (13, 21, 2020, '2023-09-01 00:00:00', '2021-04-25 00:00:00', '改善型大四居，带独立衣帽间和储藏室', 0, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 4, 0);
INSERT INTO `tb_second_house_info` VALUES (14, 22, 2018, '2021-11-15 00:00:00', '2019-07-05 00:00:00', '黄金楼层无遮挡，采光通风双优', 1, 1, 1, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 5, 0);
INSERT INTO `tb_second_house_info` VALUES (15, 23, 2019, '2022-10-20 00:00:00', '2020-09-15 00:00:00', '电梯低层适合老人，满二唯一交易成本低', 1, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 4, 0);
INSERT INTO `tb_second_house_info` VALUES (16, 24, 2020, '2023-05-12 00:00:00', '2021-12-08 00:00:00', ' \r\n带电梯 近医院 装修好 南向采光好', 0, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 5, 0);
INSERT INTO `tb_second_house_info` VALUES (17, 25, 2019, '2022-07-30 00:00:00', '2020-10-22 00:00:00', ' \r\n带电梯 近医院 装修好 南向采光好', 1, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 4, 0);
INSERT INTO `tb_second_house_info` VALUES (18, 26, 2020, '2023-08-18 00:00:00', '2021-06-14 00:00:00', ' \r\n带电梯 近医院 装修好 南向采光好', 0, 1, 0, '住宅', '2025-10-30 00:31:05', '2025-12-07 16:27:55', 5, 0);

-- ----------------------------
-- Table structure for tb_team
-- ----------------------------
DROP TABLE IF EXISTS `tb_team`;
CREATE TABLE `tb_team`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '团队ID',
  `team_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '团队名称（唯一）',
  `manager_id` int NOT NULL COMMENT '团队经理ID（关联tb_user表，角色类型为3）',
  `establish_time` date NOT NULL COMMENT '成立时间',
  `team_size` int NULL DEFAULT 0 COMMENT '团队人数',
  `performance_target` decimal(15, 2) NULL DEFAULT NULL COMMENT '月度业绩目标（元）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_team_name`(`team_name` ASC) USING BTREE,
  INDEX `idx_manager_id`(`manager_id` ASC) USING BTREE,
  CONSTRAINT `fk_team_manager` FOREIGN KEY (`manager_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售团队表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_team
-- ----------------------------
INSERT INTO `tb_team` VALUES (1, '精英销售一部', 2, '2024-01-15', 3, 5000000.00, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team` VALUES (2, '精英销售二部', 3, '2024-02-20', 2, 4000000.00, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team` VALUES (3, '金牌销售团队', 2, '2024-03-10', 2, 4500000.00, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team` VALUES (4, '卓越销售团队', 3, '2024-04-05', 1, 3500000.00, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team` VALUES (5, '先锋销售团队', 2, '2024-05-12', 2, 4200000.00, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team` VALUES (6, '创新销售团队', 3, '2024-06-18', 1, 3800000.00, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team` VALUES (7, '高效销售团队', 2, '2024-07-22', 2, 4800000.00, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team` VALUES (8, '优质服务团队', 3, '2024-08-30', 1, 3600000.00, '2025-10-30 00:31:05', '2025-10-30 00:31:05');

-- ----------------------------
-- Table structure for tb_team_member
-- ----------------------------
DROP TABLE IF EXISTS `tb_team_member`;
CREATE TABLE `tb_team_member`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `team_id` int NOT NULL COMMENT '团队ID（关联tb_team表）',
  `user_id` int NOT NULL COMMENT '用户ID（关联tb_user表，角色类型为2）',
  `join_time` date NOT NULL COMMENT '加入团队时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_team_user`(`team_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_member_team` FOREIGN KEY (`team_id`) REFERENCES `tb_team` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_member_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团队成员关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_team_member
-- ----------------------------
INSERT INTO `tb_team_member` VALUES (1, 1, 4, '2024-01-15', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (2, 1, 5, '2024-01-15', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (3, 1, 6, '2024-02-01', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (4, 2, 5, '2024-02-20', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (5, 2, 6, '2024-02-20', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (6, 3, 4, '2024-03-10', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (7, 3, 5, '2024-03-10', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (8, 4, 6, '2024-04-05', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (9, 5, 4, '2024-05-12', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (10, 5, 6, '2024-05-12', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (11, 6, 5, '2024-06-18', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (12, 7, 4, '2024-07-22', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (13, 7, 5, '2024-07-22', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_team_member` VALUES (14, 8, 6, '2024-08-30', '2025-10-30 00:31:05', '2025-10-30 00:31:05');

-- ----------------------------
-- Table structure for tb_transaction
-- ----------------------------
DROP TABLE IF EXISTS `tb_transaction`;
CREATE TABLE `tb_transaction`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '交易ID',
  `transaction_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '交易编号（唯一，如\"JY20240001\"）',
  `house_id` int NOT NULL COMMENT '房源ID（关联tb_house表）',
  `customer_id` int NOT NULL COMMENT '客户ID（关联tb_customer表）',
  `sales_id` int NOT NULL COMMENT '销售ID（关联tb_user表）',
  `deal_price` decimal(12, 2) NOT NULL COMMENT '成交价格（元）',
  `deposit` decimal(10, 2) NOT NULL COMMENT '定金金额（元）',
  `deposit_time` datetime NULL DEFAULT NULL COMMENT '定金支付时间',
  `down_payment` decimal(12, 2) NULL DEFAULT NULL COMMENT '首付款金额（元）',
  `down_payment_time` datetime NULL DEFAULT NULL COMMENT '首付款支付时间',
  `loan_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '贷款金额（元）',
  `loan_status` tinyint NULL DEFAULT 0 COMMENT '贷款状态：0=未申请，1=审核中，2=已放款，3=未通过',
  `transfer_time` datetime NULL DEFAULT NULL COMMENT '过户时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '交易状态：0=待付定金，1=已付定金，2=已付首付，3=已过户，4=已完成，5=已取消',
  `manager_audit` tinyint NULL DEFAULT 0 COMMENT '经理审核：0=待审核，1=已通过，2=已驳回',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_transaction_no`(`transaction_no` ASC) USING BTREE,
  INDEX `idx_house_id`(`house_id` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `fk_trans_sales`(`sales_id` ASC) USING BTREE,
  CONSTRAINT `fk_trans_customer` FOREIGN KEY (`customer_id`) REFERENCES `tb_customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_trans_house` FOREIGN KEY (`house_id`) REFERENCES `tb_house` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_trans_sales` FOREIGN KEY (`sales_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '交易信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_transaction
-- ----------------------------
INSERT INTO `tb_transaction` VALUES (1, 'JY20240001', 5, 5, 5, 5100000.00, 100000.00, '2024-10-18 10:00:00', 2000000.00, '2024-10-25 14:00:00', 3000000.00, 2, '2024-11-15 09:00:00', 4, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_transaction` VALUES (2, 'JY20240002', 4, 4, 4, 2750000.00, 50000.00, '2024-10-20 11:00:00', 1000000.00, '2024-10-27 15:00:00', 1700000.00, 2, '2024-11-20 10:00:00', 4, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_transaction` VALUES (3, 'JY20240003', 1, 1, 4, 4450000.00, 80000.00, '2024-10-22 09:30:00', NULL, NULL, NULL, 0, NULL, 1, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_transaction` VALUES (4, 'JY20240004', 2, 2, 5, 6750000.00, 150000.00, '2024-10-24 13:00:00', 2500000.00, '2024-10-30 16:00:00', 4100000.00, 1, NULL, 2, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_transaction` VALUES (5, 'JY20240005', 3, 3, 6, 3180000.00, 60000.00, '2024-10-26 10:30:00', NULL, NULL, NULL, 0, NULL, 1, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_transaction` VALUES (6, 'JY20240006', 7, 7, 4, 8400000.00, 200000.00, '2024-10-28 14:00:00', 3200000.00, '2024-11-05 11:00:00', 5000000.00, 1, NULL, 2, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_transaction` VALUES (7, 'JY20240007', 8, 8, 5, 1780000.00, 30000.00, '2024-10-30 15:00:00', 700000.00, '2024-11-08 10:30:00', 1050000.00, 0, NULL, 0, 0, '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_transaction` VALUES (8, 'JY20240008', 6, 6, 6, 3450000.00, 70000.00, '2024-11-01 09:00:00', 1300000.00, '2024-11-10 14:00:00', 2080000.00, 1, NULL, 2, 1, '2025-10-30 00:31:05', '2025-10-30 00:31:05');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录用户名（唯一）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话（唯一）',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '头像地址',
  `role_type` tinyint NOT NULL DEFAULT 5 COMMENT '角色类型：1=系统管理员，2=销售顾问，3=销售经理，4=财务人员,5普通用户',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '账号状态：0=禁用，1=正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_role_type`(`role_type` ASC) USING BTREE COMMENT '按角色类型查询索引'
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房产销售系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1, 'zhangsan', '123456', '系统管理员', '13800000001', NULL, '/uploads/avatars/default.jpg', 1, 1, '2025-10-30 00:31:05', '2025-12-04 00:28:27');
INSERT INTO `tb_user` VALUES (2, 'mg1', '123456', '张经理', '13800000002', NULL, '/avatars/manager1.jpg', 3, 1, '2025-10-30 00:31:05', '2025-12-04 23:05:50');
INSERT INTO `tb_user` VALUES (3, 'manager2', '123456', '李经理', '13800000003', NULL, '/avatars/manager2.jpg', 3, 1, '2025-10-30 00:31:05', '2025-12-04 16:33:32');
INSERT INTO `tb_user` VALUES (4, 'sales1', '123456', '王销售', '13800000004', NULL, '/avatars/sales1.jpg', 3, 1, '2025-10-30 00:31:05', '2025-12-06 01:26:14');
INSERT INTO `tb_user` VALUES (5, 'sales2', '123456', '赵销售', '13800000005', NULL, '/avatars/sales2.jpg', 2, 1, '2025-10-30 00:31:05', '2025-12-04 16:33:32');
INSERT INTO `tb_user` VALUES (6, 'sales3', '123456', '刘销售', '13800000006', '111@qq.com', '/avatars/sales3.jpg', 2, 1, '2025-10-30 00:31:05', '2025-12-06 22:05:20');
INSERT INTO `tb_user` VALUES (7, 'finance1', '123456', '陈财务', '13800000007', NULL, '/avatars/finance1.jpg', 4, 1, '2025-10-30 00:31:05', '2025-12-04 16:33:32');
INSERT INTO `tb_user` VALUES (8, 'finance2', '123456', '杨财务', '13800000008', NULL, '/avatars/finance2.jpg', 4, 1, '2025-10-30 00:31:05', '2025-12-04 16:33:32');
INSERT INTO `tb_user` VALUES (9, 'user1', '123456', '普通用户1', '13800000009', NULL, '/avatars/user1.jpg', 5, 1, '2025-10-30 00:31:05', '2025-12-04 16:33:32');
INSERT INTO `tb_user` VALUES (10, 'user2', '123456', '普通用户2', '13800000010', NULL, '/avatars/user2.jpg', 5, 1, '2025-10-30 00:31:05', '2025-12-04 16:33:32');

-- ----------------------------
-- Table structure for tb_user_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_role`;
CREATE TABLE `tb_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user_role
-- ----------------------------
INSERT INTO `tb_user_role` VALUES (1, 1, 1, '2025-10-30 00:31:05');
INSERT INTO `tb_user_role` VALUES (2, 2, 2, '2025-10-30 00:31:05');
INSERT INTO `tb_user_role` VALUES (3, 3, 2, '2025-10-30 00:31:05');
INSERT INTO `tb_user_role` VALUES (5, 5, 3, '2025-10-30 00:31:05');
INSERT INTO `tb_user_role` VALUES (7, 7, 4, '2025-10-30 00:31:05');
INSERT INTO `tb_user_role` VALUES (8, 8, 4, '2025-10-30 00:31:05');
INSERT INTO `tb_user_role` VALUES (9, 9, 5, '2025-10-30 00:31:05');
INSERT INTO `tb_user_role` VALUES (10, 10, 5, '2025-10-30 00:31:05');
INSERT INTO `tb_user_role` VALUES (12, 4, 3, '2025-12-06 01:26:14');
INSERT INTO `tb_user_role` VALUES (17, 6, 2, '2025-12-06 22:05:20');

-- ----------------------------
-- Table structure for tb_view_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_view_record`;
CREATE TABLE `tb_view_record`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '带看ID',
  `customer_id` int NOT NULL COMMENT '客户ID（关联tb_customer表）',
  `house_id` int NOT NULL COMMENT '房源ID（关联tb_house表）',
  `sales_id` int NOT NULL COMMENT '销售ID（关联tb_user表）',
  `view_time` datetime NOT NULL COMMENT '带看时间',
  `customer_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '客户反馈（如\"价格偏高，户型满意\"）',
  `follow_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '跟进建议（如\"下周推送同小区低价房源\"）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_house`(`customer_id` ASC, `house_id` ASC) USING BTREE,
  INDEX `idx_sales_id`(`sales_id` ASC) USING BTREE,
  INDEX `fk_view_house`(`house_id` ASC) USING BTREE,
  CONSTRAINT `fk_view_customer` FOREIGN KEY (`customer_id`) REFERENCES `tb_customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_view_house` FOREIGN KEY (`house_id`) REFERENCES `tb_house` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_view_sales` FOREIGN KEY (`sales_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '带看记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_view_record
-- ----------------------------
INSERT INTO `tb_view_record` VALUES (1, 1, 1, 4, '2024-10-10 09:00:00', '户型满意，价格偏高', '继续寻找同小区低价房源', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_view_record` VALUES (2, 1, 4, 4, '2024-10-12 14:00:00', '面积偏小，装修不错', '推荐更大户型房源', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_view_record` VALUES (3, 2, 2, 5, '2024-10-11 10:30:00', '非常满意，考虑购买', '尽快安排二次看房', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_view_record` VALUES (4, 3, 3, 6, '2024-10-13 15:00:00', '楼层太高，价格合适', '推荐中低楼层房源', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_view_record` VALUES (5, 4, 4, 4, '2024-10-14 11:00:00', '性价比高，位置便利', '准备签约材料', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_view_record` VALUES (6, 5, 5, 5, '2024-10-15 16:00:00', '学区房优势明显', '联系教育部门确认学区政策', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_view_record` VALUES (7, 6, 6, 6, '2024-10-16 13:30:00', '环境优美，交通便利', '提供周边配套设施介绍', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_view_record` VALUES (8, 7, 7, 4, '2024-10-17 10:00:00', '海景房视野开阔', '安排业主洽谈价格', '2025-10-30 00:31:05', '2025-10-30 00:31:05');

-- ----------------------------
-- Table structure for tb_work_notice
-- ----------------------------
DROP TABLE IF EXISTS `tb_work_notice`;
CREATE TABLE `tb_work_notice`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `notice_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `notice_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `notice_type` tinyint NOT NULL COMMENT '通知类型：1=系统通知，2=任务分配，3=交易提醒，4=佣金通知，5=团队通知',
  `sender_id` int NOT NULL COMMENT '发送人ID',
  `sender_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送人姓名',
  `receiver_type` tinyint NOT NULL COMMENT '接收类型：1=指定用户，2=指定角色，3=指定团队，4=全部用户',
  `receiver_ids` json NULL COMMENT '接收人ID列表（根据receiver_type存储用户ID、角色ID或团队ID数组）',
  `priority` tinyint NOT NULL DEFAULT 2 COMMENT '优先级：1=紧急，2=普通，3=低',
  `read_count` int NOT NULL DEFAULT 0 COMMENT '已读人数',
  `total_receivers` int NOT NULL DEFAULT 0 COMMENT '总接收人数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=草稿，1=已发送，2=已撤回',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_notice_type`(`notice_type` ASC) USING BTREE,
  INDEX `idx_send_time`(`send_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工作通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_work_notice
-- ----------------------------
INSERT INTO `tb_work_notice` VALUES (1, '月度销售目标通知', '各位销售同事，本月销售目标已设定，请登录系统查看并努力完成。', 5, 2, '张经理', 2, '[3]', 2, 3, 3, 1, '2025-11-30 23:59:59', '2025-10-30 09:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_work_notice` VALUES (2, '新交易待审核', '您有新的交易申请需要审核，请及时处理。', 3, 4, '王销售', 1, '[2]', 1, 1, 1, 1, '2025-11-01 23:59:59', '2025-10-30 10:30:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_work_notice` VALUES (3, '佣金已发放', '您的佣金已经发放，请查收银行账户。', 4, 7, '陈财务', 1, '[4]', 2, 1, 1, 1, NULL, '2025-10-30 14:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');
INSERT INTO `tb_work_notice` VALUES (4, '系统维护通知', '系统将于今晚22:00-24:00进行维护，期间可能无法访问。', 1, 1, '系统管理员', 4, '[]', 2, 8, 10, 1, '2025-10-31 23:59:59', '2025-10-30 16:00:00', '2025-10-30 00:31:05', '2025-10-30 00:31:05');

-- ----------------------------
-- Function structure for fn_check_user_permission
-- ----------------------------
DROP FUNCTION IF EXISTS `fn_check_user_permission`;
delimiter ;;
CREATE FUNCTION `fn_check_user_permission`(p_user_id INT,
    p_permission_code VARCHAR(100))
 RETURNS tinyint(1)
  READS SQL DATA 
  DETERMINISTIC
BEGIN
    DECLARE v_has_permission TINYINT DEFAULT 0;
    
    SELECT COUNT(*) INTO v_has_permission
    FROM `tb_user` `u`
    JOIN `tb_user_role` `ur` ON `u`.`id` = `ur`.`user_id`
    JOIN `tb_role_permission` `rp` ON `ur`.`role_id` = `rp`.`role_id`
    JOIN `tb_permission` `p` ON `rp`.`permission_id` = `p`.`id`
    WHERE `u`.`id` = p_user_id 
    AND `u`.`status` = 1
    AND `p`.`permission_code` = p_permission_code
    AND `p`.`status` = 1;
    
    RETURN IF(v_has_permission > 0, 1, 0);
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for sp_change_user_role
-- ----------------------------
DROP PROCEDURE IF EXISTS `sp_change_user_role`;
delimiter ;;
CREATE PROCEDURE `sp_change_user_role`(IN p_user_id INT,
    IN p_new_role_id INT,
    IN p_operator_id INT)
BEGIN
    DECLARE v_username VARCHAR(50);
    DECLARE v_real_name VARCHAR(50);
    DECLARE v_role_name VARCHAR(50);
    DECLARE v_operator_name VARCHAR(50);
    
    -- 获取用户和角色信息
    SELECT `username`, `real_name` INTO v_username, v_real_name 
    FROM `tb_user` WHERE `id` = p_user_id;
    
    SELECT `role_name` INTO v_role_name 
    FROM `tb_role` WHERE `id` = p_new_role_id;
    
    SELECT `real_name` INTO v_operator_name
    FROM `tb_user` WHERE `id` = p_operator_id;
    
    -- 开始事务
    START TRANSACTION;
    
    -- 1. 删除用户原有角色关联
    DELETE FROM `tb_user_role` WHERE `user_id` = p_user_id;
    
    -- 2. 添加新的角色关联
    INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`)
    VALUES (p_user_id, p_new_role_id, NOW());
    
    -- 3. 更新用户表中的role_type
    UPDATE `tb_user` 
    SET `role_type` = p_new_role_id, 
        `update_time` = NOW()
    WHERE `id` = p_user_id;
    
    -- 4. 记录操作日志
    INSERT INTO `tb_operation_log` (
        `module`, `operation_type`, `operation_desc`, 
        `user_id`, `user_real_name`, `ip_address`, 
        `request_url`, `request_method`, `request_params`, 
        `status`, `operation_time`
    ) VALUES (
        '用户管理',
        '修改角色',
        CONCAT('修改用户 [', v_username, '] 的角色为 ', v_role_name),
        p_operator_id,
        v_operator_name,
        NULL,
        '/api/user/change-role',
        'POST',
        CONCAT('{"userId":', p_user_id, ',"newRoleId":', p_new_role_id, '}'),
        1,
        NOW()
    );
    
    -- 提交事务
    COMMIT;
    
    -- 返回成功
    SELECT 1 AS `result`, '角色修改成功' AS `message`;
    
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
