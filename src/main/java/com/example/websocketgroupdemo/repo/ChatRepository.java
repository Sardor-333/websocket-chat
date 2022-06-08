package com.example.websocketgroupdemo.repo;

import com.example.websocketgroupdemo.entity.Chat;
import com.example.websocketgroupdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    boolean existsBySenderIdAndReceiverId(Long sender_id, Long receiver_id);

    Chat getBySenderIdAndReceiverId(Long sender_id, Long receiver_id);

    Optional<Chat> findBySenderAndReceiver(User sender, User receiver);

    List<Chat> findAllBySenderAndReceiver(User sender, User receiver);

    boolean existsBySenderAndReceiver(User sender, User receiver);
}
