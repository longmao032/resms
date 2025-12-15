房产销售管理系统 (HMS) - BUG修复与功能完善技术实施文档
一、房源列表模块
1. 新增与权限问题
需求分析：

管理员/销售经理新增 -> 状态可选（默认在售）。

销售顾问新增 -> 状态强制为0（待审核），sales_id 锁定为当前用户，触发通知。

数据库实施：

表：tb_house, tb_work_notice, tb_user, tb_role

逻辑：

新增房源时，后端判断 CurrentSessionUser.roleId。

若角色为 销售顾问 (Role ID = 3)：

INSERT INTO tb_house (..., status, sales_id) VALUES (..., 0, CurrentUserId);

触发任务：查询该销售所属团队的经理 ID。

SQL: SELECT manager_id FROM tb_team t JOIN tb_team_member tm ON t.id = tm.team_id WHERE tm.user_id = {CurrentUserId}

插入通知：

INSERT INTO tb_work_notice (notice_title, notice_type, biz_type, biz_id, sender_id, receiver_type, receiver_ids, router_path) VALUES ('房源审核申请', 2, 'house_audit', {NewHouseId}, {CurrentUserId}, 1, JSON_ARRAY({ManagerId}), '/house/audit');

2. 编辑功能与扩展信息填充
需求分析：

修复二手房/新房扩展信息获取失败问题。

销售顾问编辑时，status 和 sales_id 字段前端只读。

数据库实施：

表：tb_house (主表), tb_new_house_info (新房扩展), tb_second_house_info (二手房扩展), tb_house_image

查询逻辑：

当 tb_house.house_type = 1 (二手房) 时，LEFT JOIN tb_second_house_info 获取 build_year, decoration_time。

当 tb_house.house_type = 2 (新房) 时，LEFT JOIN tb_new_house_info 获取 pre_sale_license_no, record_price, actual_area_rate (得房率), elevator_ratio (梯户比), estimated_delivery_time (交房时间)。

图片问题：确保 tb_house_image 表中 house_id 关联正确，编辑时需支持 INSERT/UPDATE/DELETE 该表数据。

3. 负责销售下拉列表
需求分析：获取所有销售人员。

数据库实施：

SQL：SELECT id, real_name FROM tb_user WHERE role_type IN (2, 3) AND status = 1; (获取销售经理和销售顾问)。

4. 删除逻辑
需求分析：已预定/已成交不可删；销售顾问无删除/审核按钮。

数据库实施：

逻辑：前端根据 tb_house.status 判断：若状态为 2 (已预订) 或 3 (已成交)，隐藏删除按钮。

后端校验：接口层再次校验 status，防止接口被绕过。

二、小区/楼盘列表模块
1. 数据权限隔离
需求分析：

管理员：看所有。

销售经理：看自己创建的。

销售顾问：看所属团队经理创建的。

数据库实施：

表：tb_project (新房楼盘), tb_second_house_community (二手房小区)

查询逻辑：

管理员：SELECT * FROM tb_project

销售经理：SELECT * FROM tb_project WHERE creator_id = {CurrentUserId}

销售顾问：

先查经理ID：SELECT manager_id FROM tb_team t JOIN tb_team_member tm ON t.id = tm.team_id WHERE tm.user_id = {CurrentUserId}

再查楼盘：SELECT * FROM tb_project WHERE creator_id = {ManagerId}

2. 按钮权限控制
实施：前端根据 CurrentSessionUser.roleId 判断。若 Role ID = 3 (销售顾问)，隐藏 add, edit, delete 按钮，仅保留 view。

三、客户列表模块
1. 数据权限与删除关联校验
需求分析：权限隔离；删除时检查关联看房记录。

数据库实施：

表：tb_customer, tb_view_record

权限SQL：

管理员：SELECT * FROM tb_customer

销售顾问：SELECT * FROM tb_customer WHERE sales_id = {CurrentUserId}

