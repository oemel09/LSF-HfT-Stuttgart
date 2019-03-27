package de.oemel09.lsf.api.callback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.oemel09.lsf.api.listeners.LsfLoginSuccessful;
import de.oemel09.lsf.api.listeners.LsfRequestListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static de.oemel09.lsf.api.LsfApi.ASI;

public class LsfAsiCallback extends LsfCallback {

    private SharedPreferences.Editor editor;
    private final LsfLoginSuccessful lsfLoginRequestSuccessful;

    @SuppressLint("CommitPrefEdits")
    public LsfAsiCallback(Context context, LsfRequestListener lsfRequestListener,
                          LsfLoginSuccessful lsfLoginRequestSuccessful) {
        super(context, lsfRequestListener);
        this.editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        this.lsfLoginRequestSuccessful = lsfLoginRequestSuccessful;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful() && response.body() != null) {
            String content = responseToString(response.body());
            validateResponse(content);
            lsfLoginRequestSuccessful.onLoginRequestSuccessful();
        } else {
            failedToLogIn();
        }
    }

    private void validateResponse(String result) {
        Pattern pattern = Pattern.compile("asi=(.*?)\"");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            String asi = matcher.group(1);
            editor.putString(ASI, asi).apply();
        } else {
            failedToLogIn();
        }
    }
}
