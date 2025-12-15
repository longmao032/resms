package com.guang.resms.module.websocket.config;

import com.guang.resms.module.websocket.interceptor.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置类
 * 
 * @author guang
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor authInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单消息代理,用于向客户端发送消息
        // /topic - 用于广播消息 (一对多)
        // /queue - 用于点对点消息 (一对一)
        registry.enableSimpleBroker("/topic", "/queue");

        // 设置客户端发送消息的前缀
        registry.setApplicationDestinationPrefixes("/app");

        // 设置点对点消息的前缀
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 注册 STOMP 端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 允许跨域
                .addInterceptors(authInterceptor) // 添加认证拦截器
                .withSockJS(); // 启用 SockJS 降级支持
    }
}
