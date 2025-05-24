package kiradev.studio.Eommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class NotificationDTO {
    private UUID id;
    private String tited;
    private String msg;
    private String mail;
}
