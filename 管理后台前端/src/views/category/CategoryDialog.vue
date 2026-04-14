<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑分类' : '新增分类'"
    width="600px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-form-item label="分类名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入分类名称" />
      </el-form-item>
      
      <el-form-item label="图标" prop="icon">
        <el-input v-model="formData.icon" placeholder="请输入图标名称（如：House）" />
        <div style="color: #999; font-size: 12px; margin-top: 5px;">
          使用 Element Plus 图标名称，留空则不显示
        </div>
      </el-form-item>
      
      <el-form-item label="排序" prop="sortOrder">
        <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        <div style="color: #999; font-size: 12px; margin-top: 5px;">
          数值越小越靠前
        </div>
      </el-form-item>
      
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入分类描述"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const props = defineProps({
  visible: Boolean,
  categoryData: Object
})

const emit = defineEmits(['update:visible', 'refresh'])

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const formData = reactive({
  id: null,
  name: '',
  icon: '',
  sortOrder: 100,
  status: 1,
  description: ''
})

const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val) {
    resetForm()
    if (props.categoryData) {
      isEdit.value = true
      Object.assign(formData, props.categoryData)
    } else {
      isEdit.value = false
      formData.sortOrder = 100
      formData.status = 1
    }
  }
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

const resetForm = () => {
  formData.id = null
  formData.name = ''
  formData.icon = ''
  formData.sortOrder = 100
  formData.status = 1
  formData.description = ''
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true
    
    if (isEdit.value) {
      await request.put(`/admin/categories/${formData.id}`, formData)
      ElMessage.success('更新成功')
    } else {
      await request.post('/admin/categories', formData)
      ElMessage.success('创建成功')
    }
    
    emit('refresh')
    dialogVisible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败', error)
    }
  } finally {
    submitting.value = false
  }
}

const handleClosed = () => {
  resetForm()
}
</script>

<style scoped>
.el-form {
  padding: 10px 0;
}
</style>
