import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getNoticeList } from '@/api/notice'

export const useNoticeStore = defineStore('notice', () => {
    const unreadCount = ref(0)
    const unreadList = ref<any[]>([])
    const timer = ref<any>(null)

    // 获取未读消息
    const fetchUnread = async () => {
        try {
            const res = await getNoticeList({
                pageNum: 1,
                pageSize: 5,
                isRead: 0
            })
            if (res.data) { // Handle inconsistent API wrappers if any
                const data = res.data || {}
                unreadCount.value = data.total || 0
                unreadList.value = data.records || []
            }
        } catch (error) {
            console.error('Failed to fetch unread notices:', error)
        }
    }

    // 开启轮询
    const startPolling = (interval = 30000) => {
        fetchUnread() // 立即执行一次
        if (timer.value) clearInterval(timer.value)
        timer.value = setInterval(() => {
            fetchUnread()
        }, interval)
    }

    // 停止轮询
    const stopPolling = () => {
        if (timer.value) {
            clearInterval(timer.value)
            timer.value = null
        }
    }

    return {
        unreadCount,
        unreadList,
        fetchUnread,
        startPolling,
        stopPolling
    }
})
