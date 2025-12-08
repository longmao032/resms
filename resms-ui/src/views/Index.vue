<template>
    <div>
        <Header :current-city="currentCity" @city-change="handleCityChange" />
    </div>

    <div class="home-container">
        <!-- 热门楼盘 -->
        <section class="hot-project-section">
            <div class="carousel-wrapper">
                <h2 class="section-title">热门楼盘 <i class="el-icon-arrow-right"></i></h2>
                <el-carousel :interval="4000" type="" height="300px">
                    <el-carousel-item v-for="(item, index) in hotProjects" :key="index">
                        <img :src="item.image" alt="楼盘宣传图" class="carousel-image" />
                        <div class="carousel-info">
                            <p class="project-name">{{ item.name }}</p>
                            <p class="price">{{ item.price }}元/m²</p>
                            <p class="area">{{ item.area }}㎡</p>
                            <p class="description">{{ item.description }}</p>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
        </section>

        <!-- 主要内容区域：左侧房产信息 + 右侧新房动态 -->
        <div class="main-content-wrapper">
            <div class="main-content">
                <!-- 左侧：房产信息容器（包含精选小区和精选二手房） -->
                <div class="property-container">
                    <!-- 精选楼盘 -->
                    <section class="selected-community-section">
                        <h2 class="section-title">精选楼盘 <a href="#" class="more-link">查看更多</a></h2>
                        <div class="project-list">
                            <div v-for="(item, index) in communities" :key="index" class="project-card">
                                <div class="project-image">
                                    <img :src="item.image" :alt="item.name" />
                                </div>
                                <div class="project-content">
                                    <div class="project-header">
                                        <h3 class="project-name">{{ item.name }}</h3>
                                        <div class="project-price">
                                            <span class="price-number">{{ item.price }}</span>
                                            <span class="price-unit">元/平</span>
                                        </div>
                                    </div>
                                    <div class="project-address">
                                        <i class="el-icon-location"></i>
                                        <span>{{ item.address }}</span>
                                    </div>
                                    <div class="project-layout">
                                        <span>{{ item.layout }}</span>
                                    </div>
                                    <div class="project-opening">
                                        <span>开盘时间：{{ item.openingTime }}</span>
                                    </div>
                                    <div class="project-tags">
                                        <span class="tag-highlight">{{ item.highlight }}</span>
                                        <span class="tag-feature" v-for="(feature, fIndex) in item.features" :key="fIndex">
                                            {{ feature }}
                                        </span>
                                    </div>
                                    <div class="project-actions">
                                        <el-button type="primary" size="small" class="collect-btn">
                                            <i class="el-icon-star-off"></i>
                                            收藏
                                        </el-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <!-- 精选二手房 -->
                    <section class="second-hand-section">
                        <h2 class="section-title">精选二手房 <a href="#" class="more-link">查看更多</a></h2>
                        <div class="project-list">
                            <div v-for="(item, index) in secondHandHouses" :key="index" class="project-card">
                                <div class="project-image">
                                    <img :src="item.image" :alt="item.name" />
                                </div>
                                <div class="project-content">
                                    <div class="project-header">
                                        <h3 class="project-name">{{ item.name }}</h3>
                                        <div class="project-price">
                                            <span class="price-number">{{ item.price }}</span>
                                            <span class="price-unit">元/平</span>
                                        </div>
                                    </div>
                                    <div class="project-address">
                                        <i class="el-icon-location"></i>
                                        <span>{{ item.address }}</span>
                                    </div>
                                    <div class="project-layout">
                                        <span>{{ item.layout }}</span>
                                    </div>
                                    <div class="project-opening">
                                        <span>开盘时间：{{ item.openingTime }}</span>
                                    </div>
                                    <div class="project-tags">
                                        <span class="tag-highlight">{{ item.highlight }}</span>
                                        <span class="tag-feature" v-for="(feature, fIndex) in item.features" :key="fIndex">
                                            {{ feature }}
                                        </span>
                                    </div>
                                    <div class="project-actions">
                                        <el-button type="primary" size="small" class="collect-btn">
                                            <i class="el-icon-star-off"></i>
                                            收藏
                                        </el-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                </div>

                <!-- 右侧：新房动态 -->
                <div class="right-sidebar">
                    <section class="news-section">
                        <h2 class="section-title">新房动态 <i class="el-icon-arrow-right"></i></h2>
                        <ul class="news-list">
                            <li v-for="(item, index) in newsList" :key="index" class="news-item">
                                <a href="#" class="news-link">{{ item }}</a>
                            </li>
                        </ul>
                    </section>
                </div>
            </div>
        </div>

        <!-- 广告位 -->
        <section class="ad-section">
            <!-- 广告内容 -->
        </section>
    </div>
</template>

<script lang="ts" setup>
import { computed, defineComponent } from 'vue'
import Header from '@/components/layout/Header.vue';
import { ref } from 'vue'
import { useHouseProjectStore } from '@/stores/projectsStores'

// 使用 Pinia store
const houseStore = useHouseProjectStore()

