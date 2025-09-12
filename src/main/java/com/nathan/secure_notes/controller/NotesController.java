package com.nathan.secure_notes.controller;

import com.nathan.secure_notes.model.Notes;
import com.nathan.secure_notes.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class NotesController {
    private final NoteService noteService;

    @PostMapping("/create")
    public ResponseEntity<Notes> createNoteForUser(@RequestBody Notes note,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Notes createdNote = noteService.createNoteForUser(note, username);
        return new ResponseEntity<>(createdNote, HttpStatus.CREATED);
    }

    @GetMapping("/allNotes")
    public ResponseEntity<List<Notes>> getAllNotesForUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<Notes> notes = noteService.getAllNotesAUser(username);
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @PutMapping("/update/{noteId}")
    public ResponseEntity<Notes> updateNoteForUser(@PathVariable Long noteId,
                                                   @RequestBody Notes note,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Notes updatedNote = noteService.updateNoteForUser(noteId, note, username);
        return new ResponseEntity<>(updatedNote, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<Void> deleteNoteForUser(@PathVariable Long noteId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        noteService.deleteNoteForUser(noteId, username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
