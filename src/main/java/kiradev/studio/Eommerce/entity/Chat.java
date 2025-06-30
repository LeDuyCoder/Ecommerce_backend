package kiradev.studio.Eommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "userSender", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private User userSender;

    @ManyToOne
    @JoinColumn(name = "userReceiver", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private User userReceiver;

    @Column(nullable = false)
    private String message;

    private String createdAt;

    @ManyToOne
    @JoinColumn(name = "conversationId", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Conversation conversation;
}
