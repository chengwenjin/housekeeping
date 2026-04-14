<template>
  <div class="system-config">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 基础配置 -->
      <el-tab-pane label="基础配置" name="basic">
        <el-form
          ref="basicFormRef"
          :model="basicConfig"
          label-width="150px"
          style="max-width: 800px; padding: 20px;"
        >
          <el-form-item label="小程序名称">
            <el-input v-model="basicConfig.appName" placeholder="请输入小程序名称" />
          </el-form-item>
          
          <el-form-item label="小程序 Logo">
            <el-input v-model="basicConfig.logo" placeholder="请输入 Logo URL" />
          </el-form-item>
          
          <el-form-item label="客服电话">
            <el-input v-model="basicConfig.servicePhone" placeholder="请输入客服电话" />
          </el-form-item>
          
          <el-form-item label="客服微信">
            <el-input v-model="basicConfig.serviceWechat" placeholder="请输入客服微信" />
          </el-form-item>
          
          <el-form-item label="营业时间">
            <el-input v-model="basicConfig.businessHours" placeholder="例如：9:00-21:00" />
          </el-form-item>
          
          <el-form-item label="公告信息">
            <el-input
              v-model="basicConfig.announcement"
              type="textarea"
              :rows="4"
              placeholder="请输入公告内容"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="saveBasicConfig">保存配置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <!-- 订单配置 -->
      <el-tab-pane label="订单配置" name="order">
        <el-form
          ref="orderFormRef"
          :model="orderConfig"
          label-width="150px"
          style="max-width: 800px; padding: 20px;"
        >
          <el-form-item label="自动接单">
            <el-switch v-model="orderConfig.autoAccept" active-text="开启" inactive-text="关闭" />
          </el-form-item>
          
          <el-form-item label="订单有效期 (小时)">
            <el-input-number v-model="orderConfig.expireHours" :min="1" :max="72" />
          </el-form-item>
          
          <el-form-item label="最低订单金额">
            <el-input-number v-model="orderConfig.minAmount" :min="0" :precision="2" />
          </el-form-item>
          
          <el-form-item label="最高订单金额">
            <el-input-number v-model="orderConfig.maxAmount" :min="0" :precision="2" />
          </el-form-item>
          
          <el-form-item label="订单超时取消 (分钟)">
            <el-input-number v-model="orderConfig.timeoutCancel" :min="0" />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="saveOrderConfig">保存配置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <!-- 支付配置 -->
      <el-tab-pane label="支付配置" name="payment">
        <el-form
          ref="paymentFormRef"
          :model="paymentConfig"
          label-width="150px"
          style="max-width: 800px; padding: 20px;"
        >
          <el-form-item label="微信支付">
            <el-switch v-model="paymentConfig.wechatPayEnabled" active-text="开启" inactive-text="关闭" />
          </el-form-item>
          
          <el-form-item label="支付宝支付">
            <el-switch v-model="paymentConfig.alipayEnabled" active-text="开启" inactive-text="关闭" />
          </el-form-item>
          
          <el-form-item label="余额支付">
            <el-switch v-model="paymentConfig.balanceEnabled" active-text="开启" inactive-text="关闭" />
          </el-form-item>
          
          <el-form-item label="默认支付方式">
            <el-select v-model="paymentConfig.defaultMethod">
              <el-option label="微信支付" value="wechat" />
              <el-option label="支付宝" value="alipay" />
              <el-option label="余额" value="balance" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="savePaymentConfig">保存配置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <!-- 其他配置 -->
      <el-tab-pane label="其他配置" name="other">
        <el-form
          ref="otherFormRef"
          :model="otherConfig"
          label-width="150px"
          style="max-width: 800px; padding: 20px;"
        >
          <el-form-item label="用户注册审核">
            <el-switch v-model="otherConfig.requireAudit" active-text="需要" inactive-text="不需要" />
          </el-form-item>
          
          <el-form-item label="服务者认证审核">
            <el-switch v-model="otherConfig.providerAudit" active-text="需要" inactive-text="不需要" />
          </el-form-item>
          
          <el-form-item label="评价审核">
            <el-switch v-model="otherConfig.reviewAudit" active-text="需要" inactive-text="不需要" />
          </el-form-item>
          
          <el-form-item label="系统维护模式">
            <el-switch v-model="otherConfig.maintenanceMode" active-text="开启" inactive-text="关闭" />
          </el-form-item>
          
          <el-form-item label="维护提示">
            <el-input
              v-model="otherConfig.maintenanceMessage"
              type="textarea"
              :rows="3"
              placeholder="系统维护中，请稍后再试..."
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="saveOtherConfig">保存配置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const activeTab = ref('basic')

const basicConfig = reactive({
  appName: '',
  logo: '',
  servicePhone: '',
  serviceWechat: '',
  businessHours: '',
  announcement: ''
})

const orderConfig = reactive({
  autoAccept: false,
  expireHours: 24,
  minAmount: 0,
  maxAmount: 10000,
  timeoutCancel: 30
})

const paymentConfig = reactive({
  wechatPayEnabled: true,
  alipayEnabled: false,
  balanceEnabled: true,
  defaultMethod: 'wechat'
})

const otherConfig = reactive({
  requireAudit: false,
  providerAudit: true,
  reviewAudit: false,
  maintenanceMode: false,
  maintenanceMessage: '系统维护中，请稍后再试...'
})

// 加载配置
const loadConfigs = async () => {
  try {
    const res = await request.get('/admin/system/configs')
    if (res.data) {
      Object.assign(basicConfig, res.data.basic || {})
      Object.assign(orderConfig, res.data.order || {})
      Object.assign(paymentConfig, res.data.payment || {})
      Object.assign(otherConfig, res.data.other || {})
    }
  } catch (error) {
    console.error('加载配置失败', error)
  }
}

// 保存基础配置
const saveBasicConfig = async () => {
  try {
    await request.post('/admin/system/config/basic', basicConfig)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  }
}

// 保存订单配置
const saveOrderConfig = async () => {
  try {
    await request.post('/admin/system/config/order', orderConfig)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  }
}

// 保存支付配置
const savePaymentConfig = async () => {
  try {
    await request.post('/admin/system/config/payment', paymentConfig)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  }
}

// 保存其他配置
const saveOtherConfig = async () => {
  try {
    await request.post('/admin/system/config/other', otherConfig)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadConfigs()
})
</script>

<style scoped>
.system-config {
  padding: 20px;
}

.el-tabs {
  min-height: 600px;
}

.el-form {
  margin-top: 20px;
}
</style>
