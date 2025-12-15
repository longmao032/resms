/**
 * 图片处理工具类
 */

/**
 * 获取图片完整访问地址
 * 统一处理图片路径，解决前端硬编码域名和路径格式不一致的问题
 * 利用 Vite 的 proxy 配置 ('/uploads') 将请求代理到后端
 * 
 * @param url 图片路径 (可以是相对路径、绝对路径或完整URL)
 * @returns 完整的图片访问地址
 */
export const getImageUrl = (url: string | undefined | null): string => {
    if (!url) return ''

    // 1. 如果是完整 URL (http/https 开头)，直接返回
    if (url.startsWith('http://') || url.startsWith('https://')) return url

    // 2. 如果是 base64 数据，直接返回
    if (url.startsWith('data:image')) return url

    // 3. 处理路径标准化
    let path = url

    // Windows路径分隔符处理
    path = path.replace(/\\/g, '/')

    // 移除开头的 ./
    if (path.startsWith('./')) {
        path = path.slice(2)
    }

    // 确保以 / 开头
    if (!path.startsWith('/')) {
        path = '/' + path
    }

    // 4. 如果路径已经包含 /uploads 前缀，优先将其转换为可访问的绝对地址
    const resolveWithHost = (p: string) => {
        // 优先使用 Vite 环境变量中可能配置的后端地址（例如 VITE_API_HOST）
        const env = import.meta.env as Record<string, any>
        const apiHost = (env.VITE_API_HOST || env.VITE_BASE_API || env.VITE_APP_API_URL || env.VITE_BACKEND_URL) as string | undefined

        // 如果在构建时或运行时无法找到 host，回退到当前页面的 origin
        if (apiHost && apiHost.length > 0) {
            const host = apiHost.replace(/\/+$/g, '')
            return `${host}${p}`
        }

        if (typeof window !== 'undefined' && window.location && window.location.origin) {
            return `${window.location.origin}${p}`
        }

        // 最后回退到相对路径（SSR 或测试场景）
        return p
    }

    if (path === '/uploads' || path.startsWith('/uploads/')) {
        return resolveWithHost(path)
    }

    // 5. 如果没有 /uploads 前缀，则拼接并返回可访问地址
    const uploadsPath = `/uploads${path}`
    return resolveWithHost(uploadsPath)
}
