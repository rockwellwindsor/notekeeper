package com.example.android.windsordesignstudio.notekeeper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Rockwell Rice on 2/19/18.
 */
public class DataManagerTest {

    static DataManager sDataManager;

    @BeforeClass
    public static void classSetUp() throws Exception {
        sDataManager = DataManager.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        sDataManager.getNotes().clear();
        sDataManager.initializeExampleNotes();
    }

    @Test
    public void createNewNote() throws Exception {
        final CourseInfo course = sDataManager.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText = "This is the body of the test note";

        // Create a new note
        int noteIndex  = sDataManager.createNewNote();
        NoteInfo newNote = sDataManager.getNotes().get(noteIndex); // get index of note
        newNote.setCourse(course);                                 // Set new note values
        newNote.setTitle(noteTitle);                               // Set new note values
        newNote.setText(noteText);                                 // Set new note values

        // Test the new note
        NoteInfo compareNote = sDataManager.getNotes().get(noteIndex);
        junit.framework.Assert.assertEquals(compareNote.getCourse(), course);
        junit.framework.Assert.assertEquals(compareNote.getTitle(), noteTitle);
        junit.framework.Assert.assertEquals(compareNote.getText(), noteText);
    }

}