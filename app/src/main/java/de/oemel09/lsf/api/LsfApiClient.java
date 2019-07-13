package de.oemel09.lsf.api;

import android.content.Context;
import android.preference.PreferenceManager;

import retrofit2.Retrofit;

import static de.oemel09.lsf.MainActivity.DOMAIN;

class LsfApiClient {

    private static final String HTTPS = "https://";
    private static final String BASE_PATH = "/qisserver/";

    private static Retrofit createNewRetrofitInstance(Context context) {
        String domain = PreferenceManager.getDefaultSharedPreferences(context).getString(DOMAIN, null);
        String baseUrl = HTTPS + domain + BASE_PATH;
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .build();
    }

    static LsfApi getLsfApi(Context context) {
        return createNewRetrofitInstance(context).create(LsfApi.class);
    }
}
