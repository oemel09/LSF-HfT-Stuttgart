package de.oemel09.lsf.api;

import android.annotation.SuppressLint;
import android.content.Context;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.oemel09.lsf.api.callback.LsfAsiCallback;
import de.oemel09.lsf.api.callback.LsfGradeDetailsCallback;
import de.oemel09.lsf.api.callback.LsfGradesCallback;
import de.oemel09.lsf.api.callback.LsfLoginCallback;
import de.oemel09.lsf.api.listeners.LsfGradeDetailsSuccessful;
import de.oemel09.lsf.api.listeners.LsfGradesSuccessful;
import de.oemel09.lsf.api.listeners.LsfRequestListener;

public class LsfLoader {

    private Context context;
    private LsfApi lsfApi;

    private LsfRequestListener lsfRequestListener;
    private LsfGradesSuccessful lsfRequestSuccessful;

    private LsfLogin lsfLogin;

    @SuppressLint("CommitPrefEdits")
    public LsfLoader(Context context, LsfRequestListener lsfRequestListener) {
        this.context = context;
        this.lsfRequestListener = lsfRequestListener;
        this.lsfApi = LsfApiClient.getLsfApi();
        this.lsfLogin = new LsfLogin(context);
    }

    public void getGrades(LsfGradesSuccessful lsfRequestSuccessful1) {
        this.lsfRequestSuccessful = lsfRequestSuccessful1;
        lsfRequestListener.onRequestStart();
        if (lsfLogin.isCookieStillValid()) {
            loadGrades();
        } else {
            performLogin();
        }
    }

    public void getGradeDetails(String gradeDetails, LsfGradeDetailsSuccessful lsfGradeDetailsSuccessful) {
        String nodeId = extractNodeId(gradeDetails);
        lsfRequestListener.onRequestStart();
        LsfGradeDetailsCallback callback = new LsfGradeDetailsCallback(context, lsfRequestListener, lsfGradeDetailsSuccessful);
        lsfApi.loadGradeDetails(lsfLogin.getCookie(), lsfLogin.getAsi(), nodeId).enqueue(callback);
    }

    private String extractNodeId(String link) {
        Pattern pattern = Pattern.compile("nodeID=(.*?)&");
        Matcher matcher = pattern.matcher(link);
        String nodeId = null;
        if (matcher.find()) {
            nodeId = decodeNoteId(matcher.group(1));
        }
        return nodeId;
    }

    private String decodeNoteId(String nodeId) {
        try {
            return URLDecoder.decode(nodeId, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void performLogin() {
        LsfLoginCallback callback = new LsfLoginCallback(context, lsfRequestListener, this::getAsiToken);
        lsfApi.performLogin(lsfLogin.getFormData()).enqueue(callback);
    }

    private void getAsiToken() {
        LsfAsiCallback callback = new LsfAsiCallback(context, lsfRequestListener, this::loadGrades);
        lsfApi.loadAsiToken(lsfLogin.getCookie()).enqueue(callback);
    }

    private void loadGrades() {
        LsfGradesCallback callback = new LsfGradesCallback(context, lsfRequestListener, lsfRequestSuccessful);
        lsfApi.loadGrades(lsfLogin.getCookie(), lsfLogin.getAsi()).enqueue(callback);
    }
}
