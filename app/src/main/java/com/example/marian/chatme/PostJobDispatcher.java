package com.example.marian.chatme;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostJobDispatcher extends JobService
{
    private AsyncTask asyncTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters)
    {
        asyncTask = new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object[] params)
            {
                Context context = PostJobDispatcher.this;
                return null;
            }

            @Override
            protected void onPostExecute(Object o)
            {

                jobFinished(jobParameters, false);
            }
        };
        asyncTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters)
    {
        if (asyncTask != null) asyncTask.cancel(true);
        return true;
    }
}
