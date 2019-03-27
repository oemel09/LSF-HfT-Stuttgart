package de.oemel09.lsf.api;

import retrofit2.Retrofit;

class LsfApiClient {

    private static final String BASE_URL = "https://lsf.hft-stuttgart.de/qisserver/";

    private static Retrofit retrofit;
    private static LsfApi lsfApi;

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = createNewRetrofitInstance();
        }
        return retrofit;
    }

    private static Retrofit createNewRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
    }

    static LsfApi getLsfApi() {
        if (lsfApi == null) {
            lsfApi = getRetrofitInstance().create(LsfApi.class);
        }
        return lsfApi;
    }
}
