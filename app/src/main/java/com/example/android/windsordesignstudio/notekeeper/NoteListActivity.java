package com.example.android.windsordesignstudio.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private ArrayAdapter<NoteInfo> mAdapterNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapterNotes.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {

        // Get reference to ListView
        final ListView mListNotes = (ListView) findViewById(R.id.list_notes);

        // Get notes
        List<NoteInfo> notes = DataManager.getInstance().getNotes();

        // Create Array Adapter
        mAdapterNotes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);

        // Set Adapter to the note list
        mListNotes.setAdapter(mAdapterNotes);

        // Display note on item click
        mListNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
//                NoteInfo note = (NoteInfo) mListNotes.getItemAtPosition(position);
                intent.putExtra(NoteActivity.NOTE_POSITION, position);
                startActivity(intent);
            }
        });
    }
}
