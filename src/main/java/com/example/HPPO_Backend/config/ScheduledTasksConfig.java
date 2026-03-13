package com.example.HPPO_Backend.config;

import com.example.HPPO_Backend.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTasksConfig {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksConfig.class);
    
    private final CartService cartService;

    public ScheduledTasksConfig(CartService cartService) {
        this.cartService = cartService;
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void cleanExpiredCarts() {
        try {
            logger.info("Iniciando limpieza de carritos expirados...");
            int deletedCount = cartService.deleteExpiredCarts();
            logger.info("Limpieza completada. Carritos expirados eliminados: {}", deletedCount);
        } catch (Exception e) {
            logger.error("Error durante la limpieza de carritos expirados", e);
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void dailyCartCleanup() {
        try {
            logger.info("Iniciando limpieza diaria de carritos expirados...");
            int deletedCount = cartService.deleteExpiredCarts();
            logger.info("Limpieza diaria completada. Carritos expirados eliminados: {}", deletedCount);
        } catch (Exception e) {
            logger.error("Error durante la limpieza diaria de carritos expirados", e);
        }
    }
}
