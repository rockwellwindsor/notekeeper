package com.example.android.windsordesignstudio.notekeeper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rockwell Windsor Rice on 3/7/18.
 */

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NoteKeeperDatabase.db";
    public static final int DATABASE_VERSION = 2;

    public NoteKeeperOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(NoteKeeperDatabaseContract.NoteInfoEntry.SQL_CREATE_TABLE);

        // Run statements to create Indexes
        db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(NoteKeeperDatabaseContract.NoteInfoEntry.SQL_CREATE_INDEX1);

        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertCourses();
        worker.insertSampleNotes();
    }

    /**
     *
     * This method's responsibility is to upgrade the db version when it is changed.
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2) {
            // Run statements to create Indexes
            db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_INDEX1);
            db.execSQL(NoteKeeperDatabaseContract.NoteInfoEntry.SQL_CREATE_INDEX1);
        }
    }
}
