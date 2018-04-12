package com.example.android.windsordesignstudio.notekeeper;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;

public class NoteUploaderJobService extends JobService {

    public static final String EXTRA_DATA_URI = "com.example.android.windsordesignstudio.notekeeper.extras.DATA_URI";
    private NoteUploader mNoteUploader;

    public NoteUploaderJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        AsyncTask<JobParameters, Void, Void> task = new AsyncTask<JobParameters, Void, Void>() {

            @Override
            protected Void doInBackground(JobParameters... backgroundParams) {
                JobParameters jobParams = backgroundParams[0];
                String stringDataUri = jobParams.getExtras().getString(EXTRA_DATA_URI);
                Uri dataUri = Uri.parse(stringDataUri);
                mNoteUploader.doUpload(dataUri);

                if(! mNoteUploader.isCanceled()) {
                    jobFinished(jobParams, false); // False means the job is done, true will reschedule the job
                }
                return null;
            }
        };

        mNoteUploader = new NoteUploader(this);
        // Start the actual background work running
        task.execute(params);

        return true; // Lets job scheduler know we have started the job in the background and the process needs to be allowed to run
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mNoteUploader.cancel();
        return true; // Sends true back so job will be rescheduled





    }
}
