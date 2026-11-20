package application.adapters.notification;

import application.domain.enums.NotificationChannel;
import application.domain.models.User;
import application.domain.ports.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Notification adapter that only logs; a real EMAIL/SMS/PUSH gateway can replace it later. */
@Component
public class LoggingNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationAdapter.class);

    @Override
    public void notifyUser(User user, String message, NotificationChannel channel) {
        log.info("Notification [{}] to user {}: {}", channel, user == null ? "UNKNOWN" : user.getUsername(), message);
    }
}
