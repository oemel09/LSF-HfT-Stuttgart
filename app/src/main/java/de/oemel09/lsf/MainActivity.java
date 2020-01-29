package de.oemel09.lsf;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.oemel09.lsf.api.LsfLoader;
import de.oemel09.lsf.api.listeners.LsfRequestListener;
import de.oemel09.lsf.gradeinfo.GradeInfo;
import de.oemel09.lsf.gradeinfo.grades.Grade;
import de.oemel09.lsf.gradeinfo.grades.GradeAdapter;
import de.oemel09.lsf.gradeinfo.grades.details.GradeDetailsActivity;
import de.oemel09.lsf.poll.PollGradesSchedulerService;


public class MainActivity extends AppCompatActivity implements LsfRequestListener,
        GradeAdapter.OnGradeClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String LOGGED_IN = "LOGGED_IN";
    public static final String DOMAIN = "DOMAIN";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String GRADE = "GRADE";
    private static final int JOB_ID = 1305;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private GradeAdapter gradesAdapter;
    private ProgressDialog loadingDialog;
    private LsfLoader lsfLoader;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        if (prefs.getBoolean(LOGGED_IN, false)) {
            setupGradeView();
        } else {
            setupLoginView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setUpSearchDialog(menu);
        return true;
    }

    private void setUpSearchDialog(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
    }

    private void setupGradeView() {
        setContentView(R.layout.activity_main);
        lsfLoader = new LsfLoader(this, this);
        lsfLoader.getGrades(gradeInfo -> {
            fillTextViews(gradeInfo);
            allGradesLoaded(gradeInfo.getGrades());
        });
    }

    private void fillTextViews(GradeInfo gradeInfo) {
        ((TextView) findViewById(R.id.grade_info_total_average)).setText(gradeInfo.getAverage());
        ((TextView) findViewById(R.id.grade_info_cp_base_courses)).setText(gradeInfo.getCpBaseCourses());
        ((TextView) findViewById(R.id.grade_info_cp_main_courses)).setText(gradeInfo.getCpMainCourses());
    }

    private void allGradesLoaded(ArrayList<Grade> grades) {
        gradesAdapter = new GradeAdapter(grades);
        gradesAdapter.setOnGradeClickListener(this);

        configureRecyclerView();

        gradesAdapter.notifyDataSetChanged();
        loadingDialog.cancel();
    }

    private void configureRecyclerView() {
        RecyclerView rvGrades = findViewById(R.id.rv_grades);
        rvGrades.setLayoutManager(new LinearLayoutManager(this));
        rvGrades.setAdapter(gradesAdapter);
    }

    private void setupLoginView() {
        setContentView(R.layout.login);
        TextInputEditText etDomain = findViewById(R.id.et_domain);
        TextInputEditText etUsername = findViewById(R.id.et_username);
        TextInputEditText etPassword = findViewById(R.id.et_password);

        fillOutLoginData(etDomain, etUsername);
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            String domain = validateDomain(Objects.requireNonNull(etDomain.getText()).toString());
            editor.putString(DOMAIN, domain);
            editor.putString(USERNAME, Objects.requireNonNull(etUsername.getText()).toString());
            editor.putString(PASSWORD, Objects.requireNonNull(etPassword.getText()).toString());
            editor.apply();
            setupGradeView();
        });
    }

    private String validateDomain(String domain) {
        if (domain == null) return "";
        if (domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }
        return domain;
    }

    private void fillOutLoginData(TextInputEditText etDomain, TextInputEditText etUsername) {
        if (prefs.getString(DOMAIN, null) != null) {
            etDomain.setText(prefs.getString(DOMAIN, null));
        }
        if (prefs.getString(USERNAME, null) != null) {
            etUsername.setText(prefs.getString(USERNAME, null));
        }
    }

    @Override
    public void onRequestStart() {
        showLoadingDialog();
    }

    private void showLoadingDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(false);
        loadingDialog.setMessage(getString(R.string.loading));
        loadingDialog.show();
    }

    @Override
    public void onRequestFailed() {
        loadingDialog.cancel();
    }

    @Override
    public void onLoginFailed() {
        setupLoginView();
    }

    @Override
    public void onGradeClick(Grade grade) {
        showDetailsPage(grade);
    }

    private void showDetailsPage(Grade grade) {
        if (grade.getGradeDetailsLink() == null) {
            Toast.makeText(this, getString(R.string.no_grade_details), Toast.LENGTH_SHORT).show();
        } else {
            if (grade.getGradeDetails() == null) {
                loadGradeDetails(grade);
            } else {
                startDetailsActivity(grade);
            }
        }
    }

    private void loadGradeDetails(Grade grade) {
        lsfLoader.getGradeDetails(grade.getGradeDetailsLink(), gradeDetails -> {
            loadingDialog.cancel();
            if (gradeDetails != null) {
                grade.setGradeDetails(gradeDetails);
                startDetailsActivity(grade);
            }
        });
    }

    private void startDetailsActivity(Grade grade) {
        Intent startDetailsActivity = new Intent(this, GradeDetailsActivity.class);
        startDetailsActivity.putExtra(GRADE, grade);
        startActivity(startDetailsActivity);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        gradesAdapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        gradesAdapter.filter(newText);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        schedulePullNewGrades();
    }

    private void schedulePullNewGrades() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (jobScheduler.getPendingJob(JOB_ID) == null) {
            int resultCode = jobScheduler.schedule(createJobInfo());
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.i(TAG, "schedulePullNewGrades: Job scheduled.");
            } else {
                Log.e(TAG, "schedulePullNewGrades: Failed to schedule job.");
            }
        }
    }

    private JobInfo createJobInfo() {
        ComponentName componentName = new ComponentName(this, PollGradesSchedulerService.class);
        int repeatInMillis = 30 * 60 * 1000;
        return new JobInfo.Builder(JOB_ID, componentName)
                .setPeriodic(repeatInMillis)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();
    }
}
