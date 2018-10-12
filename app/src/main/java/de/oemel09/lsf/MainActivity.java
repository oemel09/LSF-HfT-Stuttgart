package de.oemel09.lsf;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity {

    private static final String LSF_LOGIN = "https://lsf.hft-stuttgart.de/qisserver/rds?state=user&type=1&category=auth.login&startpage=portal.vm&breadCrumbSource=portal";
    private static final String LSF_EXAM_ADMINISTRATION = "https://lsf.hft-stuttgart.de/qisserver/rds?state=change&type=1&moduleParameter=studyPOSMenu&nextdir=change&next=menu.vm&subdir=applications&xml=menu&purge=y&navigationPosition=functions%2CstudyPOSMenu&breadcrumb=studyPOSMenu&topitem=functions&subitem=studyPOSMenu";
    private static final String ASI_PLACEHOLDER = "{your_asi_here}";
    private static final String LSF_GRADES = "https://lsf.hft-stuttgart.de/qisserver/rds?state=notenspiegelStudent&next=list.vm&nextdir=qispos/notenspiegel/student&createInfos=Y&struct=auswahlBaum&nodeID=auswahlBaum%7Cabschluss%3Aabschl%3D84%2Cstgnr%3D1&expand=0&asi=" + ASI_PLACEHOLDER + "#auswahlBaum%7Cabschluss%3Aabschl%3D84%2Cstgnr%3D1";
    private static final String LOGGED_IN = "LOGGED_IN";
    private static final String COOKIE = "COOKIE";
    private static final String COOKIE_TIME = "COOKIE_TIME";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String JSESSION_ID = "JSESSIONID=";

    private String cookie;
    private String asi;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private GradeAdapter gradesAdapter;
    private ArrayList<Grade> grades;
    private ProgressDialog dialog;

    private enum Order {
        FIRST, SECOND, THIRD
    }

    @SuppressLint({"StaticFieldLeak", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        if (prefs.getBoolean(LOGGED_IN, false)) {
            setupMain();
        } else {
            setupLogin();
        }
    }

    private void setupMain() {
        setContentView(R.layout.activity_main);

        RecyclerView rvGrades = findViewById(R.id.rv_grades);
        rvGrades.setHasFixedSize(true);
        rvGrades.setLayoutManager(new LinearLayoutManager(this));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        rvGrades.addItemDecoration(itemDecoration);
        grades = new ArrayList<>();
        gradesAdapter = new GradeAdapter(grades);
        rvGrades.setAdapter(gradesAdapter);
        login();
    }

    private void setupLogin() {
        setContentView(R.layout.login);
        if (prefs.getString(USERNAME, null) != null) {
            ((TextInputEditText) findViewById(R.id.et_username)).setText(prefs.getString(USERNAME, null));
        }
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            editor.putString(USERNAME, Objects.requireNonNull(((TextInputEditText) findViewById(R.id.et_username)).getText()).toString());
            editor.putString(PASSWORD, Objects.requireNonNull(((TextInputEditText) findViewById(R.id.et_password)).getText()).toString());
            editor.apply();
            setupMain();
        });
    }

    private void login() {
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.loading_grades));
        dialog.show();
        // if cookie is still good
        if (prefs.getLong(COOKIE_TIME, -1) > System.currentTimeMillis()) {
            cookie = prefs.getString(COOKIE, "");
            asi = prefs.getString("ASI", "");
            getGrades();
        } else {
            new FetchData(Order.FIRST) {
                @Override
                void answer(String result) {
                    if (!result.contains(getString(R.string.result_login_failed))) {
                        Pattern pattern = Pattern.compile("jsessionid=(.*?)\\?");
                        Matcher matcher = pattern.matcher(result);
                        if (matcher.find()) {
                            cookie = matcher.group(1);
                            editor.putString(COOKIE, cookie);
                            editor.putLong(COOKIE_TIME, System.currentTimeMillis() + 28 * 60 * 1000);
                            editor.putBoolean(LOGGED_IN, true);
                            editor.apply();
                            getAsi();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                        editor.putBoolean(LOGGED_IN, false);
                        editor.apply();
                        setupLogin();
                    }
                }
            }.execute();
        }
    }

    private void getAsi() {
        new FetchData(Order.SECOND) {
            @Override
            void answer(String result) {
                Pattern pattern = Pattern.compile("asi=(.*?)\"");
                Matcher matcher = pattern.matcher(result);
                if (matcher.find()) {
                    asi = matcher.group(1);
                    editor.putString("ASI", asi);
                    editor.apply();
                    getGrades();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            }
        }.execute();
    }

    private void getGrades() {
        new FetchData(Order.THIRD) {
            @Override
            void answer(String result) {
                parseGrades(result);
            }
        }.execute();
    }

    private void parseGrades(String html) {
        Document doc = Jsoup.parse(html);
        Elements tbody = doc.select("tbody");
        int i = 0;
        for (Element e : tbody) {
            if (i == 0) {
                i++;
                continue;
            }
            Elements trs = e.select("tr");
            int j = 0;
            for (Element tr : trs) {
                List<Node> children = tr.childNodes();
                int childrenCount = 0;
                for (Node node : children) {
                    if (node instanceof Element) {
                        childrenCount++;
                    }
                }
                if (childrenCount == 9) {
                    if (j == 0) {
                        j++;
                        continue;
                    }
                    Grade grade = new Grade();
                    grade.setGradeNr(((Element) children.get(1)).text());
                    grade.setGradeName(((Element) children.get(3)).text());
                    grade.setSemester(((Element) children.get(5)).text());
                    String tmpGrade = ((Element) children.get(7)).text();
                    grade.setGrade((tmpGrade.isEmpty() ? " - " : tmpGrade));
                    grade.setState(((Element) children.get(9)).text());
                    grade.setEcts(((Element) children.get(11)).text());
                    grade.setAttempt(((Element) children.get(15)).text());
                    String tmpDate = ((Element) children.get(17)).text();
                    grade.setDate((tmpDate.isEmpty()) ? " - " : tmpDate);
                    grades.add(grade);
                }
            }
        }
        Collections.sort(grades, (o1, o2) -> {
            String one = getNumberToCompare(o1.getSemester());
            String two = getNumberToCompare(o2.getSemester());

            if (one.compareTo(two) == 0) {
                return Integer.parseInt(o2.getGradeNr()) - Integer.parseInt(o1.getGradeNr());
            } else {
                return one.compareTo(two) * -1;
            }
        });
        dialog.cancel();
        gradesAdapter.notifyDataSetChanged();
    }

    private String getNumberToCompare(String semester) {
        if (semester.startsWith("SoSe")) {
            String[] pre1 = semester.split("SoSe ");
            return pre1[1] + "1";
        } else {
            String[] pre3 = semester.split("WiSe ");
            String[] pre4 = pre3[1].split("/");
            return pre4[0] + "2";
        }
    }

    abstract class FetchData extends AsyncTask<String, String, String> {

        private URL url;
        private Order order;

        FetchData(Order order) {
            try {
                String target = null;
                switch (order) {
                    case FIRST:
                        target = LSF_LOGIN;
                        break;

                    case SECOND:
                        target = LSF_EXAM_ADMINISTRATION;
                        break;

                    case THIRD:
                        target = LSF_GRADES.replace(ASI_PLACEHOLDER, asi);
                        break;
                }
                this.url = new URL(target);
                this.order = order;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            return fetchData();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            answer(result);
        }

        private String fetchData() {

            try {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
                httpsConnection.setRequestMethod("POST");
                httpsConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpsConnection.setDoInput(true);

                HashMap<String, String> params = new HashMap<>();
                switch (order) {
                    case FIRST:
                        params.put("asdf", prefs.getString(USERNAME, null));
                        params.put("fdsa", prefs.getString(PASSWORD, null));
                        params.put("submit", getString(R.string.submit_value));
                        break;

                    case SECOND:
                        httpsConnection.setRequestProperty(COOKIE, JSESSION_ID + cookie);
                        break;

                    case THIRD:
                        httpsConnection.setRequestProperty(COOKIE, JSESSION_ID + cookie);
                        break;
                }

                OutputStream outputStream = new BufferedOutputStream(httpsConnection.getOutputStream());
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(getPostDataString(params));
                outputStreamWriter.flush();
                outputStreamWriter.close();

                if (httpsConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    Charset charset = Charset.forName("UTF-8");
                    InputStreamReader stream = new InputStreamReader(httpsConnection.getInputStream(), charset);
                    BufferedReader reader = new BufferedReader(stream);
                    StringBuilder responseBuffer = new StringBuilder();

                    String answer;
                    while ((answer = reader.readLine()) != null) {
                        responseBuffer.append(answer);
                    }
                    return responseBuffer.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "ERROR";
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();
        }

        abstract void answer(String result);
    }
}
