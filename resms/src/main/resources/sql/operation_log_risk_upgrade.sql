ALTER TABLE tb_operation_log
  ADD COLUMN risk_level TINYINT NULL COMMENT '风险等级：1高危 2中等 3普通' AFTER operation_desc,
  ADD COLUMN risk_dimension VARCHAR(100) NULL COMMENT '风险维度：FINANCIAL/DATA/SYSTEM 逗号分隔' AFTER risk_level;
