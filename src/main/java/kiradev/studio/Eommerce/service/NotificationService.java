package kiradev.studio.Eommerce.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kiradev.studio.Eommerce.dto.NotificationDTO;
import kiradev.studio.Eommerce.entity.Notification;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.NotificationRepository;
import kiradev.studio.Eommerce.repository.UserRepository;
import kiradev.studio.Eommerce.service.Interface.INotificationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private final Map<String, FluxSink<Notification>> userSinks = new ConcurrentHashMap<>();

    /**
     * Lấy danh sách thông báo cho người dùng theo email
     *
     * @param mail email của người dùng
     * @return danh sách thông báo
     */
    public Flux<Notification> getNotifications(String mail) {
        User user = userRepository.findByemail(mail).orElseThrow();

        return Flux.create(sink -> {
            userSinks.put(user.getID().toString(), sink);

            Flux<Notification> notificationSource = getNotificationSource(mail);

            // Khi có thông báo mới từ nguồn, push vào sink
            notificationSource.subscribe(
                    sink::next, // gửi thông báo
                    sink::error, // gửi lỗi nếu có
                    sink::complete // hoàn tất khi hết thông báo
            );

            // Xử lý khi kết nối bị hủy
            sink.onDispose(() -> userSinks.remove(user.getID()));
        }, FluxSink.OverflowStrategy.LATEST);
    }

    /**
     * Lưu thông báo vào cơ sở dữ liệu và gửi đến người dùng nếu họ đang subscribe
     *
     * @param dto thông báo cần lưu
     */
    @Transactional
    public void updateNotification(NotificationDTO dto) {
        Notification notification;

        if (dto.getId() != null && notificationRepository.existsById(dto.getId())) {
            // Trường hợp cập nhật
            notification = notificationRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        } else {
            // Trường hợp tạo mới
            notification = new Notification();
            notification.setMail(dto.getMail()); // nhớ set các field cần thiết nếu là bản mới
        }

        // Cập nhật dữ liệu
        notification.setTitle(dto.getTited());
        notification.setContent(dto.getMsg());
        notification.setCreatedAt(Instant.now().toString());
        Notification savedNotification = notificationRepository.save(notification);

        // Gửi notification nếu user đang subscribe
        String userId = userRepository.findByemail(savedNotification.getMail())
                .orElseThrow()
                .getID()
                .toString();

        FluxSink<Notification> sink = userSinks.get(userId);
        if (sink != null) {
            sink.next(savedNotification);
        }
    }


    /**
     * Giả lập nguồn dữ liệu thông báo từ cơ sở dữ liệu hoặc API
     *
     * @param mail email của người dùng
     * @return Flux<Notification> nguồn dữ liệu thông báo
     */
    private Flux<Notification> getNotificationSource(String mail) {
        // Ví dụ giả lập nguồn dữ liệu từ cơ sở dữ liệu hoặc API
        return Flux.create(sink -> {});
    }

    /**
     * Constructor for NotificationService.
     *
     * @param notificationRepository the notification repository
     * @param userRepository         the user repository
     */
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lấy danh sách thông báo theo email
     *
     * @param mail email của người dùng
     * @return danh sách thông báo
     */
    public List<Notification> getNotificationByMail(String mail) {
        return notificationRepository.findBymail(mail);
    }

    /**
     * Lấy danh sách thông báo theo email
     *
     * @param email email của người dùng
     * @return danh sách thông báo
     */
    public List<Notification> getNotificationByEmail(String email) {
        return notificationRepository.findBymail(email);

    }

    /**
     * Lưu thông báo vào cơ sở dữ liệu
     *
     * @param titled        tiêu đề thông báo
     * @param message       nội dung thông báo
     * @param recipientEmail email người nhận
     */
    @Override
    public void sendNotification(String titled, String message, String recipientEmail) {
        Notification notification = new Notification();
        notification.setTitle(titled);
        notification.setContent(message);
        notification.setMail(recipientEmail);
        notificationRepository.save(notification);
    }
}
