package com.guang.resms.module.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.module.notice.entity.WorkNotice;
import com.guang.resms.module.notice.entity.dto.WorkNoticeQueryDTO;
import com.guang.resms.module.notice.entity.vo.WorkNoticeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface WorkNoticeMapper extends BaseMapper<WorkNotice> {

    /**
     * 分页查询当前用户的通知列表
     * SQL逻辑：
     * 1. 筛选状态为已发送(status=1)
     * 2. 匹配接收范围(全员 or 包含用户ID/角色ID/团队ID)
     * 3. 关联查询阅读状态
     */
    IPage<WorkNoticeVO> selectMyNoticePage(Page<WorkNoticeVO> page,
            @Param("dto") WorkNoticeQueryDTO dto,
            @Param("userId") Integer userId,
            @Param("roleId") Integer roleId);

    /**
     * 增加阅读计数
     */
    @Update("UPDATE tb_work_notice SET read_count = read_count + 1 WHERE id = #{id}")
    void incrementReadCount(@Param("id") Integer id);
}
