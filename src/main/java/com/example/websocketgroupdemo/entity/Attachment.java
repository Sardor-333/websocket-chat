package com.example.websocketgroupdemo.entity;

import com.example.websocketgroupdemo.entity.abs.AbsEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "attachments")
public class Attachment extends AbsEntity {

    @Column(nullable = false, updatable = false)
    String originalFileName;

    @Column(nullable = false, unique = true, updatable = false)
    String generatedFileName;

    @Column(nullable = false, updatable = false)
    Long size;

    @Column(nullable = false, updatable = false)
    String contentType;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "attachment_content_id", nullable = false, referencedColumnName = "id")
    AttachmentContent attachmentContent;

    public static Attachment prepareAttachment(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
            if (2 > split.length) {
                return null;
            }
            String generatedFileName = UUID.randomUUID() + "." + split[split.length - 1];
            return new Attachment(
                    file.getOriginalFilename(),
                    generatedFileName,
                    file.getSize(),
                    file.getContentType(),
                    new AttachmentContent(file.getBytes())
            );
        }
        return null;
    }

    public static String convertToBase64Encoded(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
