-- 报表管理菜单权限数据
-- 请执行以下SQL初始化报表模块菜单

-- 1. 报表管理 (目录)
INSERT INTO `tb_permission` (id, permission_name, permission_code, parent_id, permission_type, path, component, icon, sort_order, description, status, create_time, update_time) 
VALUES (200, '报表管理', 'report:manage', 0, 1, '/report', NULL, 'bar-chart', 90, '报表管理目录', 1, NOW(), NOW());

-- 2. 销售报表 (菜单)
INSERT INTO `tb_permission` (id, permission_name, permission_code, parent_id, permission_type, path, component, icon, sort_order, description, status, create_time, update_time) 
VALUES (201, '销售报表', 'report:sale:manage', 200, 1, '/report/sale', 'report/sale/index', 'trend-charts', 1, '销售报表菜单', 1, NOW(), NOW());

-- 3. 财务报表 (菜单)
INSERT INTO `tb_permission` (id, permission_name, permission_code, parent_id, permission_type, path, component, icon, sort_order, description, status, create_time, update_time) 
VALUES (202, '财务报表', 'report:financial:manage', 200, 1, '/report/financial', 'report/financial/index', 'wallet', 2, '财务报表菜单', 1, NOW(), NOW());

-- 4. 客户报表 (菜单)
INSERT INTO `tb_permission` (id, permission_name, permission_code, parent_id, permission_type, path, component, icon, sort_order, description, status, create_time, update_time) 
VALUES (203, '客户报表', 'report:customer:manage', 200, 1, '/report/customer', 'report/customer/index', 'user', 3, '客户报表菜单', 1, NOW(), NOW());

-- 5. 分配给系统管理员角色 (ID=1)
INSERT INTO `tb_role_permission` (role_id, permission_id) VALUES (1, 200);
INSERT INTO `tb_role_permission` (role_id, permission_id) VALUES (1, 201);
INSERT INTO `tb_role_permission` (role_id, permission_id) VALUES (1, 202);
INSERT INTO `tb_role_permission` (role_id, permission_id) VALUES (1, 203);