销售经理：需联表查询团队成员负责的客户。 SELECT c.* FROM tb_customer c JOIN tb_team_member tm ON c.sales_id = tm.user_id JOIN tb_team t ON tm.team_id = t.id WHERE t.manager_id = {CurrentUserId}

删除校验：

执行删除前检查：SELECT COUNT(*) FROM tb_view_record WHERE customer_id = {TargetId}

若返回值 > 0，抛出异常：“该客户存在关联看房记录，无法删除”。

2. 新增客户逻辑
实施：INSERT INTO tb_customer (..., sales_id) VALUES (..., {CurrentUserId})。强制后端赋值，不依赖前端传参。

四、客户跟进 (带看记录) 模块
1. 新增跟进逻辑完善
需求分析：只能选已预约的记录；关联房源；带看时间不可改。

数据库实施：

表：tb_view_record

下拉列表数据源：SELECT id, customer_id, house_id, view_time FROM tb_view_record WHERE status = 1 (1=已预约)。

填充逻辑：前端选中预约记录后，根据返回的 house_id 和 view_time 自动填充表单，并设置字段 disabled。

2. 数据一致性修复
需求分析：预约状态未完成不能关联用户（实际上是不能录入完成反馈）。

实施：仅当 tb_view_record.status = 1 (已预约) 时，才允许进入“填写跟进/完成带看”的流程，操作完成后更新 status = 2 (已完成)。

五、预约看房模块
1. 分页修复
实施：后端 Mapper XML 中确保没有写死的 LIMIT，并在 Service 层正确调用 PageHelper (如 MyBatis) 或 Hibernate 分页接口。

2. 新增预约逻辑
需求分析：角色区分。

数据库实施：

销售顾问：sales_id 字段后端强制设为 {CurrentUserId}。

管理员/经理：允许前端传入 sales_id，若未传则默认为 {CurrentUserId}。

3. 状态流转与按钮
数据库实施：

表：tb_view_record

操作：

点击“完成” -> UPDATE tb_view_record SET status = 2 WHERE id = ?

点击“取消” -> UPDATE tb_view_record SET status = 3, cancel_reason = ? WHERE id = ?

权限：销售顾问角色隐藏物理删除接口调用。

六、交易列表模块
1. 权限隔离
数据库实施：

表：tb_transaction

销售顾问：WHERE sales_id = {CurrentUserId}

销售经理：同客户列表逻辑，查询团队下所有成员的交易单。

2. 审批流重构
需求分析：销售经理审定金，财务审后续。

数据库实施：

表：tb_transaction

字段：status, manager_audit (现有字段)

逻辑：

阶段1 (定金审批)：当 status = 1 (已付定金) 时，销售经理操作 manager_audit = 1 (通过)。

阶段2 (财务审批)：当 status = 2 (已付首付) 或 loan_status = 2 (已放款) 时，需增加财务审核逻辑（建议复用 manager_audit 或增加 finance_audit 字段，或使用 tb_work_notice 通知财务去确认 tb_payment 收款记录）。

建议：交易的审批本质上是对资金的确认，应强关联 tb_payment 表的 payment_status。

七、佣金列表模块
1. 新增功能与流程完善
需求分析：新增佣金 -> 审核 -> 发放。

数据库实施：

表：tb_commission

字段更新：

status: 建议定义为 0=待核算(草稿), 1=待审核(新增状态), 2=已核算/已审核, 3=已发放。

逻辑：

新增：插入 status = 0。

提交审核：更新 status = 1，并在 tb_work_notice 插入一条发给管理员/财务的消息。

审核：管理员/财务更新 status = 2，记录 finance_id (操作人)。

发放：财务点击发放 -> 发送通知给管理员 -> 管理员确认 -> 更新 status = 3，写入 issue_time。

八、团队列表模块
1. 解散逻辑
数据库实施：

表：tb_team_member

