import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export type ThemeMode = 'light' | 'dark'
export type FontSize = 'small' | 'medium' | 'large'

export const useSettingsStore = defineStore('settings', () => {
    // 主题模式
    const themeMode = ref<ThemeMode>('light')

    // 字体大小
    const fontSize = ref<FontSize>('medium')

    // 消息提醒开关
    const notificationEnabled = ref(false)

    // 应用主题
    const applyTheme = () => {
        const html = document.documentElement
        if (themeMode.value === 'dark') {
            html.classList.add('dark')
        } else {
            html.classList.remove('dark')
        }
    }

    // 应用字体大小
    const applyFontSize = () => {
        const html = document.documentElement
        html.classList.remove('font-small', 'font-medium', 'font-large')
        html.classList.add(`font-${fontSize.value}`)
    }

    // 设置主题
    const setTheme = (mode: ThemeMode) => {
        themeMode.value = mode
        applyTheme()
    }

    // 设置字体大小
    const setFontSize = (size: FontSize) => {
        fontSize.value = size
        applyFontSize()
    }

    // 设置消息提醒
    const setNotificationEnabled = (enabled: boolean) => {
        notificationEnabled.value = enabled
    }

    // 初始化设置（应用已保存的设置）
    const initSettings = () => {
        applyTheme()
        applyFontSize()
    }

    return {
        themeMode,
        fontSize,
        notificationEnabled,
        setTheme,
        setFontSize,
        setNotificationEnabled,
        initSettings
    }
}, {
    persist: true
})
