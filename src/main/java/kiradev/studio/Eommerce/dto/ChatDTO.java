package kiradev.studio.Eommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kiradev.studio.Eommerce.entity.Conversation;
import kiradev.studio.Eommerce.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class ChatDTO {
    private UUID id;
    private UUID userSender;
    private UUID userReceiver;
    private String message;
    private String createdAt;
}
