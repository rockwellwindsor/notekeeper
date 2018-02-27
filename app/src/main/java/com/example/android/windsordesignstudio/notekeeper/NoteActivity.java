package com.example.android.windsordesignstudio.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_POSITION = "com.example.android.windsordesignstudio.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.android.windsordesignstudio.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.android.windsordesignstudio.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.android.windsordesignstudio.notekeeper.ORIGINAL_NOTE_TEXT";
    public EditText mNoteTitle;
    public EditText mNoteBody;
    public Spinner mCourseSpinner;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private int mNotePosition;
    public boolean mIsCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNoteTitle = (EditText) findViewById(R.id.et_note_title);
        mNoteBody = (EditText) findViewById(R.id.et_note_body);
        mCourseSpinner = (Spinner) findViewById(R.id.sp_course_select);

        List<CourseInfo> mCourses = DataManager.getInstance().getCourses();

        // Create Adapter to fill the spinner
        ArrayAdapter<CourseInfo> mAdapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCourses);
        mAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCourseSpinner.setAdapter(mAdapterCourses);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        
        readDisplayStateValues();

        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        } else {
            restoreOriginalNoteValues(savedInstanceState);
        }


        if(!mIsNewNote) {
            displayNote(mCourseSpinner, mNoteTitle, mNoteBody);
        }
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }


    private void saveOriginalNoteValues() {
        if(mIsNewNote) {
            return;
        }
        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle = mNote.getTitle();
        mOriginalNoteText = mNote.getText();
    }

    private void displayNote(Spinner courseSpinner, EditText noteTitle, EditText noteBody) {
        // Set up spinner
        // Get list of courses from data manager
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        // Get index of course in list
        int courseIndex = courses.indexOf(mNote.getCourse());
        // Set selection
        courseSpinner.setSelection(courseIndex);

        noteTitle.setText(mNote.getTitle());
        noteBody.setText(mNote.getText());
    }

    private void readDisplayStateValues() {
        Intent mIntent = getIntent();
        int position = mIntent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote = position == POSITION_NOT_SET;
        if(mIsNewNote) {
            createNewNote();
        } else {
            mNote = DataManager.getInstance().getNotes().get(position);
        }
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();
        } else if ( id == R.id.action_next) {
            moveNext();
        } else if ( id == R.id.action_previous) {
            movePrevious();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem next_item = menu.findItem(R.id.action_next);
        MenuItem previous_item = menu.findItem(R.id.action_previous);

        int lastNoteIndex = DataManager.getInstance().getNotes().size() -1;

        next_item.setEnabled(mNotePosition < lastNoteIndex);
        previous_item.setEnabled(mNotePosition > 0);

        return super.onPrepareOptionsMenu(menu);
    }

    private void movePrevious() {
        saveNote();                                                      // Since we save when navigating away we need to save the note
        --mNotePosition;                                                 // Decrement the note position counter
        mNote = DataManager.getInstance().getNotes().get(mNotePosition); // Get the note at the next position which was decremented down above
        saveOriginalNoteValues();                                        // Save original values for the note
        displayNote(mCourseSpinner, mNoteTitle, mNoteBody);              // Display the next note
        invalidateOptionsMenu();                                         // The system will run onPrepareOptionsMenu
    }

    private void moveNext() {
        saveNote();                                                      // Since we save when navigating away we need to save the note
        ++mNotePosition;                                                 // Increment the note position counter
        mNote = DataManager.getInstance().getNotes().get(mNotePosition); // Get the note at the next position which was incremented up above
        saveOriginalNoteValues();                                        // Save original values for the note
        displayNote(mCourseSpinner, mNoteTitle, mNoteBody);              // Display the next note
        invalidateOptionsMenu();                                         // The system will run onPrepareOptionsMenu
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mCourseSpinner.getSelectedItem();
        String subject = mNoteTitle.getText().toString();
        String text = "Check out what I learned in the Pluralsight course \"" +
                course.getTitle() + "\"\n" + mNoteBody.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rhc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling) {
            if(mIsNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginalNoteTitle);
        mNote.setText(mOriginalNoteText);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mCourseSpinner.getSelectedItem());
        mNote.setTitle(mNoteTitle.getText().toString());
        mNote.setText(mNoteBody.getText().toString());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }
}
