package de.oemel09.lsf.api.callback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.oemel09.lsf.R;
import de.oemel09.lsf.api.listeners.LsfLoginSuccessful;
import de.oemel09.lsf.api.listeners.LsfRequestListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static de.oemel09.lsf.api.LsfApi.COOKIE;
import static de.oemel09.lsf.api.LsfLogin.COOKIE_TIME;

public class LsfLoginCallback extends LsfCallback {

    private static final String LOGGED_IN = "LOGGED_IN";

    private SharedPreferences.Editor editor;
    private final LsfLoginSuccessful lsfLoginRequestSuccessful;

    @SuppressLint("CommitPrefEdits")
    public LsfLoginCallback(Context context, LsfRequestListener lsfRequestListener,
                            LsfLoginSuccessful lsfLoginRequestSuccessful) {
        super(context, lsfRequestListener);
        this.editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        this.lsfLoginRequestSuccessful = lsfLoginRequestSuccessful;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful() && response.body() != null) {
            String content = super.responseToString(response.body());
            validateResponse(content);
        } else {
            super.failedToLogIn();
        }
    }

    private void validateResponse(String content) {
        if (content.contains(getContext().getString(R.string.result_login_failed))) {
            loginFailed();
        } else {
            extractCookie(content);
        }
    }

    private void loginFailed() {
        super.failedToLogIn();
        editor.putBoolean(LOGGED_IN, false).apply();
        super.getLsfRequestListener().onLoginFailed();
    }

    private void extractCookie(String content) {
        Pattern pattern = Pattern.compile("jsessionid=(.*?)\\?");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            saveCookie(matcher.group(1));
            lsfLoginRequestSuccessful.onLoginRequestSuccessful();
        } else {
            super.failedToLogIn();
        }
    }

    private void saveCookie(String cookie) {
        editor.putString(COOKIE, cookie);
        editor.putLong(COOKIE_TIME, System.currentTimeMillis() + 28 * 60 * 1000);
        editor.putBoolean(LOGGED_IN, true);
        editor.apply();
    }
}
