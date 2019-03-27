package de.oemel09.lsf.api.callback;

import android.content.Context;
import android.widget.Toast;

import de.oemel09.lsf.R;
import de.oemel09.lsf.api.listeners.LsfGradeDetailsSuccessful;
import de.oemel09.lsf.api.listeners.LsfRequestListener;
import de.oemel09.lsf.gradeinfo.grades.details.GradeDetails;
import de.oemel09.lsf.gradeinfo.grades.details.GradeDetailsParser;
import de.oemel09.lsf.gradeinfo.grades.details.NoDetailsException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LsfGradeDetailsCallback extends LsfCallback {

    private final LsfGradeDetailsSuccessful lsfGradeDetailsSuccessful;

    public LsfGradeDetailsCallback(Context context, LsfRequestListener lsfRequestListener,
                                   LsfGradeDetailsSuccessful lsfGradeDetailsSuccessful) {
        super(context, lsfRequestListener);
        this.lsfGradeDetailsSuccessful = lsfGradeDetailsSuccessful;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful() && response.body() != null) {
            String content = responseToString(response.body());
            GradeDetails gradeDetails = parseGradeDetails(content);
            lsfGradeDetailsSuccessful.onGradeDetailsSuccessful(gradeDetails);
        } else {
            failedToLogIn();
        }
    }

    private GradeDetails parseGradeDetails(String content) {
        try {
            return new GradeDetailsParser().parse(content);
        } catch (NoDetailsException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), getContext().getString(R.string.no_grade_details), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        getLsfRequestListener().onRequestFailed();
        Toast.makeText(getContext(), getContext().getString(R.string.no_grade_details), Toast.LENGTH_SHORT).show();
    }
}
