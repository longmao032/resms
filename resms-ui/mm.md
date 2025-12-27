表 4-1 员工信息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 主键ID |
| username | VARCHAR(50) | 否 | 否 | 登录用户名 |
| password | VARCHAR(100) | 否 | 否 | 密码 |
| real_name | VARCHAR(50) | 否 | 否 | 真实姓名 |
| phone | VARCHAR(20) | 否 | 否 | 联系电话 |
| email | VARCHAR(255) | 是 | 否 | 邮箱 |
| avatar | VARCHAR(255) | 否 | 否 | 头像地址 |
| status | TINYINT | 否 | 否 | 0=禁用,1=正常 |
| create_time | DATETIME | 否 | 否 | 创建时间 |
| update_time | DATETIME | 否 | 否 | 更新时间 |

表 4-2 角色信息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 角色ID |
| role_name | VARCHAR(50) | 否 | 否 | 角色名称 |
| role_code | VARCHAR(50) | 否 | 否 | 角色代码 |
| description | VARCHAR(200) | 是 | 否 | 角色描述 |
| data_scope | TINYINT | 否 | 否 | 数据权限范围 |
| status | TINYINT | 否 | 否 | 0=禁用,1=启用 |
| create_time | DATETIME | 否 | 否 | 创建时间 |
| update_time | DATETIME | 否 | 否 | 更新时间 |

表 4-3 权限信息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 权限ID |
| permission_name | VARCHAR(50) | 否 | 否 | 权限名称 |
| permission_code | VARCHAR(100) | 否 | 否 | 权限代码 |
| permission_type | TINYINT | 否 | 否 | 1-菜单,2-按钮,3-数据 |
| parent_id | INT | 是 | 否 | 父权限ID |
| path | VARCHAR(200) | 是 | 否 | 路由路径 |
| component | VARCHAR(100) | 是 | 否 | 组件路径 |
| icon | VARCHAR(50) | 是 | 否 | 图标 |
| sort_order | INT | 否 | 否 | 排序序号 |
| status | TINYINT | 否 | 否 | 0=禁用,1=启用 |
| create_time | DATETIME | 否 | 否 | 创建时间 |
| update_time | DATETIME | 否 | 否 | 更新时间 |

表 4-4 用户角色关联表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 关联ID |
| user_id | INT | 否 | 否 | 用户ID |
| role_id | INT | 否 | 否 | 角色ID |
| create_time | DATETIME | 否 | 否 | 创建时间 |

表 4-5 角色权限关联表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 关联ID |
| role_id | INT | 否 | 否 | 角色ID |
| permission_id | INT | 否 | 否 | 权限ID |
| create_time | DATETIME | 否 | 否 | 创建时间 |

表 4-6 销售团队表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 团队ID |
| team_name | VARCHAR(50) | 否 | 否 | 团队名称 |
| manager_id | INT | 否 | 否 | 经理ID |
| establish_time | DATE | 否 | 否 | 成立时间 |
| team_size | INT | 是 | 否 | 团队人数 |
| performance_target | DECIMAL | 是 | 否 | 月度目标 |
| create_time | DATETIME | 否 | 否 | 创建时间 |

表 4-7 团队成员关联表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 关联ID |
| team_id | INT | 否 | 否 | 团队ID |
| user_id | INT | 否 | 否 | 用户ID |
| join_time | DATE | 否 | 否 | 加入时间 |
| create_time | DATETIME | 否 | 否 | 创建时间 |

表 4-8 项目信息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 项目ID |
| project_no | VARCHAR(100) | 否 | 否 | 项目编号 |
| project_name | VARCHAR(255) | 否 | 否 | 项目名称 |
| developer | VARCHAR(255) | 否 | 否 | 开发商 |
| address | VARCHAR(255) | 否 | 否 | 地址 |
| price_avg | INT | 是 | 否 | 平均价格 |
| opening_time | DATE | 否 | 否 | 开盘时间 |
| province | VARCHAR(20) | 否 | 否 | 省份 |
| city | VARCHAR(20) | 否 | 否 | 城市 |
| district | VARCHAR(20) | 否 | 否 | 区县 |
| status | TINYINT | 否 | 否 | 状态信息 |
| creator_id | INT | 是 | 否 | 创建者ID |
| create_time | DATETIME | 否 | 否 | 创建时间 |

表 4-9 房屋信息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 房源ID |
| house_no | VARCHAR(30) | 否 | 否 | 房源编号 |
| area | DECIMAL | 否 | 否 | 建筑面积 |
| layout | VARCHAR(20) | 否 | 否 | 户型 |
| floor | INT | 否 | 否 | 所在楼层 |
| total_floor | INT | 否 | 否 | 总楼层 |
| price | DECIMAL | 否 | 否 | 挂牌价 |
| status | TINYINT | 否 | 否 | 在售/成交状态 |
| sales_id | INT | 是 | 否 | 负责销售ID |
| house_type | TINYINT | 否 | 否 | 1-二手, 2-新房 |
| project_id | INT | 否 | 否 | 所属项目ID |
| room_no | VARCHAR(20) | 否 | 否 | 房号 |
| create_time | DATETIME | 否 | 否 | 创建时间 |

表 4-10 房源图片表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 图片ID |
| house_id | INT | 否 | 否 | 关联房源ID |
| image_url | VARCHAR(500) | 否 | 否 | 图片URL |
| image_type | TINYINT | 否 | 否 | 1-封面, 2-室内 |
| sort_order | INT | 否 | 否 | 排序序号 |
| upload_user_id | INT | 否 | 否 | 上传者ID |
| create_time | DATETIME | 否 | 否 | 上传时间 |