校验SQL：SELECT COUNT(*) FROM tb_team_member WHERE team_id = {TeamId}。只有当结果 <= 1 (仅含经理自己) 时，允许执行 DELETE FROM tb_team WHERE id = {TeamId}。

2. 权限隔离
逻辑：

管理员：SELECT * FROM tb_team

团队经理：SELECT * FROM tb_team WHERE manager_id = {CurrentUserId}

销售顾问：SELECT t.* FROM tb_team t JOIN tb_team_member tm ON t.id = tm.team_id WHERE tm.user_id = {CurrentUserId}

九、收款记录列表
1. 时间格式修复
实施：

后端 Entity 类中，payment_time 字段添加注解： @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

确保数据库 tb_payment 表中 payment_time 字段类型为 DATETIME。

十、通知列表模块
1. 跳转逻辑
数据库实施：

表：tb_work_notice

关键字段：router_path (路由), biz_id (业务ID), biz_type (业务类型)。

逻辑：

生成通知时必须写入 router_path (例如 /house/audit?id=27)。

前端点击“前往处理”时，读取 router_path 进行路由跳转，并携带 biz_id 参数打开对应弹窗。

十一、个人资料模块
1. 数据获取与文件上传
数据库实施：

表：tb_user

SQL：确保查询语句包含 phone, email, avatar 字段。

文件上传：后端需实现 /api/upload 接口，将图片保存至服务器/OSS，并返回 URL。更新头像时：UPDATE tb_user SET avatar = {NewUrl} WHERE id = {CurrentUserId}。

十二、客户报表模块
1. 导出功能
实施：

后端引入 Apache POI 或 EasyExcel 依赖。

编写接口 /report/customer/export。

根据当前筛选条件查询数据库，生成 Excel 流返回给前端下载。

十三、操作日志模块
1. 日志记录实现
数据库实施：

表：tb_operation_log

AOP切面编程：

在 Controller 层定义切面。

记录字段：

module: 操作模块 (如 "房源管理")

operation_type: "新增", "删除", "审批"

operation_desc: 详细描述 (如 "审核通过房源 FC20240001")

user_id: CurrentUserId

operation_time: NOW()

status: 1 (成功) / 0 (失败)

分级：在自定义注解中增加 level 属性 (High, Medium, Normal)，保存到日志表的扩展字段或 description 中。


