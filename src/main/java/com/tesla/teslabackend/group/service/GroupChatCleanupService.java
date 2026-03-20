package com.tesla.teslabackend.group.service;

import com.tesla.teslabackend.group.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GroupChatCleanupService {

    private final ChatMessageRepository chatMessageRepository;

    // Se ejecuta todos los días a las 3:00 AM (hora del servidor)
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanOldChatMessages() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        chatMessageRepository.deleteOlderThan(oneWeekAgo);
        System.out.println("🧹 Limpieza del Chat completada: Mensajes anteriores a " + oneWeekAgo + " eliminados.");
    }
}