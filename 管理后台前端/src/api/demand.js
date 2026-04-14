import request from '@/utils/request'

/**
 * 获取需求列表
 */
export function getDemandList(params) {
  return request.get('/admin/demands', { params })
}

/**
 * 获取需求详情
 */
export function getDemandDetail(id) {
  return request.get(`/admin/demands/${id}`)
}

/**
 * 下架需求
 */
export function offlineDemand(id) {
  return request.put(`/admin/demands/${id}/offline`)
}

/**
 * 删除需求
 */
export function deleteDemand(id) {
  return request.delete(`/admin/demands/${id}`)
}
