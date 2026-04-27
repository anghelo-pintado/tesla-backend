package com.tesla.teslasocialservice.config;

import com.tesla.teslasocialservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class GroupWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-group-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    // AÑADIMOS EL INTERCEPTOR PARA VALIDAR EL JWT EN WEBSOCKETS
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // Solo validamos cuando el cliente intenta conectarse
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new IllegalArgumentException("No autorizado: Falta el token JWT");
                    }

                    String token = authHeader.substring(7);
                    if (!jwtUtil.isTokenValid(token)) {
                        throw new IllegalArgumentException("No autorizado: Token inválido o expirado");
                    }

                    // Puedes guardar el ID del usuario en la sesión del socket
                    Long userId = jwtUtil.extractUserId(token);
                    accessor.getSessionAttributes().put("userId", userId);
                }
                return message;
            }
        });
    }
}