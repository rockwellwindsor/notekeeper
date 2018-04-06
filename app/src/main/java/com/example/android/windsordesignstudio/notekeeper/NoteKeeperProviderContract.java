package com.example.android.windsordesignstudio.notekeeper;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by Rockwell Windsor Rice on 4/6/18.
 */

public final class NoteKeeperProviderContract {
    private NoteKeeperProviderContract() {}
    public static final String AUTHORITY = "com.example.android.windsordesignstudio.notekeeper.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface CourseIdColumns {
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    protected interface CoursesColumns {
        public static final String COLUMN_COURSE_TITLE = "course_title";
    }

    protected interface NotesColumns {
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT  = "note_text";
    }

    public static final class Courses implements BaseColumns, CoursesColumns, CourseIdColumns {
        public static final String PATH = "courses"; // content://com.example.android.windsordesignstudio.notekeeper.provider/courses
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Notes implements BaseColumns, NotesColumns, CourseIdColumns, CoursesColumns {
        public static final String PATH = "notes"; // content://com.example.android.windsordesignstudio.notekeeper.provider/notes
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);

        /*
         * Provides access to the constant COLUMN_COURSE_TITLE
         * The COLUMN_COURSE_TITLE value is only available with this URI
        */
        public static final String PATH_EXPANDED = "notes_expanded";
        public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED);
    }
}