这个分类标准主要依据三个维度：资金安全（Financial）、数据资产安全（Data Integrity/Privacy） 和 系统权限安全（System Security）。
您可以将此标准直接用于实现 tb_operation_log 中的日志分级功能。1. 高危行为 (High Risk)定义：直接涉及资金变动、核心资产丢失、敏感数据泄露或系统最高权限变更的操作。模块涉及表 (Table)具体操作行为风险理由权限管理tb_role, tb_permission, tb_user_role修改角色权限、分配角色直接影响用户能做什么，若被恶意篡改可能导致系统被接管。用户管理tb_user重置密码、禁用/删除用户影响用户账号的可用性和安全性。财务/收款tb_payment确认收款、作废收款直接对应 amount 资金字段的变动，涉及公司实际收入，是审计的重中之重。佣金管理tb_commission佣金发放 (Issue)将资金从公司账户划出给销售人员，直接涉及资金流出。数据导出tb_customer, tb_house批量导出客户/房源数据房产中介的核心资产是客户资料（phone, id_card）和房源，批量导出通常意味着“飞单”或数据泄露风险。核心删除tb_house, tb_customer, tb_transaction物理删除房源/客户/交易核心业务数据的永久性销毁，属于不可逆操作（建议系统内仅做逻辑删除 status 变更）。团队管理tb_team解散团队破坏组织架构，影响该团队下所有成员的数据归属和业绩核算。2. 中等风险行为 (Medium Risk)定义：涉及业务流程的关键节点流转、状态变更、敏感信息修改，通常需要审批的操作。模块涉及表 (Table)具体操作行为风险理由房源管理tb_house, tb_house_image房源审核、房源状态变更、修改价格房源从“待售”变“售罄”或价格变动，直接影响交易金额和库存状态。交易管理tb_transaction交易审批 (Manager Audit)决定一笔交易是否继续推进，锁定房源状态，影响后续财务流程。佣金核算tb_commission佣金核算 (Calculate)虽然未发放，但计算金额 amount 决定了后续发放的数额，计算错误会导致财务损失。资源分配tb_customer, tb_house分配客户、分配房源涉及“私海”资源的转移，影响销售人员的业绩归属，容易产生内部纠纷。新房/楼盘tb_project, tb_new_house_info新增/编辑楼盘项目楼盘信息是新房销售的基础，错误的信息（如佣金点位、开盘时间）会导致严重的销售误导。客户编辑tb_customer修改关键信息 (如意向等级)影响销售策略的判断，但一般不涉及直接资产损失。3. 普通行为 (Normal / Low Risk)定义：日常高频操作、只读操作、个人维度的非敏感修改。模块涉及表 (Table)具体操作行为风险理由登录/登出tb_operation_log用户登录、退出记录系统的访问轨迹，属于常规审计。带看记录tb_view_record新增/完成带看、写跟进销售日常工作记录，数据量大，风险低。房源浏览tb_house查看房源列表/详情只读操作，不改变数据状态。消息通知tb_work_notice, tb_chat_message发送通知、聊天消息正常的业务沟通行为。个人中心tb_user修改头像、修改个人资料仅影响当前登录用户自身的信息展示。预约看房tb_view_record发起预约业务流程的起始动作，尚未产生实质性影响。图片上传tb_house_image上传图片补充资料行为（除非上传违规图片，否则属于低危）。4. 数据库层面的日志实现建议在您实现 13、操作日志 需求时，可以在 tb_operation_log 表中增加 risk_level 字段，或者在代码层面的注解中定义。AOP 注解示例 (Java/SpringBoot):Java// 高危操作：作废收款
@Log(module = "财务管理", action = "作废收款", level = RiskLevel.HIGH)
public Result voidPayment(...) { ... }

// 中等操作：房源审核
@Log(module = "房源管理", action = "审核房源", level = RiskLevel.MEDIUM)
public Result auditHouse(...) { ... }

// 普通操作：新增带看
@Log(module = "客户跟进", action = "新增带看", level = RiskLevel.NORMAL)
public Result addViewRecord(...) { ... }
SQL 查询建议：管理员在后台查询日志时，可以重点关注 HIGH 级别的操作，例如：SQL-- 查询最近的高危操作，监控谁在删除数据或动用资金
SELECT * FROM tb_operation_log 
WHERE risk_level = 'HIGH' 
ORDER BY operation_time DESC;


