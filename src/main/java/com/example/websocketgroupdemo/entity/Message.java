package com.example.websocketgroupdemo.entity;

import com.example.websocketgroupdemo.entity.abs.AbsEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "messages")
public class Message extends AbsEntity {

    @Column(nullable = false, name = "message_text", columnDefinition = "TEXT")
    String text;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false, referencedColumnName = "id")
    User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false, referencedColumnName = "id")
    User receiver;

    @Column(nullable = false)
    UUID chatId;
}
