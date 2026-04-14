import request from '@/utils/request'

/**
 * 获取订单列表
 */
export function getOrderList(params) {
  return request.get('/admin/orders', { params })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id) {
  return request.get(`/admin/orders/${id}`)
}

/**
 * 强制取消订单
 */
export function forceCancelOrder(id, reason) {
  return request.post(`/admin/orders/${id}/force-cancel`, null, {
    params: { reason }
  })
}