这些功能通常遵循 提交(Submit) -> 待办通知(Notify) -> 审核(Audit) -> 结果通知(Feedback) 的闭环逻辑。1. 房源管理模块 (House Management)这是核心资产的入库流程，必须严控数据质量。业务场景：销售顾问新增房源（二手房）或新房项目。触发条件：tb_house.status = 0 (待审核)tb_project.status = 4 (待审核 - 对应新房楼盘)tb_second_house_community.status = 1 (待审核 - 对应小区)通知流程：提交申请：销售顾问点击“新增”，数据入库。触发通知：系统向所属团队的销售经理发送 notice_type=2 (任务分配) 的通知。Router: /house/audit 或 /project/list审核结果：经理点击“通过/驳回”。反馈通知：系统向**发起人（销售顾问）**发送 notice_type=1 (系统通知) 的结果消息。2. 交易审批模块 (Transaction Audit)这是资金流转的起始点，涉及风控，需求文档中明确了分级审批机制。业务场景：交易流程推进（定金 -> 首付 -> 贷款 -> 完结）。触发条件：阶段一（经理审定金）：tb_transaction.status = 1 (已付定金) 且 manager_audit = 0 (待审核)。阶段二（财务审后续）：tb_transaction.status = 2 (已付首付) 或 loan_status 变更。通知流程：提交交易：销售顾问录入交易并上传定金凭证。触发通知 A：向销售经理发送“定金审核”任务。触发通知 B：(阶段二) 当录入首付款/按揭信息后，向财务人员发送“款项确认”任务。反馈通知：审核通过/驳回后，通知销售顾问。3. 收款确认模块 (Payment Confirmation)每一笔进入公司的资金都需要财务确认到账，防止“虚假入账”。业务场景：销售录入客户转账记录，财务核对银行流水。触发条件：tb_payment.payment_status = 0 (待确认)通知流程：录入收款：销售顾问上传转账截图，创建收款记录。触发通知：向财务人员发送 biz_type='payment' 的待办通知。确认到账：财务核实后点击“确认”，payment_status 变更为 1。反馈通知：向销售顾问发送“收款已确认”通知，交易状态可能随之自动流转。4. 佣金发放模块 (Commission Issue) - (新增重点需求)需求文档中明确要求：财务发放佣金前，必须经过管理员审核。业务场景：财务核算出佣金后，申请发放现金。触发条件：tb_commission.status = 1 (已核算/待审核) —— 注：需根据需求修改状态定义通知流程：提交发放申请：财务人员在佣金列表点击“申请发放”。触发通知：向系统管理员发送 notice_type=4 (佣金通知) 的审批请求。管理员审核：管理员确认无误后点击“通过”。执行发放：系统自动（或由财务二次确认）将状态改为 2 (已发放)，并记录 issue_time。反馈通知：向财务人员和对应的销售人员发送“佣金已发放”的好消息。5. 图片/实勘图审核 (Image Audit)房源图片涉及合规性（如不能包含个人电话、水印等），通常需要审核。业务场景：上传房源实拍图。触发条件：tb_house_image.audit_status = 0 (待审核)通知流程：上传图片：销售上传图片。触发通知：向销售经理或运营专员发送审核任务。审核操作：管理员/经理在“图片审核页面”进行批量通过或删除。6. 预约看房 (Appointment)虽然不是严格的行政审批，但属于双向确认流程。业务场景：客户线上预约或销售录入预约。触发条件：tb_view_record.status = 0 (待确认)通知流程：发起预约：客户在小程序/App发起预约。触发通知：向负责房源的销售顾问发送“新带看预约”提醒。确认预约：销售点击“接受预约”，状态变为 1 (已预约)。反馈通知：(可选) 向客户发送短信或站内信通知预约成功。总结：技术实现映射表功能模块关键状态字段 (DB)发起人接收人(审批人)关联 tb_work_notice 的 biz_type房源新增tb_house.status = 0销售顾问销售经理house_audit项目/小区新增tb_project.status = 4销售顾问管理员/经理project_audit / community_audit交易(定金)manager_audit = 0销售顾问销售经理transaction_audit收款确认payment_status = 0销售顾问财务人员payment_confirm佣金发放tb_commission.status = 1财务人员系统管理员commission_audit图片审核audit_status = 0销售顾问经理/管理员image_audit


根据软件工程的依赖关系和您现有 BUG 的紧急程度，我建议按照 “基础权限 -> 核心数据 -> 业务流程 -> 财务结算 -> 辅助功能” 的顺序进行修复。

核心理由：目前的 BUG 大多集中在**“权限控制失效”（如销售能看到不该看的按钮）和“数据归属错误”**（如新增数据未绑定当前用户）。如果不先解决底层的用户上下文和权限校验，后续修改房源、客户模块时会重复返工。

以下是推荐的五阶段修复路线图：

第一阶段：地基修复（RBAC 与 用户上下文）
涉及模块：用户管理、权限管理、团队管理 优先级：🔥🔥🔥🔥🔥 (最高)

