package com.eryonix.service;

import com.eryonix.model.Message;
import com.eryonix.model.User;
import com.eryonix.repository.MessageRepository;
import com.eryonix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    //  Get all messages between two users
    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }

    //  Get inbox view – recent chats sorted by latest message
    public List<Message> getRecentChats(Long userId) {
        return messageRepository.findRecentChats(userId);
    }

    //  Send message to a user (with optional shared media)
    public Message sendMessage(Long senderId, Long receiverId, String content, String sharedMediaUrl, String sharedMediaType) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setSharedMediaUrl(sharedMediaUrl);
        message.setSharedMediaType(sharedMediaType);

        return messageRepository.save(message);
    }

    
}

