@Entity
@Table(name = "user_notifications")
public class UserNotification {

    @EmbeddedId
    private UserNotificationId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("notificationId")
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @Column(name = "is_read")
    private boolean read;

    // getters and setters
}
