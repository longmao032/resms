package com.guang.resms.module.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.module.customer.entity.ViewRecord;
import com.guang.resms.module.customer.entity.dto.ViewRecordQueryDTO;
import com.guang.resms.module.customer.entity.vo.ViewRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ViewRecordMapper extends BaseMapper<ViewRecord> {

    Page<ViewRecordVO> selectPageVO(Page<ViewRecordVO> page, @Param("query") ViewRecordQueryDTO queryDTO);
}
// Force recompile