为什么先修这里？ 文档中 80% 的问题（如：销售顾问显示删除按钮、能看所有数据、新增数据未绑定自己）都源于后端未正确获取当前登录人信息以及前端未根据角色正确渲染按钮。

执行步骤：

后端上下文封装：

确保后端有一个全局工具类（如 UserContext 或 SecurityUtils），能从 Token 中准确解析出 userId、roleId 和 teamId。

解决 BUG：新增客户/预约/房源时无法录入当前 ID 的问题。

前端权限指令：

修复前端的 v-if 或自定义指令（如 v-permission），确保销售顾问角色的页面隐藏 删除、审核 按钮。

解决 BUG：房源、小区、客户列表中的按钮显示错误。

团队数据隔离 SQL：

在 MyBatis/DAO 层封装通用的“数据权限 SQL 片段”。

解决 BUG：销售经理能看全团队、销售顾问只能看自己的数据隔离逻辑。

第二阶段：核心资产修复（房源与项目）
涉及模块：房源列表、小区/楼盘列表、图片上传 优先级：🔥🔥🔥🔥 (高)

为什么是第二个？ 这是业务系统的源头数据。如果房源状态不对（如未审核就上架）或扩展信息（新房/二手房字段）存不进去，后续的带看和交易就全是错的。

执行步骤：

修复新增/编辑逻辑：

实现新房 (tb_new_house_info) 和二手房 (tb_second_house_info) 的联表查询与更新。

解决 BUG：新房预售证、备案价存入失败；二手房年代填充失败。

状态流转与审批接入：

修改 save 接口：若当前用户是销售，强制 status=0 (待审核)。

开发通知工具类：实现 NotificationService.send()，在此处首次调用，发送审批通知给经理。

图片上传组件：

封装统一的 OSS/本地文件上传接口，替换掉旧的、可能报错的上传逻辑。

第三阶段：客户与带看流程
涉及模块：客户列表、预约看房、客户跟进 优先级：🔥🔥🔥 (中高)

为什么是第三个？ 有了“人”（权限）和“房”（房源），接下来就是“客”和“看”。这部分的逻辑强依赖于前两个阶段。

执行步骤：

数据清洗与关联：

修复客户列表的 sales_id 绑定逻辑。

修复删除客户时的校验逻辑（先查 tb_view_record，有记录则抛异常）。

预约状态机修复：

严格限制状态流转：0(待确认) -> 1(已预约) -> 2(已完成)。

解决 BUG：未完成预约就能写跟进反馈的问题。

分页插件修复：

检查 PageHelper 或后端分页参数传递，解决“无法查看全部记录”的 BUG。

第四阶段：资金流与审批（高危区）
涉及模块：交易列表、收款记录、佣金管理 优先级：🔥🔥🔥 (关键)

为什么是第四个？ 涉及钱的模块必须在数据流转稳定后处理。这里的核心是**“审批流重构”**。

执行步骤：

交易审批分级：

拆分接口：managerAudit (改 manager_audit 字段) 和 financeConfirm (改 loan_status 或关联收款)。

收款时间修复：

全局配置 Jackson 或 Fastjson 的时间格式化 (yyyy-MM-dd HH:mm:ss)，彻底解决前端传参报错。

佣金新增与发放：

实现缺失的“新增佣金”接口。

实现“发放前校验”：必须有管理员的审核记录才能执行 status=2 的更新操作。

第五阶段：报表、日志与体验优化
涉及模块：通知列表、个人资料、客户报表、操作日志 优先级：🔥🔥 (中)

为什么是最后？ 这些是辅助功能。虽然重要，但不阻塞核心业务流程。

执行步骤：

通知跳转：

前端解析通知中的 router_path 和 biz_id，实现点击“前往处理”弹出对应单据详情。

报表导出：

引入 EasyExcel 或 POI，实现客户报表的 Excel 导出。

全局日志 AOP：

配置切面，记录高危操作（如删除、资金变动），满足审计需求。