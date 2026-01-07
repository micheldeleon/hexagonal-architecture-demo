package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.adapters.out.persistence.jpa.entities.NotificationEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.UserEntity;
import com.example.demo.core.application.service.NotificationSseService;
import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.ports.out.NotificationPort;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
public class NotificationRepository implements NotificationPort {

    @PersistenceContext
    private EntityManager entityManager;

    private final NotificationSseService notificationSseService;

    public NotificationRepository(NotificationSseService notificationSseService) {
        this.notificationSseService = notificationSseService;
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        UserEntity userEntity = entityManager.find(UserEntity.class, notification.getUserId());
        if (userEntity == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        NotificationEntity entity = new NotificationEntity();
        entity.setUser(userEntity);
        entity.setType(notification.getType());
        entity.setTitle(notification.getTitle());
        entity.setMessage(notification.getMessage());
        entity.setRelatedEntityId(notification.getRelatedEntityId());
        entity.setRead(notification.isRead());
        entity.setCreatedAt(notification.getCreatedAt() != null ? notification.getCreatedAt() : new Date());
        entity.setReadAt(notification.getReadAt());

        entityManager.persist(entity);
        entityManager.flush();

        return toModel(entity);
    }

    @Override
    public List<Notification> findByUserId(Long userId) {
        return entityManager
                .createQuery("SELECT n FROM NotificationEntity n WHERE n.user.id = :userId ORDER BY n.createdAt DESC",
                        NotificationEntity.class)
                .setParameter("userId", userId)
                .getResultList()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        return entityManager
                .createQuery(
                        "SELECT n FROM NotificationEntity n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC",
                        NotificationEntity.class)
                .setParameter("userId", userId)
                .getResultList()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Notification findById(Long id) {
        NotificationEntity entity = entityManager.find(NotificationEntity.class, id);
        return entity != null ? toModel(entity) : null;
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        NotificationEntity entity = entityManager.find(NotificationEntity.class, notificationId);
        if (entity != null && !entity.isRead()) {
            entity.setRead(true);
            entity.setReadAt(new Date());
            entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        entityManager
                .createQuery("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.user.id = :userId AND n.isRead = false")
                .setParameter("userId", userId)
                .setParameter("readAt", new Date())
                .executeUpdate();
    }

    @Override
    public int countUnreadByUserId(Long userId) {
        Long count = entityManager
                .createQuery("SELECT COUNT(n) FROM NotificationEntity n WHERE n.user.id = :userId AND n.isRead = false",
                        Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        return count.intValue();
    }

    @Override
    @Transactional
    public void notifyUsersOfTournament(Long tournamentId, String title, String message, NotificationType type) {
        // 1. Obtener usuarios individuales inscritos directamente
        List<Long> individualUserIds = entityManager
                .createQuery(
                        "SELECT tp.userId FROM TournamentParticipantEntity tp " +
                        "WHERE tp.tournamentId = :tournamentId",
                        Long.class)
                .setParameter("tournamentId", tournamentId)
                .getResultList();

        // 2. Obtener creadores de equipos inscritos (usando IDs primitivos)
        List<Long> teamCreatorIds = entityManager
                .createQuery(
                        "SELECT DISTINCT t.creatorId FROM TeamEntity t " +
                        "WHERE t.id IN (SELECT tt.teamId FROM TournamentTeamEntity tt WHERE tt.tournamentId = :tournamentId)",
                        Long.class)
                .setParameter("tournamentId", tournamentId)
                .getResultList();

        // Combinar ambas listas (sin duplicados)
        java.util.Set<Long> allUserIds = new java.util.HashSet<>();
        allUserIds.addAll(individualUserIds);
        allUserIds.addAll(teamCreatorIds);

        // Crear notificación para cada usuario
        for (Long userId : allUserIds) {
            UserEntity user = entityManager.find(UserEntity.class, userId);
            if (user != null) {
                NotificationEntity notification = new NotificationEntity();
                notification.setUser(user);
                notification.setType(type);
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setRelatedEntityId(tournamentId);
                notification.setRead(false);
                notification.setCreatedAt(new Date());
                entityManager.persist(notification);
                entityManager.flush();
                
                // 🔔 Enviar notificación en tiempo real via SSE
                try {
                    Notification notificationModel = toModel(notification);
                    notificationSseService.sendNotificationToUser(userId, notificationModel);
                } catch (Exception e) {
                    System.err.println("Error enviando SSE a usuario " + userId + ": " + e.getMessage());
                }
            }
        }
    }

    @Override
    @Transactional
    public void notifyTeamMembers(Long teamId, String title, String message, NotificationType type) {
        // Obtener SOLO el creador del equipo (usando campo primitivo creatorId)
        Long creatorUserId = entityManager
                .createQuery(
                        "SELECT t.creatorId FROM TeamEntity t WHERE t.id = :teamId",
                        Long.class)
                .setParameter("teamId", teamId)
                .getSingleResult();

        if (creatorUserId != null) {
            UserEntity user = entityManager.find(UserEntity.class, creatorUserId);
            if (user != null) {
                NotificationEntity notification = new NotificationEntity();
                notification.setUser(user);
                notification.setType(type);
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setRelatedEntityId(teamId);
                notification.setRead(false);
                notification.setCreatedAt(new Date());
                entityManager.persist(notification);
                entityManager.flush();
                
                // 🔔 Enviar notificación en tiempo real via SSE
                try {
                    Notification notificationModel = toModel(notification);
                    notificationSseService.sendNotificationToUser(creatorUserId, notificationModel);
                } catch (Exception e) {
                    System.err.println("Error enviando SSE a usuario " + creatorUserId + ": " + e.getMessage());
                }
            }
        }
    }

    private Notification toModel(NotificationEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getUser().getId(),
                entity.getType(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getRelatedEntityId(),
                entity.isRead(),
                entity.getCreatedAt(),
                entity.getReadAt());
    }
}
