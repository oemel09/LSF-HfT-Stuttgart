package de.oemel09.lsf.pullgrades;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.oemel09.lsf.R;
import de.oemel09.lsf.api.LsfLoader;
import de.oemel09.lsf.api.listeners.LsfRequestListener;
import de.oemel09.lsf.notification.NotificationHandler;

public class PullGradesSchedulerService extends JobService implements LsfRequestListener {

    private static final String OLD_AMOUNT_OF_GRADES = "OLD_AMOUNT_OF_GRADES";

    private JobParameters params;
    private SharedPreferences prefs;
    private NotificationHandler notificationHandler;

    @Override
    public boolean onStartJob(JobParameters params) {
        init(params);
        loadAllGrades();
        return true;
    }

    private void init(JobParameters params) {
        this.params = params;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        notificationHandler = new NotificationHandler(this);
    }

    private void loadAllGrades() {
        LsfLoader lsfLoader = new LsfLoader(this, this);
        lsfLoader.getGrades(gradeInfo -> {
            int oldAmountOfGrades = getOldAmountOfGrades();
            int newAmountOfGrades = gradeInfo.getGrades().size();
            saveNewAmountOfGrades(newAmountOfGrades);
            showNotification(newAmountOfGrades > oldAmountOfGrades);
        });
    }

    private int getOldAmountOfGrades() {
        return prefs.getInt(OLD_AMOUNT_OF_GRADES, 0);
    }

    private void saveNewAmountOfGrades(int newAmountOfGrades) {
        prefs.edit().putInt(OLD_AMOUNT_OF_GRADES, newAmountOfGrades).apply();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @Override
    public void onRequestStart() {
    }

    @Override
    public void onRequestFailed() {
        showNotificationCheckForGradesFailed(getString(R.string.error_request_failed));
    }

    @Override
    public void onLoginFailed() {
        showNotificationCheckForGradesFailed(getString(R.string.error_login_failed));
    }

    private void showNotification(boolean newGradeAvailable) {
        String notificationText = newGradeAvailable
                ? getString(R.string.result_new_grades_available)
                : getString(R.string.result_no_new_grades_available);
        notificationHandler.showNotification(notificationText);
        jobFinished(params, false);
    }

    private void showNotificationCheckForGradesFailed(String error) {
        notificationHandler.showNotification(error);
        jobFinished(params, true);
    }
}
