package com.example.android.windsordesignstudio.notekeeper;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Rockwell Windsor Rice on 4/11/18.
 */

public class CourseEventBroadcastHelper {
    public static final String ACTION_COURSE_EVENT = "com.example.android.windsordesignstudio.notekeeper.action.COURSE_EVENT";
    public static final String EXTRA_COURSE_ID = "com.example.android.windsordesignstudio.notekeeper.extra.COURSE_ID";
    public static final String EXTRA_COURSE_MESSAGE = "com.example.android.windsordesignstudio.notekeeper.extra.COURSE_MESSAGE";

    public static void sendEventBroadcast(Context context, String courseId, String message) {
        Intent intent = new Intent(ACTION_COURSE_EVENT);
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_COURSE_MESSAGE, message);

        context.sendBroadcast(intent);
    }
}
