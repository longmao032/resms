-- 恢复/填充图片路径的SQL脚本 (修正版)
-- 目的：修复图片路径，移除多余的 /uploads 前缀，使其与 FileUploadUtils 逻辑一致 (即存储为 /project/xxx.jpg)

-- 1. 确保所有用户有默认头像
-- 路径应为 /avatars/default.jpg (前端拼接 /uploads 后变为 /uploads/avatars/default.jpg)
UPDATE tb_user 
SET avatar = '/avatars/default.jpg' 
WHERE avatar IS NULL OR avatar = '' OR avatar LIKE '%uploads%';

-- 2. 尝试匹配项目编号对应的图片
UPDATE tb_project 
SET cover_url = CONCAT('/project/', project_no, '.jpg')
WHERE project_no IN ('XM202512080002');

-- 3. 为没有封面的项目随机分配 1.jpg 到 18.jpg
UPDATE tb_project 
SET cover_url = CONCAT('/project/', ((id % 18) + 1), '.jpg')
WHERE (cover_url IS NULL OR cover_url = '' OR cover_url LIKE '/uploads/%' OR cover_url NOT LIKE '/%')
AND project_no != 'XM202512080002';

-- 4. 移除可能已经产生的 /uploads 前缀 (如果之前运行过错误的脚本)
UPDATE tb_project 
SET cover_url = REPLACE(cover_url, '/uploads/', '/')
WHERE cover_url LIKE '/uploads/%';

UPDATE tb_user 
SET avatar = REPLACE(avatar, '/uploads/', '/')
WHERE avatar LIKE '/uploads/%';

UPDATE tb_house_image 
SET image_url = REPLACE(image_url, '/uploads/', '/')
WHERE image_url LIKE '/uploads/%';

-- 5. 确保开头有斜杠
UPDATE tb_project SET cover_url = CONCAT('/', cover_url) WHERE cover_url NOT LIKE '/%';
UPDATE tb_user SET avatar = CONCAT('/', avatar) WHERE avatar NOT LIKE '/%';
