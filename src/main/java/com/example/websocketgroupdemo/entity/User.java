package com.example.websocketgroupdemo.entity;

import com.example.websocketgroupdemo.entity.abs.AbsEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User extends AbsEntity implements UserDetails {

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    String fullName;

    @Column(nullable = false, columnDefinition = "boolean default false")
    boolean isOnline = false;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "attachment_id", referencedColumnName = "id")
    Attachment attachment;

    @OneToMany(mappedBy = "sender")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Message> senderMessages;

    @OneToMany(mappedBy = "receiver")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Message> receiverMessages;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User(String username, String password, String fullName, boolean isOnline) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.isOnline = isOnline;
    }
}
