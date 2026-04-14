import request from '@/utils/request'

/**
 * 获取系统配置
 */
export function getSystemConfigs() {
  return request.get('/admin/system/configs')
}

/**
 * 保存基础配置
 */
export function saveBasicConfig(data) {
  return request.post('/admin/system/config/basic', data)
}

/**
 * 保存订单配置
 */
export function saveOrderConfig(data) {
  return request.post('/admin/system/config/order', data)
}

/**
 * 保存支付配置
 */
export function savePaymentConfig(data) {
  return request.post('/admin/system/config/payment', data)
}

/**
 * 保存其他配置
 */
export function saveOtherConfig(data) {
  return request.post('/admin/system/config/other', data)
}
