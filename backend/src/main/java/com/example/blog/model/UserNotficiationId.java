@Embeddable
public class UserNotificationId implements Serializable {

    private Long userId;
    private Long notificationId;

    // equals() and hashCode() are required!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserNotificationId)) return false;
        UserNotificationId that = (UserNotificationId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, notificationId);
    }

    // getters and setters
}


/* 
// create a notification
Notification notif = new Notification();
notif.setAuthorId(42L);
notif.setPostCreatedAt(LocalDateTime.now());

// assign it to a user
UserNotification link = new UserNotification();
link.setUser(user);
link.setNotification(notif);
link.setRead(false);

// set IDs
link.setId(new UserNotificationId(user.getId(), notif.getId()));

// wire relationships
user.getNotifications().add(link);
notif.getUserNotifications().add(link);


------------------------------

User currentUser = userRepository.findById(userId).orElseThrow();
Set<User> following = currentUser.getFollowing();

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.authorId IN :authorIds ORDER BY n.postCreatedAt DESC")
    List<Notification> findNotificationsByAuthors(@Param("authorIds") Set<Long> authorIds);
}

then :

Set<Long> authorIds = currentUser.getFollowing()
                                 .stream()
                                 .map(User::getId)
                                 .collect(Collectors.toSet());

List<Notification> notifs = notificationRepository.findNotificationsByAuthors(authorIds);


****************************************
Option 2: If you also want per-user “read/unread” state

Since the “read” flag is stored in UserNotification, you can join that table too:

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    @Query("""
        SELECT un
        FROM UserNotification un
        JOIN un.notification n
        WHERE n.authorId IN :authorIds
          AND un.user = :currentUser
        ORDER BY n.postCreatedAt DESC
    """)
    List<UserNotification> findFeedForUser(
        @Param("currentUser") User currentUser,
        @Param("authorIds") Set<Long> authorIds
    );
}


Then you can do:

List<UserNotification> feed = userNotificationRepository
    .findFeedForUser(currentUser, authorIds);


This returns full records like:

notification_id | author_id | post_created_at | is_read
*/