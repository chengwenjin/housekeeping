import request from '@/utils/request'

/**
 * 获取评价列表
 */
export function getReviewList(params) {
  return request.get('/admin/reviews', { params })
}

/**
 * 获取评价详情
 */
export function getReviewDetail(id) {
  return request.get(`/admin/reviews/${id}`)
}

/**
 * 回复评价
 */
export function replyReview(id, content) {
  return request.post(`/admin/reviews/${id}/reply`, null, {
    params: { content }
  })
}

/**
 * 删除评价
 */
export function deleteReview(id) {
  return request.delete(`/admin/reviews/${id}`)
}
