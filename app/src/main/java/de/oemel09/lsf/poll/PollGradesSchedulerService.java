package de.oemel09.lsf.poll;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class PollGradesSchedulerService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        new PollGrades(this, params).run();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
