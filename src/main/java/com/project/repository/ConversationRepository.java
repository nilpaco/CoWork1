package com.project.repository;

import com.project.domain.Conversation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Conversation entity.
 */
public interface ConversationRepository extends JpaRepository<Conversation,Long> {

    @Query("select conversation from Conversation conversation where conversation.user.login = ?#{principal.username}")
    Page<Conversation> findByUserIsCurrentUser(Pageable pageable);

    @Query("select conversation from Conversation conversation where conversation.user.login = ?#{principal.username} and conversation.space.id = :space_id")
    Conversation findByUserIsCurrentUserAndSpace(@Param("space_id") Long id);

    @Query("select conversation from Conversation conversation, Space s where s.user.login = ?#{principal.username} and conversation.space.id = s.id")
    Page<Conversation> findByUserAndSpace(Pageable pageable);



}