// 使用store中的全局城市状态
const currentCity = computed(() => houseStore.currentCity)

// 处理城市变更
const handleCityChange = async (newCity: string) => {
    await houseStore.updateQueryParams({
        city: newCity,
        pageNum: 1
    })
}

// 热门楼盘数据
const hotProjects = [
    {
        image: 'uploads/project/1.jpg',
        name: '五象湖观房 双11清盘秒杀',
        price: '8800',
        area: '35-150㎡',
        description: '秀田小学旁95-150㎡国企现房'
    },
    {
        image: 'uploads/project/1.jpg',
        name: '6栋临溪小高层 最后5套',
        price: '10800',
        area: '83-140㎡',
        description: '五象三中旁 邻万象汇 高拓展红盘'
    }
]

// 新房动态
const newsList = [
    '轨道御澜上城在售建面约95-150㎡现房',
    '轨道云著满栋钜惠95折',
    '轨道御水江湾在售高层/洋房/合院',
    '新希望锦宸府年终让利，多重好礼',
    '荣盛山水园在售120-195㎡合院现房',
    '合景叠翠峰已交付，在售建面81-128㎡',
    '南宁绿地城少有的板式产品在售',
    '悦桂Hill·福昇和庭在售建面75-118㎡',
    '万丰新作新晋双地铁，购房6重好礼',
    '南金海江都壹号在售建面86-107㎡的三至四房'
]

// 精选楼盘数据（根据图片样式调整）
const communities = [
    { 
        image: 'uploads/project/1.jpg', 
        name: '华润凤岭北壹号', 
        address: '南宁青秀区南宁市青秀区凤岭北',
        layout: '3室2厅2卫、4室2厅2卫',
        openingTime: '2024-08-15',
        highlight: '凤岭北高圆',
        features: ['凤岭北高端住宅区', '学区房优势明显', '交通便利'],
        price: '18,000'
    },
    { 
        image: 'uploads/project/2.jpg', 
        name: '建发东盟商务中心', 
        address: '南宁青秀区南宁市青秀区东盟商务区',
        layout: '3室2厅1卫、3室2厅2卫、4室2厅2卫',
        openingTime: '2024-10-01',
        highlight: '东盟商务区高圆',
        features: ['东盟商务区核心', '国际化社区', '交通便利'],
        price: '15,000'
    },
    { 
        image: 'uploads/project/3.jpg', 
        name: '恒大五象国际', 
        address: '南宁良庆区南宁市良庆区五象新区',
        layout: '3室2厅2卫、4室2厅2卫、5室2厅3卫',
        openingTime: '2024-12-01',
        highlight: '五象新区高圆',
        features: ['五象新区CBD', '未来发展潜力巨大', '交通便利'],
        price: '15,000'
    },
    { 
        image: 'uploads/project/4.jpg', 
        name: '万科朝阳中心', 
        address: '南宁兴宁区南宁市兴宁区朝阳广场',
        layout: '3室2厅1卫、3室2厅2卫',
        openingTime: '2024-09-01',
        highlight: '朝阳高圆',
        features: ['市中心黄金地段', '商业配套完善', '交通便利'],
        price: '12,000'
    }
]

// 精选二手房数据（根据图片样式调整）
const secondHandHouses = [
    { 
        image: 'uploads/project/5.jpg', 
        name: '荣和悦澜山', 
        address: '南宁青秀区凤岭北',
        layout: '3室2厅 140㎡',
        openingTime: '2021年',
        highlight: '人气好房',
        features: ['学区房', '满五唯一', '精装修'],
        price: '12,059'
    },
    { 
        image: 'uploads/project/6.jpg', 
        name: '绿地中央广场', 
        address: '南宁青秀区东盟大道',
        layout: '4室2厅 164.76㎡',
        openingTime: '2021年',
        highlight: '人气好房',
        features: ['CBD', '视野好', '豪华装修'],
        price: '14,284'
    },
    { 
        image: 'uploads/project/7.jpg', 
        name: '万科城(南区)', 
        address: '南宁青秀区民族路',
        layout: '3室2厅 85.19㎡',
        openingTime: '2022年',
        highlight: '店长推荐',
        features: ['小户型', '总价低', '品牌物业'],
        price: '9,823'
    },
    { 
        image: 'uploads/project/8.jpg', 
        name: '荣和大地_明玥轩洋房', 
        address: '南宁青秀区凤岭北',
        layout: '4室2厅 140.95㎡',
        openingTime: '2019年',
        highlight: '店长推荐',
        features: ['洋房', '稀缺房源', '改善型'],
        price: '14,048'
    }
]
</script>

