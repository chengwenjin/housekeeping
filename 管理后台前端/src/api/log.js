import request from '@/utils/request'

/**
 * 获取操作日志列表
 */
export function getOperationLogs(params) {
  return request.get('/admin/logs', { params })
}

/**
 * 获取操作日志详情
 */
export function getOperationLogDetail(id) {
  return request.get(`/admin/logs/${id}`)
}
