package com.tesla.teslasocialservice.controller;

import com.tesla.teslasocialservice.entity.ChatMessage;
import com.tesla.teslasocialservice.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GroupChatController {

    private final ChatMessageRepository chatMessageRepository;

    // Inyectamos Redis en lugar de SimpMessagingTemplate
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @GetMapping("/{groupId}/chat/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable Long groupId) {
        return ResponseEntity.ok(chatMessageRepository.findByGroupIdOrderByTimestampAsc(groupId));
    }

    @MessageMapping("/chat/{groupId}/sendMessage")
    public void sendMessage(@DestinationVariable Long groupId, @Payload ChatMessage chatMessage) {
        chatMessage.setGroupId(groupId);

        // 1. Guardamos el mensaje permanentemente en nuestra BD separada
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // 2. Publicamos el mensaje en el canal de REDIS.
        // Esto alertará a TODAS las instancias del servidor para que retransmitan el mensaje
        redisTemplate.convertAndSend(topic.getTopic(), savedMessage);
    }
}