# Project Analysis Report

## 1. Directory Tree & Rules

### Root Directory
- `resms/`: Backend Project (Java/Spring Boot)
- `resms-ui/`: Frontend Project (Vue.js/Vite)
- `hms_sys.sql`: Database initialization script

### Backend Structure (`resms`)
**Root**: `src/main/java/com/guang/resms`

| Directory | Purpose | Naming Convention |
| :--- | :--- | :--- |
| `controller` | REST API Controllers | `*Controller.java` |
| `service` | Business Logic Interfaces | `*Service.java` |
| `service/impl` | Service Implementations | `*ServiceImpl.java` |
| `mapper` | MyBatis Data Access | `*Mapper.java` |
| `entity` | Database Entities & DTOs | `*Entity.java` (or just CamelCase matching table), `*VO.java`, `*DTO.java` |
| `common` | Global utilities, Result wrappers | `Result.java`, `PageResult.java` |
| `config` | Spring Configuration | `*Config.java` |

### Frontend Structure (`resms-ui`)
**Root**: `src`

| Directory | Purpose | Naming Convention |
| :--- | :--- | :--- |
| `api` | API request definitions | `*.ts` (module based, e.g., `user.ts`) |
| `views` | Page Components | PascalCase directories/files (e.g., `work-notice/NoticeList.vue`) |
| `components`| Reusable UI Components | PascalCase (e.g., `NoticeForm.vue`) |
| `router` | Routing configuration | `index.ts` |
| `stores` | Pinia State Management | `*.ts` (e.g., `user.ts`) |
| `utils` | Utility functions | `request.ts` (axios instance), etc. |

## 2. Database Schema

### Overview
- **Database Name**: `hms_sys`
- **Engine**: InnoDB
- **Charset**: utf8mb4

### Tables & Fields

#### 1. Core System
*   **`tb_user`** (User Accounts)
    *   `id`, `username`, `password`, `real_name`, `phone`, `email`, `avatar`, `role_type` (1=Admin, 2=Consultant, 3=Manager, 4=Finance, 5=User), `status`, `create_time`, `update_time`
*   **`tb_role`** (Roles)
    *   `id`, `role_name`, `role_code`, `description`, `data_scope`, `status`, `create_time`, `update_time`
*   **`tb_user_role`** (User-Role Relation)
    *   `id`, `user_id`, `role_id`
*   **`tb_permission`** (Permissions/Menus)
    *   `id`, `permission_name`, `permission_code`, `permission_type` (1=Menu, 2=Btn, 3=Data), `parent_id`, `path`, `component`, `icon`, `sort_order`, `description`, `status`
*   **`tb_role_permission`** (Role-Permission Relation)
    *   `id`, `role_id`, `permission_id`
*   **`tb_operation_log`** (Audit Logs)
    *   `id`, `module`, `operation_type`, `operation_desc`, `user_id`, `user_real_name`, `ip_address`, `request_url`, `request_method`, `request_params`, `response_result`, `status`, `error_message`, `execute_time`, `operation_time`

#### 2. Housing Resources
*   **`tb_house`** (Core House Info - Second-hand focus?)
    *   `id`, `house_no`, `area`, `unit_name`, `layout`, `floor`, `total_floor`, `orientation`, `decoration`, `price`, `status`, `sales_id`, `description`, `house_type` (1=Second, 2=New, 3=Rent), `building_no`, `project_id`, `room_no`
*   **`tb_house_image`** (Images)
    *   `id`, `house_id`, `image_url`, `image_type` (1=Cover, 2=Indoor, 3=Layout, 4=Env), `sort_order`, `upload_user_id`, `audit_status`, `audit_user_id`, `audit_time`
*   **`tb_new_house_info`** (New House Specifics)
    *   `id`, `house_id`, `pre_sale_license_no`, `record_price`, `property_right_years`, `estimated_delivery_time`, `delivery_standard`, `elevator_ratio`, `actual_area_rate`
*   **`tb_second_house_info`** (Second-hand House Specifics)
    *   `id`, `house_id`, `build_year`, `decoration_time`, `last_transaction_time`, `description`, `is_only_house`, `is_over_two`, `is_over_five`, `house_usage`, `community_id`, `mortgage_status`
*   **`tb_second_house_community`** (Communities)
    *   `id`, `community_name`, `address`, `province`, `city`, `district`, `build_year`, `developer`, `total_households`, `property_fee`, `metro_station`, `school_district`, `status`
*   **`tb_project`** (New House Projects)
    *   `id`, `project_no`, `project_name`, `developer`, `property_company`, `address`, `total_area`, `plot_ratio`, `greening_rate`, `total_households`, `parking_ratio`, `price_avg`, `property_fee`, `property_type`, `opening_time`, `completion_time`, `description`, `province`, `city`, `district`, `longitude`, `latitude`, `metro_distance`, `metro_station`, `school_district`, `business_district`, `cover_url`, `status`
