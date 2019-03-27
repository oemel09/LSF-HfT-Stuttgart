package de.oemel09.lsf.api.callback;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import de.oemel09.lsf.R;
import de.oemel09.lsf.api.listeners.LsfRequestListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LsfCallback implements Callback<ResponseBody> {

    private Context context;
    private LsfRequestListener lsfRequestListener;

    LsfCallback(Context context, LsfRequestListener lsfRequestListener) {
        this.context = context;
        this.lsfRequestListener = lsfRequestListener;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

    }

    String responseToString(ResponseBody body) {
        try {
            return body.string();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        failedToLogIn();
    }

    void failedToLogIn() {
        lsfRequestListener.onRequestFailed();
        Toast.makeText(context, context.getString(R.string.result_login_failed), Toast.LENGTH_SHORT).show();
    }

    Context getContext() {
        return context;
    }

    LsfRequestListener getLsfRequestListener() {
        return lsfRequestListener;
    }
}
