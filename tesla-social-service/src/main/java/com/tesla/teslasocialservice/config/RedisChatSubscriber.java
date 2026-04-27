package com.tesla.teslasocialservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tesla.teslasocialservice.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisChatSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    // Configurar ObjectMapper para soportar LocalDateTime de ChatMessage
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 1. Recibimos el mensaje crudo desde Redis
            String payload = new String(message.getBody());

            // 2. Lo convertimos de vuelta a nuestro objeto ChatMessage
            ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);

            // 3. Retransmitimos a los usuarios conectados a ESTA instancia local de WebSockets
            String topicDest = "/topic/group/" + chatMessage.getGroupId();
            messagingTemplate.convertAndSend(topicDest, chatMessage);

            log.debug("Mensaje retransmitido al grupo {} desde Redis", chatMessage.getGroupId());
        } catch (Exception e) {
            log.error("Error al procesar mensaje de Redis Pub/Sub", e);
        }
    }
}