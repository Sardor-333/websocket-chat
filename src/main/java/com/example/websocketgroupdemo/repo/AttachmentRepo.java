package com.example.websocketgroupdemo.repo;

import com.example.websocketgroupdemo.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepo extends JpaRepository<Attachment, Long> {
}
