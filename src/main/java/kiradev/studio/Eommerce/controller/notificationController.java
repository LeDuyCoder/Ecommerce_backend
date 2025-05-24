package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.dto.NotificationDTO;
import kiradev.studio.Eommerce.entity.Notification;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.NotificationService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notifications")
public class notificationController {
    private final NotificationService notificationService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public notificationController(NotificationService notificationService, UserService userService, JwtUtil jwtUtil) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Streams notifications for a specific user in real-time using Server-Sent Events (SSE).
     *
     * @param mail the email address of the user whose notifications are to be streamed
     * @return a Flux stream of Notification objects for the specified user
     */
    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Notification> streamNotifications(@RequestParam String mail) {
        return notificationService.getNotifications(mail);
    }

    /**
     * Gets a list of notifications for a specific user based on their email address.
     *
     * @param mail the email address of the user to whom the notification is sent
     */
    @GetMapping(value = "/getNotification")
    public List<Notification> getNotification(@RequestParam String mail) {
        return notificationService.getNotificationByMail(mail);
    }


    /**
     * Sends a notification to a specific user.
     *
     * @param notificationDTO the notification data transfer object containing the details of the notification
     */
    @PutMapping(value = "/sendNotification")
    public void updateNotification(
            @RequestBody NotificationDTO notificationDTO) {

        notificationService.updateNotification(notificationDTO);
    }
}
