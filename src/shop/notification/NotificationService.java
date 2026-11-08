package shop.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    // T2.5: Menyimpan List<NotificationChannel> (pemrograman terhadap interface)
    private final List<NotificationChannel> channels = new ArrayList<>();

    public void register(NotificationChannel channel) {
        channels.add(channel);
    }

    public int broadcast(CustomerContact contact, String message) {
        int count = 0;
        for (NotificationChannel channel : channels) {
            if (channel.send(contact, message)) {
                count++;
            }
        }
        return count;
    }
}