<style lang="scss" scoped>
.home-container {
    padding: 20px;
    font-family: 'Microsoft YaHei', sans-serif;

    .hot-project-section {
        max-width: 1200px;
        margin: 0 auto 40px;
        
        .carousel-wrapper {
            width: 100%;
        }
    }

    .main-content-wrapper {
        max-width: 1200px;
        margin: 0 auto 40px;
    }

    .main-content {
        display: flex;
        gap: 30px;
        width: 100%;
        
        // 左侧房产信息容器
        .property-container {
            flex: 3;
            display: flex;
            flex-direction: column;
            gap: 40px;
            
            .selected-community-section,
            .second-hand-section {
                .project-list {
                    display: flex;
                    flex-direction: column;
                    gap: 20px;
                }

                .project-card {
                    display: flex;
                    background: white;
                    border-radius: 12px;
                    overflow: hidden;
                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
                    transition: all 0.3s ease;
                    
                    &:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
                    }

                    .project-image {
                        width: 280px;
                        flex-shrink: 0;
                        
                        img {
                            width: 100%;
                            height: 100%;
                            object-fit: cover;
                        }
                    }

                    .project-content {
                        flex: 1;
                        padding: 20px;
                        display: flex;
                        flex-direction: column;
                        
                        .project-header {
                            display: flex;
                            justify-content: space-between;
                            align-items: flex-start;
                            margin-bottom: 12px;
                            
                            .project-name {
                                font-size: 18px;
                                font-weight: 600;
                                color: #1f2937;
                                margin: 0;
                                line-height: 1.4;
                            }
                            
                            .project-price {
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
                        
                        .project-address {
                            display: flex;
                            align-items: flex-start;
                            gap: 6px;
                            color: #666;
                            font-size: 14px;
                            margin-bottom: 8px;
                            line-height: 1.4;
                            
                            .el-icon-location {
                                color: #999;
                                margin-top: 2px;
                                flex-shrink: 0;
                            }
                        }
                        
                        .project-layout {
                            font-size: 14px;
                            color: #666;
                            margin-bottom: 8px;
                            line-height: 1.4;
                        }
                        
                        .project-opening {
                            font-size: 14px;
                            color: #666;
                            margin-bottom: 12px;
                            line-height: 1.4;
                        }
                        
                        .project-tags {
                            display: flex;
                            flex-wrap: wrap;
                            gap: 8px;
                            margin-bottom: 16px;
                            
                            .tag-highlight {
                                padding: 4px 8px;
                                background: #fff2e8;
                                color: #fa541c;
                                border-radius: 4px;
                                font-size: 12px;
                                font-weight: 500;
                            }
                            
                            .tag-feature {
                                padding: 4px 8px;
                                background: #f0f7ff;
                                color: #1890ff;
                                border-radius: 4px;
                                font-size: 12px;
                            }
                        }
                        
                        .project-actions {
                            margin-top: auto;
                            
                            .collect-btn {
                                .el-icon-star-off {
                                    margin-right: 4px;
                                }
                            }
                        }
                    }
                }
            }
        }

        .right-sidebar {
            flex: 1;
            min-width: 300px;
        }
    }

    .section-title {
        font-size: 18px;
        color: #333;
        margin-bottom: 20px;
        display: flex;
        align-items: center;

        .el-icon-arrow-right {
            margin-left: 5px;
            font-size: 12px;
        }

        .more-link {
            color: #409eff;
            font-size: 14px;
            margin-left: auto;
            text-decoration: none;
            
            &:hover {
                text-decoration: underline;
            }
        }
    }

    .carousel-wrapper {
        .carousel-image {
            width: 100%;
            height: 80%;
            object-fit: cover;
            border-radius: 8px;
        }

        .carousel-info {
            background: rgba(0, 0, 0, 0.7);
            color: white;
            padding: 10px;
            text-align: center;
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            border-radius: 0 0 8px 8px;

            .project-name {
                font-size: 16px;
                margin: 0;
            }

            .price {
                font-size: 14px;
                margin: 5px 0;
            }

            .area {
                font-size: 12px;
                color: #ccc;
            }

            .description {
                font-size: 12px;
                color: #fff;
                margin-top: 5px;
            }
        }
    }

    .news-section {
        background: #fff;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);

        .news-list {
            list-style: none;
            padding: 0;
            margin: 0;

            .news-item {
                padding: 12px 0;
                border-bottom: 1px solid #f0f0f0;

                &:last-child {
                    border-bottom: none;
                }

                .news-link {
                    color: #333;
                    text-decoration: none;
                    font-size: 14px;
                    transition: color 0.3s;
                    display: block;
                    line-height: 1.4;

                    &:hover {
                        color: #409eff;
                    }
                }
            }
        }
    }

    .ad-section {
        margin: 40px 0;
        text-align: center;
    }

    @media (max-width: 768px) {
        .main-content {
            flex-direction: column;
            
            .property-container,
            .right-sidebar {
                width: 100%;
            }
            
            .project-card {
                flex-direction: column !important;
                
                .project-image {
                    width: 100% !important;
                    height: 200px;
                }
                
                .project-header {
                    flex-direction: column;
                    align-items: flex-start !important;
                    gap: 8px;
                    
                    .project-price {
                        text-align: left !important;
                    }
                }
            }
        }
        
        .hot-project-section,
        .carousel-wrapper {
            width: 95%;
        }
    }
}
</style>