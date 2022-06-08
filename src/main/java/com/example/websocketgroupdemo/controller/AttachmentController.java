package com.example.websocketgroupdemo.controller;

import com.example.websocketgroupdemo.entity.Attachment;
import com.example.websocketgroupdemo.entity.AttachmentContent;
import com.example.websocketgroupdemo.repo.AttachmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentRepo attachmentRepo;

    @GetMapping("/download-file/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long fileId) {
        Optional<Attachment> attachmentOptional = attachmentRepo.findById(fileId);
        if (attachmentOptional.isPresent()) {
            Attachment attachment = attachmentOptional.get();
            AttachmentContent content = attachment.getAttachmentContent();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + attachment.getOriginalFileName())
                    .body(new ByteArrayResource(content.getContent()));
        }
        return null;
    }
}
