package com.eryonix.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User recipient;

    private Long mediaId;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    private String message;

    private LocalDateTime timestamp;

    public enum MediaType {
        POST, VIDEO
    }
}
