package com.eryonix.controller;



import com.eryonix.model.ContactMessage;
import com.eryonix.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*") // Allow frontend requests
public class ContactMessageController {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @PostMapping
    public ResponseEntity<?> receiveMessage(@RequestBody ContactMessage message) {
        contactMessageRepository.save(message);
        return ResponseEntity.ok("Message received successfully!");
    }
}
