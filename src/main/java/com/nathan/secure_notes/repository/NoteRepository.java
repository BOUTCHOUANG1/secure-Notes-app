package com.nathan.secure_notes.repository;

import com.nathan.secure_notes.model.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Notes, Long> {
    List<Notes> findByOwnerUserName(String ownerUserName);

    Notes findByContent(String content);

    Notes findByContentAndOwnerUserName(String content, String username);
}
