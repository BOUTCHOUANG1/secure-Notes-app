package com.nathan.secure_notes.service.impl;

import com.nathan.secure_notes.exception.APIException;
import com.nathan.secure_notes.exception.ResourceNotFoundException;
import com.nathan.secure_notes.model.Notes;
import com.nathan.secure_notes.repository.NoteRepository;
import com.nathan.secure_notes.service.NoteService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;

    @Override
    public Notes createNoteForUser(Notes note, String username) {
        Notes content = this.noteRepository.findByContentAndOwnerUserName(note.getContent(), username);
        if(content != null) {
            throw new APIException("Note already exists for this user");
        }
        return noteRepository.save(note);
    }

    @Override
    public List<Notes> getAllNotesAUser(String username) {
        List<Notes> notes = noteRepository.findByOwnerUserName(username);
        if(notes.isEmpty()) {
            throw new APIException("Notes not found for this " + username);
        }
        return notes;
    }

    @Override
    public Notes updateNoteForUser(Long noteId, Notes note, String username) {
        Notes existingNote = this.noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Notes", "NotesId", noteId));
        existingNote.setOwnerUserName(note.getOwnerUserName());
        existingNote.setContent(note.getContent());
        return noteRepository.save(existingNote);
    }

    @Override
    public void deleteNoteForUser(Long noteId, String username) {
        Notes existingNote = this.noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Notes", "NotesId", noteId));
        noteRepository.delete(existingNote);
    }
}