*   **`tb_view_record`** (House Viewing)
    *   `id`, `customer_id`, `house_id`, `sales_id`, `view_time`, `customer_feedback`, `follow_advice`
*   **`tb_favorites`** (User Favorites)
    *   `id`, `user_id`, `target_type` (project/building/layout/unit), `target_id`, `created_time`

#### 3. Customer & Transactions
*   **`tb_customer`** (Customers)
    *   `id`, `customer_no`, `real_name`, `phone`, `id_card`, `demand_area`, `demand_price`, `demand_layout`, `demand_area_region`, `intention_level`, `sales_id`, `source`
*   **`tb_transaction`** (Deals)
    *   `id`, `transaction_no`, `house_id`, `customer_id`, `sales_id`, `deal_price`, `deposit`, `deposit_time`, `down_payment`, `down_payment_time`, `loan_amount`, `loan_status`, `transfer_time`, `status`, `manager_audit`
*   **`tb_payment`** (Payments)
    *   `id`, `transaction_id`, `payment_type` (1=Deposit, 2=Down, 3=Tail, 4=Fee), `flow_type`, `amount`, `payment_status`, `payment_time`, `payment_method`, `receipt_no`, `proof_url`, `payer_info`, `finance_id`, `remark`
*   **`tb_commission`** (Commissions)
    *   `id`, `transaction_id`, `sales_id`, `commission_rate`, `amount`, `status`, `calculate_time`, `issue_time`, `finance_id`

#### 4. Team & Work
*   **`tb_team`** (Sales Teams)
    *   `id`, `team_name`, `manager_id`, `establish_time`, `team_size`, `performance_target`
*   **`tb_team_member`** (Team Members)
    *   `id`, `team_id`, `user_id`, `join_time`
*   **`tb_work_notice`** (Notices)
    *   `id`, `notice_title`, `notice_content`, `content_type`, `attachments` (JSON), `notice_type`, `biz_type`, `biz_id`, `router_path`, `sender_id`, `sender_name`, `receiver_type` (1=User, 2=Role, 3=Team, 4=All), `receiver_ids` (JSON), `priority`, `read_count`, `total_receivers`, `status`, `withdraw_reason`, `expire_time`, `send_time`, `extra_data`
*   **`tb_notice_read`** (Notice Read Status)
    *   `id`, `notice_id`, `user_id`, `read_time`

## 3. Directory Rules

### 1. General Rules
- **Naming**: Use kebab-case for directories (e.g., `work-notice`) and generally for files in frontend unless they are components (PascalCase).
- **Organization**: Group files by feature/module rather than just technical layer where possible within standard framework constraints.

### 2. Backend Rules (Java/Spring Boot)
- **Controller**: `src/main/java/com/guang/resms/controller` - REST API endpoints.
- **Service**: `src/main/java/com/guang/resms/service` - Business logic interfaces.
- **Service Impl**: `src/main/java/com/guang/resms/service/impl` - Business logic implementation.
- **Mapper**: `src/main/java/com/guang/resms/mapper` - MyBatis interfaces.
- **Entity**: `src/main/java/com/guang/resms/entity` - JPA/MyBatis entities mapping to DB tables.
- **DTO/VO**: Use `dto` for input data and `vo` for view objects/responses.

### 3. Frontend Rules (Vue 3/TypeScript)
- **Views**: `src/views` - Page-level components, grouped by module (e.g., `src/views/work-notice/`).
- **Components**: `src/components` - Global shared components. Module-specific components stay in `views/<module>/components`.
- **API service**: `src/api` - API calls, one file per controller/module (e.g., `notice.ts`).
- **Types**: Define interfaces for API responses and component props (e.g., in `src/api` or `src/types`).

## 4. Code Style

### 1. Java (Backend)
- **Framework**: Spring Boot + MyBatis Plus.
- **Indentation**: 4 spaces.
- **Naming**: camelCase for variables/methods, PascalCase for classes.
- **Annotations**: extensive use of Spring annotations (`@RestController`, `@Autowired`, `@PostMapping`).
- **Response**: Uniform response wrapper `ResponseResult<?>`.
- **Security**: Use `SecurityUtils` for user context.

### 2. Vue/TypeScript (Frontend)
- **Framework**: Vue 3 (Composition API) + TypeScript + Vite.
- **UI Library**: Element Plus.
- **Indentation**: 2 spaces.
- **Script Setup**: Use `<script setup lang="ts">`.
- **Naming**: 
    - Components: PascalCase (e.g., `NoticeForm.vue`).
    - Props/Events: camelCase.
    - Files: kebab-case for non-components (e.g., `index.ts`), PascalCase for components.
- **API Usage**: Import API functions from `@/api`. Use `async/await` for asynchronous operations.
- **State**: Use `ref` and `reactive` for local state.