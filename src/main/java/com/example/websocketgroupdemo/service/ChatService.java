package com.example.websocketgroupdemo.service;

import com.example.websocketgroupdemo.entity.Chat;
import com.example.websocketgroupdemo.entity.Message;
import com.example.websocketgroupdemo.entity.User;
import com.example.websocketgroupdemo.payload.req.MessageDto;
import com.example.websocketgroupdemo.payload.resp.ApiResponse;
import com.example.websocketgroupdemo.projection.MessageProjection;
import com.example.websocketgroupdemo.projection.UserProjection;
import com.example.websocketgroupdemo.repo.ChatRepository;
import com.example.websocketgroupdemo.repo.MessageRepository;
import com.example.websocketgroupdemo.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.example.websocketgroupdemo.payload.resp.ApiResponse.response;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepo userRepo;
    private final MessageRepository messageRepository;

    public MessageProjection sendMessage(MessageDto messageDto) {
        User sender = userRepo
                .findById(messageDto.getSenderId())
                .orElseThrow(() -> new IllegalStateException("User with ID: " + messageDto.getSenderId() + " not found!"));

        User receiver = userRepo
                .findById(messageDto.getReceiverId())
                .orElseThrow(() -> new IllegalStateException("User with ID: " + messageDto.getReceiverId() + " not found!"));

        if (sender.equals(receiver)) {
            Chat chat = chatRepository.findBySenderAndReceiver(sender, receiver).orElse(null);
            if (chat == null) {
                chat = chatRepository.save(new Chat(UUID.randomUUID(), sender, receiver));
            }
            Message message = messageRepository.save(new Message(messageDto.getText(), sender, sender, chat.getChatId()));
            return messageRepository.getMessageProjection(message.getId());
        }

        if (!chatRepository.existsBySenderIdAndReceiverId(sender.getId(), receiver.getId())) {
            UUID chatId = UUID.randomUUID();
            Chat first = chatRepository.save(new Chat(chatId, sender, receiver));
            Chat second = chatRepository.save(new Chat(chatId, receiver, sender));
            Message message = new Message(messageDto.getText(), sender, receiver, first.getChatId());
            message = messageRepository.save(message);
            return messageRepository.getMessageProjection(message.getId());
        }

        Chat first = chatRepository.getBySenderIdAndReceiverId(sender.getId(), receiver.getId());
        Message message = new Message(messageDto.getText(), sender, receiver, first.getChatId());
        message = messageRepository.save(message);

        return messageRepository.getMessageProjection(message.getId());
    }

    public ResponseEntity<ApiResponse<List<MessageProjection>>> getChatMessages(Long receiverId, UserDetails userDetails) {
        Objects.requireNonNull(userDetails);

        User currentUser = (User) userDetails;
        User receiver = userRepo
                .findById(receiverId)
                .orElseThrow(() -> new IllegalStateException("User with id: " + receiverId + " not found!"));

        List<MessageProjection> chatMessages = messageRepository.getChatMessages(currentUser.getId(), receiver.getId());
        return response(chatMessages);
    }

    public UserProjection checkAndGetSender(Long senderId, Long receiverId) {
        User sender = userRepo
                .findById(senderId)
                .orElseThrow(() -> new IllegalStateException("ID: " + senderId + " not found!"));

        User receiver = userRepo
                .findById(receiverId)
                .orElseThrow(() -> new IllegalStateException("ID: " + receiverId + " not found!"));

        if (chatRepository.existsBySenderAndReceiver(sender, receiver)) {
            return userRepo.getUserProjectionById(sender.getId());
        }
        return null;
    }
}
