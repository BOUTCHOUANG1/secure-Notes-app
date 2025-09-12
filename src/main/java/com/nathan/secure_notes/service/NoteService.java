package com.nathan.secure_notes.service;

import com.nathan.secure_notes.model.Notes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NoteService {
    Notes createNoteForUser(Notes note, String username);

    List<Notes> getAllNotesAUser(String username);

    Notes updateNoteForUser(Long noteId, Notes note, String username);

    void deleteNoteForUser(Long noteId, String username);
}
