package com.example.websocketgroupdemo.entity;

import com.example.websocketgroupdemo.entity.abs.AbsEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chats")
public class Chat extends AbsEntity {

    @Column(nullable = false)
    UUID chatId;

    @OneToOne
    @JoinColumn(name = "sender_id", nullable = false, referencedColumnName = "id")
    User sender;

    @OneToOne
    @JoinColumn(name = "receiver_id", nullable = false, referencedColumnName = "id")
    User receiver;

    public Chat(UUID chatId, User sender, User receiver) {
        this.chatId = chatId;
        this.sender = sender;
        this.receiver = receiver;
    }
}
