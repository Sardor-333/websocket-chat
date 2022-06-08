package com.example.websocketgroupdemo.controller;

import com.example.websocketgroupdemo.payload.req.MessageDto;
import com.example.websocketgroupdemo.payload.req.Typing;
import com.example.websocketgroupdemo.projection.MessageProjection;
import com.example.websocketgroupdemo.projection.UserProjection;
import com.example.websocketgroupdemo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@RequestBody MessageDto messageDto) {
        MessageProjection message = chatService.sendMessage(messageDto);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getSender().getId()),
                "/queue/messages",
                message
        );

        // IF MESSAGE IS NOT 'SAVED MESSAGE' THEN IT SENDS TO RECEIVER
        if (!messageDto.getSenderId().equals(messageDto.getReceiverId())) {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(message.getReceiver().getId()),
                    "/queue/messages",
                    message
            ); // /user/1/queue/messages
        }

        System.out.println("=== SENDER === " + message.getSender());
        System.out.println("=== RECEIVER === " + message.getReceiver());
    }

    @MessageMapping("/chat/typing")
    public void senderTyping(@RequestBody Typing typing) {
        UserProjection projection = chatService.checkAndGetSender(typing.getSenderId(), typing.getReceiverId());

        if (projection != null) {
            // === IF SENDER IS TYPING TO RECEIVER THEN IT NEEDS TO GO TO THE RECEIVER
            messagingTemplate.convertAndSendToUser(
                    typing.getReceiverId().toString(),
                    "/typing",
                    projection
            );
        }
    }

    @MessageMapping("/chat/notTyping")
    public void senderStoppedTyping(@RequestBody Typing typing) {
        UserProjection projection = chatService.checkAndGetSender(typing.getSenderId(), typing.getReceiverId());

        if (projection != null) {
            // === IF SENDER IS TYPING TO RECEIVER THEN IT NEEDS TO GO TO THE RECEIVER
            messagingTemplate.convertAndSendToUser(
                    typing.getReceiverId().toString(),
                    "/notTyping",
                    projection
            );
        }
    }

    @GetMapping("/chatPage")
    public String getChatMainPage() {
        return "chat";
    }
}
