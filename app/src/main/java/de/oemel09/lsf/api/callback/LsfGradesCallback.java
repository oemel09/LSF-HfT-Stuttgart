package de.oemel09.lsf.api.callback;

import android.content.Context;

import java.util.ArrayList;

import de.oemel09.lsf.api.listeners.LsfGradesSuccessful;
import de.oemel09.lsf.api.listeners.LsfRequestListener;
import de.oemel09.lsf.grades.Grade;
import de.oemel09.lsf.grades.GradeParser;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LsfGradesCallback extends LsfCallback {

    private final LsfGradesSuccessful lsfRequestSuccessful;

    public LsfGradesCallback(Context context, LsfRequestListener lsfRequestListener,
                             LsfGradesSuccessful lsfRequestSuccessful) {
        super(context, lsfRequestListener);
        this.lsfRequestSuccessful = lsfRequestSuccessful;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful() && response.body() != null) {
            String content = responseToString(response.body());
            ArrayList<Grade> grades = new ArrayList<>(new GradeParser().parse(content));
            lsfRequestSuccessful.onGradesSuccessful(grades);
        } else {
            failedToLogIn();
        }
    }
}
