-- Chat menu permission + role grants (idempotent)

-- 1) Create menu permission if not exists
INSERT INTO tb_permission (
  permission_name,
  permission_code,
  permission_type,
  parent_id,
  path,
  component,
  icon,
  sort_order,
  description,
  status,
  create_time,
  update_time
)
SELECT
  '在线聊天',
  'chat:manage',
  1,
  0,
  '/chat',
  'chat/index',
  'interaction',
  950,
  '在线聊天菜单',
  1,
  NOW(),
  NOW()
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM tb_permission WHERE permission_code = 'chat:manage'
);

-- 2) Grant to all roles (1-5) if not already granted
INSERT INTO tb_role_permission (
  role_id,
  permission_id,
  create_time
)
SELECT
  r.id AS role_id,
  p.id AS permission_id,
  NOW() AS create_time
FROM tb_role r
JOIN tb_permission p ON p.permission_code = 'chat:manage'
WHERE r.id IN (1, 2, 3, 4, 5)
  AND NOT EXISTS (
    SELECT 1
    FROM tb_role_permission rp
    WHERE rp.role_id = r.id
      AND rp.permission_id = p.id
  );
