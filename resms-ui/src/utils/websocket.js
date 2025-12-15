import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

/**
 * WebSocket 客户端管理类
 * 负责 WebSocket 连接、订阅和消息发送
 */
class WebSocketClient {
    constructor() {
        this.stompClient = null
        this.connected = false
        this.subscriptions = new Map()
        this.reconnectAttempts = 0
        this.maxReconnectAttempts = 5
        this.reconnectDelay = 3000
    }

    /**
     * 连接 WebSocket
     * @param {string} token - JWT Token
     * @returns {Promise}
     */
    connect(token) {
        return new Promise((resolve, reject) => {
            try {
                // 创建 SockJS 连接
                const socket = new SockJS(`/ws?token=${token}`)
                this.stompClient = Stomp.over(socket)

                // 禁用调试日志 (生产环境)
                this.stompClient.debug = null

                // 连接配置
                const connectHeaders = {}

                // 连接成功回调
                const onConnected = (frame) => {
                    console.log('✅ WebSocket 连接成功', frame)
                    this.connected = true
                    this.reconnectAttempts = 0
                    resolve(frame)
                }

                // 连接失败回调
                const onError = (error) => {
                    console.error('❌ WebSocket 连接失败', error)
                    this.connected = false

                    // 尝试重连
                    if (this.reconnectAttempts < this.maxReconnectAttempts) {
                        this.reconnectAttempts++
                        console.log(`🔄 尝试重连 (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`)
                        setTimeout(() => {
                            this.connect(token).then(resolve).catch(reject)
                        }, this.reconnectDelay)
                    } else {
                        reject(error)
                    }
                }

                // 建立连接
                this.stompClient.connect(connectHeaders, onConnected, onError)

            } catch (error) {
                console.error('WebSocket 初始化失败', error)
                reject(error)
            }
        })
    }

    /**
     * 订阅消息
     * @param {string} destination - 订阅地址
     * @param {function} callback - 消息回调函数
     * @param {string} id - 订阅ID (可选)
     * @returns {object} 订阅对象
     */
    subscribe(destination, callback, id = null) {
        if (!this.connected || !this.stompClient) {
            console.error('WebSocket 未连接,无法订阅:', destination)
            return null
        }

        try {
            const subscription = this.stompClient.subscribe(destination, (message) => {
                try {
                    const data = JSON.parse(message.body)
                    callback(data)
                } catch (error) {
                    console.error('解析消息失败', error)
                }
            }, id ? { id } : {})

            // 保存订阅
            const subId = id || destination
            this.subscriptions.set(subId, subscription)

            console.log('📡 订阅成功:', destination)
            return subscription

        } catch (error) {
            console.error('订阅失败', error)
            return null
        }
    }

    /**
     * 取消订阅
     * @param {string} id - 订阅ID
     */
    unsubscribe(id) {
        const subscription = this.subscriptions.get(id)
        if (subscription) {
            subscription.unsubscribe()
            this.subscriptions.delete(id)
            console.log('📴 取消订阅:', id)
        }
    }

    /**
     * 发送消息
     * @param {string} destination - 目标地址
     * @param {object} data - 消息数据
     */
    send(destination, data) {
        if (!this.connected || !this.stompClient) {
            console.error('WebSocket 未连接,无法发送消息')
            return false
        }

        try {
            this.stompClient.send(destination, {}, JSON.stringify(data))
            console.log('📤 发送消息:', destination, data)
            return true
        } catch (error) {
            console.error('发送消息失败', error)
            return false
        }
    }

    /**
     * 断开连接
     */
    disconnect() {
        if (this.stompClient) {
            // 取消所有订阅
            this.subscriptions.forEach((subscription) => {
                subscription.unsubscribe()
            })
            this.subscriptions.clear()

            // 断开连接
            this.stompClient.disconnect(() => {
                console.log('🔌 WebSocket 已断开')
            })

            this.connected = false
            this.stompClient = null
        }
    }

    /**
     * 检查连接状态
     * @returns {boolean}
     */
    isConnected() {
        return this.connected
    }
}

// 导出单例
export default new WebSocketClient()
