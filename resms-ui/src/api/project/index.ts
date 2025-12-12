import request from '@/utils/request'

/**
 * 获取所有项目列表（用于下拉选择）
 */
export const getProjectList = () => {
  return request.get('/projects/list')
}

/**
 * 分页查询项目列表
 */
export const getProjectPage = (data: any) => {
  return request.post('/projects/page', data)
}

/**
 * 新增项目
 */
export const addProject = (data: FormData) => {
  return request.post('/projects/add', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 根据ID查询项目详情
 */
export const getProjectById = (id: number) => {
  return request.get(`/projects/${id}`)
}

/**
 * 更新项目
 */
export const updateProject = (data: FormData) => {
  return request.post('/projects/update', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除项目
 */
export const deleteProject = (id: number) => {
  return request.delete(`/projects/${id}`)
}

/**
 * 审核项目
 */
export const auditProject = (id: number, status: number, reason?: string) => {
  return request.post(`/projects/audit/${id}`, null, {
    params: {
      status,
      reason
    }
  })
}

