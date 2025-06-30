package kiradev.studio.Eommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "userOne", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private User userOne;

    @ManyToOne
    @JoinColumn(name = "userTwo", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private User userTwo;

    private String lastMessage;

    private int unreadCountMessagesOfUserOne;
    private int unreadCountMessagesOfUserTwo;
}