表 4-11 新房扩展信息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 主键ID |
| house_id | INT | 否 | 否 | 房源ID |
| record_price | DECIMAL | 是 | 否 | 备案价 |
| property_right_years | INT | 是 | 否 | 产权年限 |
| estimated_delivery_time | DATE | 是 | 否 | 预计交房时间 |
| actual_area_rate | DECIMAL | 是 | 否 | 得房率 |

表 4-12 二手房小区信息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 编号 |
| community_name | VARCHAR(255) | 否 | 否 | 小区名称 |
| district | VARCHAR(40) | 是 | 否 | 区县 |
| build_year | INT | 是 | 否 | 建成年代 |
| total_households | INT | 是 | 否 | 总户数 |
| status | TINYINT | 否 | 否 | 审核状态 |

表 4-13 二手房扩展信息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 主键ID |
| house_id | INT | 否 | 否 | 房源ID |
| build_year | INT | 是 | 否 | 建筑年代 |
| description | TEXT | 是 | 否 | 房源描述 |
| house_usage | VARCHAR(20) | 是 | 否 | 房屋用途 |
| community_id | INT | 是 | 否 | 小区ID |

表 4-14 客户信息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 客户ID |
| customer_no | VARCHAR(30) | 否 | 否 | 客户编号 |
| real_name | VARCHAR(50) | 否 | 否 | 客户姓名 |
| phone | VARCHAR(20) | 否 | 否 | 客户电话 |
| demand_price | DECIMAL | 是 | 否 | 意向价格 |
| intention_level | TINYINT | 是 | 否 | 意向等级 |
| sales_id | INT | 是 | 否 | 对接销售ID |
| create_time | DATETIME | 否 | 否 | 创建时间 |

表 4-15 带看记录表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 带看ID |
| customer_id | INT | 否 | 否 | 客户ID |
| house_id | INT | 否 | 否 | 房源ID |
| sales_id | INT | 否 | 否 | 销售ID |
| view_time | DATETIME | 否 | 否 | 带看时间 |
| status | TINYINT | 否 | 否 | 状态信息 |
| create_time | DATETIME | 否 | 否 | 创建时间 |

表 4-16 交易信息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 交易ID |
| transaction_no | VARCHAR(30) | 否 | 否 | 交易编号 |
| house_id | INT | 否 | 否 | 房源ID |
| customer_id | INT | 否 | 否 | 客户ID |
| deal_price | DECIMAL | 否 | 否 | 成交价格 |
| status | TINYINT | 否 | 否 | 交易状态 |
| manager_audit | TINYINT | 是 | 否 | 经理审核 |
| create_time | DATETIME | 否 | 否 | 创建时间 |

表 4-17 收退款记录表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 收款ID |
| transaction_id | INT | 否 | 否 | 交易ID |
| amount | DECIMAL | 否 | 否 | 金额 |
| payment_status | TINYINT | 否 | 否 | 收退款状态 |
| payment_time | DATETIME | 否 | 否 | 变动时间 |
| finance_id | INT | 否 | 否 | 经办财务ID |

表 4-18 销售佣金表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 佣金ID |
| transaction_id | INT | 否 | 否 | 交易ID |
| sales_id | INT | 否 | 否 | 销售ID |
| amount | DECIMAL | 否 | 否 | 佣金金额 |
| status | TINYINT | 是 | 否 | 核算发放状态 |
| calculate_time | DATETIME | 是 | 否 | 核算时间 |

表 4-19 工作通知表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 通知ID |
| notice_title | VARCHAR(200) | 否 | 否 | 通知标题 |
| notice_type | TINYINT | 否 | 否 | 通知类型 |
| sender_id | INT | 否 | 否 | 发送者ID |
| priority | TINYINT | 否 | 否 | 优先级 |
| status | TINYINT | 否 | 否 | 状态信息 |
| send_time | DATETIME | 是 | 否 | 发送时间 |

表 4-20 操作日志表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | INT | 否 | 是 | 日志ID |
| module | VARCHAR(50) | 否 | 否 | 操作模块 |
| operation_type | VARCHAR(50) | 否 | 否 | 操作类型 |
| user_real_name | VARCHAR(50) | 否 | 否 | 操作人姓名 |
| status | TINYINT | 否 | 否 | 成功/失败状态 |
| operation_time | DATETIME | 否 | 否 | 操作时间 |

表 4-21 聊天会话表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | BIGINT | 否 | 是 | 会话ID |
| session_type | TINYINT | 否 | 否 | 私聊/群聊 |
| session_name | VARCHAR(50) | 是 | 否 | 会话名称 |
| last_message_time | DATETIME | 是 | 否 | 最后消息时间 |
| create_time | DATETIME | 否 | 否 | 创建时间 |

表 4-22 会话成员表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | BIGINT | 否 | 是 | 主键ID |
| session_id | BIGINT | 否 | 否 | 会话ID |
| user_id | INT | 否 | 否 | 用户ID |
| unread_count | INT | 否 | 否 | 未读消息数 |
| join_time | DATETIME | 否 | 否 | 加入时间 |

表 4-23 聊天消息表

| 字段名 | 数据类型 | 是否为空 | 是否主键 | 备注 |
| :---: | :---: | :---: | :---: | :---: |
| id | BIGINT | 否 | 是 | 消息ID |
| session_id | BIGINT | 否 | 否 | 会话ID |
| sender_id | INT | 否 | 否 | 发送人ID |
| content | TEXT | 否 | 否 | 消息内容 |
| msg_type | TINYINT | 否 | 否 | 消息类型 |
| create_time | DATETIME | 否 | 否 | 发送时间 |