package com.example.websocketgroupdemo.entity;

import com.example.websocketgroupdemo.entity.abs.AbsEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "attachment_contents")
public class AttachmentContent extends AbsEntity {

    @Column(nullable = false)
    byte[] content;
}
