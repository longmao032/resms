<template>
  <div class="house-card">
    <slot name="image">
      <div class="card-image">
        <img :src="house?.img" :alt="house?.name" />
        <div class="image-overlay">
          <el-tag v-if="house?.status" :type="house.statusType" size="small" effect="dark">
            {{ house.status }}
          </el-tag>
        </div>
      </div>
    </slot>
    
    <div class="card-content">
      <slot name="header">
        <div class="card-header">
          <h3 class="house-title">{{ house?.name }}</h3>
          <div class="price-tag">
            <span class="price-number">{{ house?.price }}</span>
            <span class="price-unit">元/平</span>
          </div>
        </div>
      </slot>
      
      <slot name="address">
        <p class="house-address">
          <el-icon><Location /></el-icon>
          {{ house?.address }}
        </p>
      </slot>
      
      <slot name="info">
        <div class="house-type">
          <el-icon><OfficeBuilding /></el-icon>
          <span>{{ house?.roomType }}</span>
        </div>
        
        <div class="open-time">
          <span>开盘时间：{{ house?.openingTime ||'待定'}}</span>
        </div>
      </slot>
      
      <slot name="features">
        <div class="house-features">
          <span 
            v-for="(feature, index) in house?.features" 
            :key="index" 
            class="feature-tag"
          >
            {{ feature }}
          </span>
        </div>
      </slot>
      
      <slot name="actions">
        <div class="card-actions">
          <el-button type="primary" size="small" @click="handleViewDetail">
            查看详情
          </el-button>
          <el-button size="small" text class="favorite-btn" @click="handleFavorite">
            <el-icon><Star /></el-icon>
            收藏
          </el-button>
        </div>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Location, OfficeBuilding, Star } from '@element-plus/icons-vue'

interface House {
  id: number
  name: string
  status: string
  statusType: string
  address: string
  img: string
  roomType: string
  openingTime: string
  features: string[]
  price: string
}

interface Props {
  house: House
}

const props = defineProps<Props>()

const emit = defineEmits<{
  favorite: [id: number]
  'view-detail': [id: number]
}>()

const handleFavorite = () => {
  emit('favorite', props.house.id)
}

const handleViewDetail = () => {
  emit('view-detail', props.house.id)
}
</script>

<style lang="scss" scoped>
.house-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  display: flex;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  .card-image {
    position: relative;
    width: 280px;
    flex-shrink: 0;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }
    
    &:hover img {
      transform: scale(1.05);
    }
    
    .image-overlay {
      position: absolute;
      top: 12px;
      left: 12px;
    }
  }
  
  .card-content {
    flex: 1;
    padding: 20px;
    display: flex;
    flex-direction: column;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12px;
      
      .house-title {
        font-size: 18px;
        font-weight: 600;
        color: #1f2937;
        margin: 0;
        line-height: 1.4;
      }
      
      .price-tag {
        text-align: right;
        
        .price-number {
          font-size: 24px;
          font-weight: 700;
          color: #ff4d4f;
        }
        
        .price-unit {
          font-size: 14px;
          color: #999;
          margin-left: 4px;
        }
      }
    }
    
    .house-address {
      display: flex;
      align-items: flex-start;
      gap: 6px;
      color: #666;
      font-size: 14px;
      margin: 0 0 12px 0;
      line-height: 1.4;
      
      .el-icon {
        color: #999;
        margin-top: 2px;
        flex-shrink: 0;
      }
    }
    
    .house-type {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #666;
      font-size: 14px;
      margin-bottom: 8px;
      
      .el-icon {
        color: #999;
      }
    }
    
    .open-time {
      font-size: 14px;
      color: #666;
      margin-bottom: 12px;
      line-height: 1.4;
    }
    
    .house-features {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-bottom: 16px;
      
      .feature-tag {
        padding: 4px 8px;
        background: #f0f7ff;
        color: #1890ff;
        border-radius: 4px;
        font-size: 12px;
      }
    }
    
    .card-actions {
      margin-top: auto;
      display: flex;
      gap: 12px;
      
      .favorite-btn {
        margin-left: auto;
      }
    }
  }
}

@media (max-width: 768px) {
  .house-card {
    flex-direction: column;
    
    .card-image {
      width: 100%;
      height: 200px;
    }
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 8px;
    
    .price-tag {
      text-align: left !important;
    }
  }
}
</style>