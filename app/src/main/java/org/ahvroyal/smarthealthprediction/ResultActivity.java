package org.ahvroyal.smarthealthprediction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button button;
    ProgressBar progressBar;
    private ArrayList<Diagnosis> diagnosisArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textView = findViewById(R.id.tvResult);
        editText = findViewById(R.id.etIssue);
        button = findViewById(R.id.btnShowDec);
        progressBar = findViewById(R.id.pbHealthResult);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("myBundle");
        if (args != null) {
            diagnosisArrayList = (ArrayList<Diagnosis>) args.getSerializable("data");

            // show the health prediction result to the patient
            if (diagnosisArrayList != null) {
                if (diagnosisArrayList.size() != 0) {
                    StringBuilder result = new StringBuilder();

                    int items = 0;
                    for (int i = 0; i < diagnosisArrayList.size(); i++) {
                        if (items <= 2) {
                            Diagnosis curr = diagnosisArrayList.get(i);
                            result.append("<font color=#167A1A><b>Issue id : </b></font>").append(curr.getIssue().getId()).append("<br>");
                            result.append("<font color=#167A1A><b>Issue name : </b></font>").append("<b>").append(curr.getIssue().getName()).append("</b>").append("<br>");
                            result.append("<font color=#167A1A><b>Accuracy : </b></font>").append(curr.getIssue().getAccuracy()).append("<br>").append("<br>");

                            items++;
                        }else
                            break;
                    }
                    textView.setText(Html.fromHtml(String.valueOf(result)));

                }else {
                    textView.setText("No result for your symptoms ! try again !");
                    textView.setTextColor(Color.YELLOW);
                }
            }else {
                textView.setText("Something wrong happened !");
                textView.setTextColor(Color.RED);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

    }

    private void sendRequest() {

        String issueID = editText.getText().toString();
        String language = "en-gb";

        if (issueID.isEmpty()) {
            editText.setError("This field can't be empty !");
            editText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        OkHttpClient client = OkHttpSingleton.getInstance();

        SharedPreferences sharedPreferences = ResultActivity.this.getSharedPreferences("shared_prefs", MODE_PRIVATE);
        String tokenHardcoded = sharedPreferences.getString("token_key", "");
        String host = "sandbox-healthservice.priaid.ch";
        String urlIssues = "https://sandbox-healthservice.priaid.ch/issues/" + issueID + "/info";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(urlIssues).newBuilder();
        urlBuilder.addQueryParameter("token", tokenHardcoded);
        urlBuilder.addQueryParameter("language", language);

        String url = urlBuilder.build().toString();
        Log.i("URL is :", "sendRequest: " + url);;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("host", host)
                .addHeader("key", tokenHardcoded)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {

                    Cure cure = null;
                    try {
                        final Gson gson = new Gson();
                        cure = gson.fromJson(response.body().charStream(), Cure.class);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Cure finalCure = cure;
                    ResultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            taskDone(finalCure);
                        }
                    });

                }
            }
        });

    }

    public void taskDone(Cure cure) {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(ResultActivity.this, TreatmentActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("data2",(Serializable) cure);
        intent.putExtra("myBundle2", args);
        startActivity(intent);
    }

}