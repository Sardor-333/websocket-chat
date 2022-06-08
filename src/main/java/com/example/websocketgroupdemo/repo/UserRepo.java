package com.example.websocketgroupdemo.repo;

import com.example.websocketgroupdemo.entity.User;
import com.example.websocketgroupdemo.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    @Query(
            nativeQuery = true,
            value = """
                    select id as id,
                           username as username,
                           full_name as fullName,
                           attachment_id as imgId,
                           is_online as online
                    from users u\s
                    where id = :userId
                    """
    )
    UserProjection getUserProjectionById(@Param("userId") Long userId);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query(
            nativeQuery = true,
            value = """
                    select rec.id            as id,
                           rec.username      as username,
                           rec.full_name     as fullName,
                           rec.attachment_id as imgId,
                           rec.is_online as online
                    from chats ch
                             join users sen on ch.sender_id = sen.id
                             join users rec on rec.id = ch.receiver_id
                    where sen.id = :senderId
                    """
    )
    List<UserProjection> getMyContacts(@Param("senderId") Long senderId);
}
