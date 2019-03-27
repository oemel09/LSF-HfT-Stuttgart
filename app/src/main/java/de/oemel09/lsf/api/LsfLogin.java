package de.oemel09.lsf.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import de.oemel09.lsf.R;

import static de.oemel09.lsf.MainActivity.PASSWORD;
import static de.oemel09.lsf.MainActivity.USERNAME;
import static de.oemel09.lsf.api.LsfApi.ASI;
import static de.oemel09.lsf.api.LsfApi.COOKIE;

public class LsfLogin {

    private static final String LSF_USERNAME_KEY = "asdf";
    private static final String LSF_PASSWORD_KEY = "fdsa";
    private static final String LSF_SUBMIT_KEY = "submit";

    public static final String COOKIE_TIME = "COOKIE_TIME";

    private static final String JSESSION_ID = "JSESSIONID=%s";

    private final SharedPreferences prefs;

    private String username;
    private String password;
    private String submit;

    LsfLogin(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.username = prefs.getString(USERNAME, null);
        this.password = prefs.getString(PASSWORD, null);
        this.submit = context.getString(R.string.submit_value);
    }

    Map<String, String> getFormData() {
        HashMap<String, String> formData = new HashMap<>();
        formData.put(LSF_USERNAME_KEY, username);
        formData.put(LSF_PASSWORD_KEY, password);
        formData.put(LSF_SUBMIT_KEY, submit);
        return formData;
    }

    boolean isCookieStillValid() {
        return prefs.getLong(COOKIE_TIME, -1) > System.currentTimeMillis();
    }

    String getAsi() {
        return prefs.getString(ASI, "");
    }

    String getCookie() {
        String cookie = prefs.getString(COOKIE, "");
        return String.format(JSESSION_ID, cookie);
    }
}
