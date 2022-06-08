package com.example.websocketgroupdemo.config.websocket;

import com.example.websocketgroupdemo.entity.User;
import com.example.websocketgroupdemo.projection.UserProjection;
import com.example.websocketgroupdemo.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WebsocketEventHandler {

    private final UserRepo userRepo;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Logger log = LoggerFactory.getLogger(WebsocketEventHandler.class);

    @EventListener
    public void handleWebsocketConnectionEvent(SessionConnectedEvent event) {
        log.error("=== HANDLED WEBSOCKET CONNECTION EVENT === ");
        String username = Objects.requireNonNull(event.getUser()).getName();
        log.error("=== USER " + username + " CONNECTED ===");

        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isPresent()) {
            // MAKE USER ONLINE
            User user = optionalUser.get();
            if (!user.isOnline()) {
                user.setOnline(true);
                user = userRepo.save(user);

                sendUserActivity(user);
            }
        }
    }

    @EventListener
    public void handleWebsocketDisconnectionEvent(SessionDisconnectEvent event) {
        log.warn("=== HANDLED WEBSOCKET DISCONNECTION EVENT ===");
        String username = Objects.requireNonNull(event.getUser()).getName();
        log.warn("=== USER " + username + " CONNECTED ===");

        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isPresent()) {
            // MAKE USER OFFLINE
            User user = optionalUser.get();
            if (user.isOnline()) {
                user.setOnline(false);
                user = userRepo.save(user);
                sendUserActivity(user);
            }
        }
    }

    private void sendUserActivity(User user) {
        UserProjection projection = userRepo.getUserProjectionById(user.getId());

        // USER KIMNING CONTACTLARIDA BO'LSA HAR BIRIGA YUBORISH
        List<UserProjection> contacts = userRepo.getMyContacts(user.getId());
        for (UserProjection contact : contacts) {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(contact.getId()),
                    "/contacts",
                    projection
            );
        }
    }
}
