package com.guang.resms.module.house.service;

import com.guang.resms.module.house.entity.dto.HouseDTO;
import com.guang.resms.module.house.entity.dto.HouseQueryDTO;
import com.guang.resms.module.house.entity.vo.HouseVO;

import java.util.List;
import java.util.Map;

/**
 * 房源服务接口
 *
 * @author guang
 */
public interface HouseService {

    /**
     * 分页查询房源列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Map<String, Object> pageList(HouseQueryDTO queryDTO);

    /**
     * 根据ID查询房源详情
     *
     * @param id 房源ID
     * @return 房源详情
     */
    HouseVO getHouseById(Integer id);

    /**
     * 新增房源
     *
     * @param houseDTO 房源信息
     * @return 是否成功
     */
    boolean addHouse(HouseDTO houseDTO);

    /**
     * 更新房源
     *
     * @param houseDTO 房源信息
     * @return 是否成功
     */
    boolean updateHouse(HouseDTO houseDTO);

    /**
     * 删除房源
     *
     * @param id 房源ID
     * @return 是否成功
     */
    boolean deleteHouse(Integer id);

    /**
     * 批量删除房源
     *
     * @param ids 房源ID列表
     * @return 删除数量
     */
    int batchDeleteHouse(List<Integer> ids);

    /**
     * 更新房源状态
     *
     * @param id 房源ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateStatus(Integer id, Integer status);

    /**
     * 批量更新房源状态
     *
     * @param ids 房源ID列表
     * @param status 新状态
     * @return 更新数量
     */
    int batchUpdateStatus(List<Integer> ids, Integer status);

    /**
     * 分配房源给销售
     *
     * @param id 房源ID
     * @param salesId 销售ID
     * @return 是否成功
     */
    boolean assignSales(Integer id, Integer salesId);

    /**
     * 批量分配房源给销售
     *
     * @param ids 房源ID列表
     * @param salesId 销售ID
     * @return 分配数量
     */
    int batchAssignSales(List<Integer> ids, Integer salesId);

    /**
     * 审核房源
     *
     * @param id 房源ID
     * @param approved 是否通过
     * @param reason 审核原因
     * @return 是否成功
     */
    boolean auditHouse(Integer id, Boolean approved, String reason);
}
