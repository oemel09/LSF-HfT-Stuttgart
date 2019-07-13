package de.oemel09.lsf.api.callback;

import android.content.Context;

import java.util.ArrayList;

import de.oemel09.lsf.api.listeners.LsfGradeInfoSuccessful;
import de.oemel09.lsf.api.listeners.LsfRequestListener;
import de.oemel09.lsf.gradeinfo.grades.Grade;
import de.oemel09.lsf.gradeinfo.GradeInfo;
import de.oemel09.lsf.gradeinfo.GradeInfoParser;
import de.oemel09.lsf.gradeinfo.grades.GradeParser;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LsfGradesCallback extends LsfCallback {

    private final LsfGradeInfoSuccessful lsfRequestSuccessful;

    public LsfGradesCallback(Context context, LsfRequestListener lsfRequestListener,
                             LsfGradeInfoSuccessful lsfRequestSuccessful) {
        super(context, lsfRequestListener);
        this.lsfRequestSuccessful = lsfRequestSuccessful;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful() && response.body() != null) {
            String content = responseToString(response.body());
            GradeInfo gradeInfo = new GradeInfoParser().parse(content);
            if (gradeInfo == null) {
                getLsfRequestListener().onRequestFailed();
            } else {
                ArrayList<Grade> grades = new ArrayList<>(new GradeParser().parse(content));
                gradeInfo.setGrades(grades);
                lsfRequestSuccessful.onGradeInfoSuccessful(gradeInfo);
            }
        } else {
            failedToLogIn();
        }
    }
}
