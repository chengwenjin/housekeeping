import request from '@/utils/request'

/**
 * 获取分类列表
 */
export function getCategoryList(params) {
  return request.get('/admin/categories', { params })
}

/**
 * 获取分类详情
 */
export function getCategoryDetail(id) {
  return request.get(`/admin/categories/${id}`)
}

/**
 * 创建分类
 */
export function createCategory(data) {
  return request.post('/admin/categories', data)
}

/**
 * 更新分类
 */
export function updateCategory(id, data) {
  return request.put(`/admin/categories/${id}`, data)
}

/**
 * 删除分类
 */
export function deleteCategory(id) {
  return request.delete(`/admin/categories/${id}`)
}

/**
 * 更新分类状态
 */
export function updateCategoryStatus(id, status) {
  return request.put(`/admin/categories/${id}/status`, null, {
    params: { status }
  })
}
