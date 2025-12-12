package com.guang.resms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.ViewRecord;
import com.guang.resms.entity.dto.ViewRecordQueryDTO;
import com.guang.resms.entity.vo.ViewRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ViewRecordMapper extends BaseMapper<ViewRecord> {

    Page<ViewRecordVO> selectPageVO(Page<ViewRecordVO> page, @Param("query") ViewRecordQueryDTO queryDTO);
}
// Force recompile