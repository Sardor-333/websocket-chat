package com.example.websocketgroupdemo.repo;

import com.example.websocketgroupdemo.entity.Message;
import com.example.websocketgroupdemo.projection.MessageProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(
            nativeQuery = true,
            value = """
                    select m.id as id,
                           m.message_text as messageText,
                           m.sender_id as senderId,
                           m.receiver_id as receiverId,
                           to_char(m.created_at, 'Day DD Mon, YYYY | HH24:MI') as sentAt
                    from messages m
                    where m.id = :messageId
                    """
    )
    MessageProjection getMessageProjection(@Param("messageId") Long messageId);

    @Query(
            nativeQuery = true,
            value = """
                    select m.id           as id,
                           m.message_text as messageText,
                           m.sender_id    as senderId,
                           m.receiver_id  as receiverId,
                           to_char(m.created_at, 'Day DD Mon, YYYY | HH24:MI') as sentAt
                    from messages m
                             join chats c on m.chat_id = c.chat_id
                    where c.sender_id = :senderId
                      and c.receiver_id = :receiverId
                    order by m.created_at
                    """
    )
    List<MessageProjection> getChatMessages(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    @Query(
            nativeQuery = true,
            value = """
                    select m.id                                      as id,
                           m.message_text                            as messageText,
                           m.sender_id                               as senderId,
                           m.receiver_id                             as receiverId,
                           to_char(m.created_at, 'Dy DD Mon, YYYY | HH24:MI') as sentAt
                    from messages m
                    where m.chat_id = :chatId
                    order by m.created_at
                    """
    )
    List<MessageProjection> getAllMessagesByChatId(@Param("chatId") UUID chatId);
}
