package com.eryonix.model;

import jakarta.persistence.*; // or javax.persistence.* depending on your version
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    private String content;

    private LocalDateTime timestamp;

    private boolean isRead = false;

    private String sharedMediaUrl;


    private String sharedMediaType;

    // Getters and Setters...
}
