package application.domain.ports.out;

import application.domain.enums.NotificationChannel;
import application.domain.models.User;

/** Output port: notify a user (e.g. order delivered, return approved). */
public interface NotificationPort {

    void notifyUser(User user, String message, NotificationChannel channel);
}
