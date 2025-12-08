package com.guang.resms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.entity.House;
import com.guang.resms.entity.dto.HouseQueryDTO;
import com.guang.resms.entity.vo.HouseVO;
import com.guang.resms.entity.vo.NewHouseInfoVO;
import com.guang.resms.entity.vo.SecondHouseInfoVO;
import com.guang.resms.entity.vo.ProjectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房源Mapper接口
 *
 * @author guang
 */
@Mapper
public interface HouseMapper extends BaseMapper<House> {

    /**
     * 分页查询房源列表（带关联信息）
     *
     * @param queryDTO 查询条件
     * @return 房源列表
     */
    List<HouseVO> selectHouseListWithDetails(@Param("query") HouseQueryDTO queryDTO);

    /**
     * 查询房源总数
     *
     * @param queryDTO 查询条件
     * @return 总数
     */
    Long selectHouseCount(@Param("query") HouseQueryDTO queryDTO);

    /**
     * 根据ID查询房源详情（包含关联信息）
     *
     * @param id 房源ID
     * @return 房源详情
     */
    HouseVO selectHouseDetailById(@Param("id") Integer id);

    /**
     * 生成房源编号（自动生成FC+年月日+4位流水号）
     *
     * @return 房源编号
     */
    String generateHouseNo();

    /**
     * 检查房源编号是否唯一
     *
     * @param houseNo 房源编号
     * @param id 房源ID（更新时排除自己）
     * @return 是否唯一
     */
    boolean isHouseNoUnique(@Param("houseNo") String houseNo, @Param("id") Integer id);

    /**
     * 批量更新房源状态
     *
     * @param ids 房源ID列表
     * @param status 新状态
     * @return 更新数量
     */
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") Integer status);

    /**
     * 批量分配房源给销售
     *
     * @param ids 房源ID列表
     * @param salesId 销售ID
     * @return 更新数量
     */
    int batchAssignSales(@Param("ids") List<Integer> ids, @Param("salesId") Integer salesId);

    /**
     * 查询房源图片列表
     *
     * @param houseId 房源ID
     * @return 图片URL列表
     */
    List<String> selectHouseImages(@Param("houseId") Integer houseId);

    /**
     * 查询新房扩展信息
     *
     * @param houseId 房源ID
     * @return 新房扩展信息
     */
    NewHouseInfoVO selectNewHouseInfo(@Param("houseId") Integer houseId);

    /**
     * 查询二手房扩展信息
     *
     * @param houseId 房源ID
     * @return 二手房扩展信息
     */
    SecondHouseInfoVO selectSecondHouseInfo(@Param("houseId") Integer houseId);

    /**
     * 查询项目详细信息
     *
     * @param projectId 项目ID
     * @return 项目详细信息
     */
    ProjectVO selectProjectInfo(@Param("projectId") Integer projectId);
}
