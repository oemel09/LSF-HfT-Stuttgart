package de.oemel09.lsf.poll;

import android.app.job.JobParameters;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import de.oemel09.lsf.R;
import de.oemel09.lsf.api.LsfLoader;
import de.oemel09.lsf.api.listeners.LsfRequestListener;
import de.oemel09.lsf.notification.NotificationHandler;

public class PollGrades implements Runnable, LsfRequestListener {

    private static final String TAG = PollGrades.class.getSimpleName();

    private static final String OLD_AMOUNT_OF_GRADES = "OLD_AMOUNT_OF_GRADES";

    private final PollGradesSchedulerService schedulerService;
    private final JobParameters params;
    private final SharedPreferences prefs;
    private final NotificationHandler notificationHandler;

    PollGrades(PollGradesSchedulerService pollGradesSchedulerService, JobParameters params) {
        this.schedulerService = pollGradesSchedulerService;
        this.params = params;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(pollGradesSchedulerService);
        this.notificationHandler = new NotificationHandler(pollGradesSchedulerService);
    }

    @Override
    public void run() {
        loadAllGrades();
    }

    private void loadAllGrades() {
        LsfLoader lsfLoader = new LsfLoader(schedulerService, this);
        lsfLoader.getGrades(gradeInfo -> {
            int oldAmountOfGrades = getOldAmountOfGrades();
            int newAmountOfGrades = gradeInfo.getGrades().size();
            if (newAmountOfGrades > oldAmountOfGrades) {
                showNotification();
                saveNewAmountOfGrades(newAmountOfGrades);
            } else {
                Log.i(TAG, "loadAllGrades: no new grades online");
            }
        });
    }

    private int getOldAmountOfGrades() {
        return prefs.getInt(OLD_AMOUNT_OF_GRADES, 0);
    }

    private void saveNewAmountOfGrades(int newAmountOfGrades) {
        prefs.edit().putInt(OLD_AMOUNT_OF_GRADES, newAmountOfGrades).apply();
    }

    @Override
    public void onRequestStart() {
    }

    @Override
    public void onRequestFailed() {
        showNotificationCheckForGradesFailed(schedulerService.getString(R.string.error_request_failed));
    }

    @Override
    public void onLoginFailed() {
        showNotificationCheckForGradesFailed(schedulerService.getString(R.string.error_login_failed));
    }

    private void showNotification() {
        String notificationText = schedulerService.getString(R.string.result_new_grades_available);
        notificationHandler.showNotification(notificationText);
        schedulerService.jobFinished(params, false);
    }

    private void showNotificationCheckForGradesFailed(String error) {
        notificationHandler.showNotification(error);
        schedulerService.jobFinished(params, true);
    }
}
