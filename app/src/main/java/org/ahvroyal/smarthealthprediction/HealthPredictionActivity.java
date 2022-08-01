package org.ahvroyal.smarthealthprediction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HealthPredictionActivity extends AppCompatActivity {

    ListView sListView;
    ArrayList<SymptomModel> symptomsData;
    SymptomsAdapter symptomsAdapter;
    SymptomModel symptomModel;
    Button submit;
    ProgressBar progressBar;

    private ArrayList<Diagnosis> diagnosisArrayList;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_prediction);

        submit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.pbHealthPredict);

        sListView = findViewById(R.id.listview_list);
        symptomsData = new ArrayList<>();

        addSymptomsData();

        symptomsAdapter = new SymptomsAdapter(this, symptomsData);
        sListView.setAdapter(symptomsAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendRequest();
                } catch (IOException | ExecutionException | InterruptedException e) {
                    Log.d("error received : ", e.toString());
                }

            }
        });

    }

    private void sendRequest() throws IOException, ExecutionException, InterruptedException {
        progressBar.setVisibility(View.VISIBLE);

        String gender = "";
        String birth = "";
        String symptoms = "";
        String language = "en-gb";

        ArrayList<Integer> selectedSymptoms = new ArrayList<>();

        for (SymptomModel sm : symptomsData) {
            if (sm.isChecked())
                selectedSymptoms.add(sm.getId());
        }

        symptoms = String.valueOf(selectedSymptoms);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            birth = String.valueOf(bundle.getInt("age"));
            gender = bundle.getString("gender");
        }

        // making the request with okhttp
        // using singleton instead

        OkHttpClient client = OkHttpSingleton.getInstance();

        // authorization
//        String urlAuth = "https://sandbox-authservice.priaid.ch/login";
//        String hostAuth = "authservice.priaid.ch";
//        String apiKey = "I am not going to tell you !";
//        String secretKey = "None of your business !";
//        String hashedCredentials = "";
//        String tokenFromAuth = "";
//        String json = "";
//        RequestBody body = RequestBody.create(json, JSON);
//
//        hashedCredentials = hmacDigest(urlAuth, secretKey, "HmacSHA1");
//        Log.i("Auth", "Hashed Credentials --> " + hashedCredentials);
//
//
//        Request request2 = new Request.Builder()
//                .url(urlAuth)
//                .post(body)
//                .addHeader("host", hostAuth)
//                .addHeader("Authorization", ("Bearer " + apiKey + ":" + hashedCredentials))
//                .build();


//        client.newCall(request2).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.i("Auth", "erooooor !");
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                Log.i("Auth", "response from auth is -----> " + response.body().string());
//            }
//        });

        SharedPreferences sharedPreferences = HealthPredictionActivity.this.getSharedPreferences("shared_prefs", MODE_PRIVATE);
        String tokenHardcoded = sharedPreferences.getString("token_key", "");
        String host = "sandbox-healthservice.priaid.ch";

        String urlDiagnosis = "https://sandbox-healthservice.priaid.ch/diagnosis";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(urlDiagnosis).newBuilder();
        urlBuilder.addQueryParameter("token", tokenHardcoded);
        urlBuilder.addQueryParameter("gender", gender);
        urlBuilder.addQueryParameter("year_of_birth", birth);
        urlBuilder.addQueryParameter("symptoms", symptoms);
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

                    diagnosisArrayList = new ArrayList<>();
                    try {

                        final Gson gson = new Gson();
                        Diagnosis[] diagnosisList = gson.fromJson(response.body().charStream(), Diagnosis[].class);
                        Collections.addAll(diagnosisArrayList, diagnosisList);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


//                    try {
//                        JSONArray jsonArray = new JSONArray(response.body().string());
//
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("Issue");
//
//                            Diagnosis diagnosis = new Diagnosis();
//
//                            diagnosis.setId(jsonObject.getString("ID"));
//                            diagnosis.setName(jsonObject.getString("Name"));
//                            diagnosis.setAccuracy(jsonObject.getString("Accuracy"));
//
//                            diagnosisArrayList.add(diagnosis);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    HealthPredictionActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            taskDone(diagnosisArrayList);
                        }
                    });
                }
            }
        });

    }

    public void taskDone(ArrayList<Diagnosis> diagnoses) {
        this.diagnosisArrayList = diagnoses;
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(HealthPredictionActivity.this, ResultActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("data",(Serializable) diagnosisArrayList);
        intent.putExtra("myBundle", args);
        startActivity(intent);
    }

//    public static String hmacDigest(String msg, String keyString, String algo) {
//        String digest = null;
//        try {
//            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
//            Mac mac = Mac.getInstance(algo);
//            mac.init(key);
//
//            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
//
//            StringBuffer hash = new StringBuffer();
//            for (int i = 0; i < bytes.length; i++) {
//                String hex = Integer.toHexString(0xFF & bytes[i]);
//                if (hex.length() == 1) {
//                    hash.append('0');
//                }
//                hash.append(hex);
//            }
//            digest = hash.toString();
//        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return digest;
//    }

    private void addSymptomsData() {
        //Symptom1
        symptomModel = new SymptomModel();
        symptomModel.setId(10);
        symptomModel.setSymptomName("Abdominal pain");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom2
        symptomModel = new SymptomModel();
        symptomModel.setId(238);
        symptomModel.setSymptomName("Anxiety");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom3
        symptomModel = new SymptomModel();
        symptomModel.setId(104);
        symptomModel.setSymptomName("Back pain");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom4
        symptomModel = new SymptomModel();
        symptomModel.setId(75);
        symptomModel.setSymptomName("Burning eyes");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom5
        symptomModel = new SymptomModel();
        symptomModel.setId(46);
        symptomModel.setSymptomName("Burning in the throat");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom6
        symptomModel = new SymptomModel();
        symptomModel.setId(170);
        symptomModel.setSymptomName("Cheek swelling");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom7
        symptomModel = new SymptomModel();
        symptomModel.setId(17);
        symptomModel.setSymptomName("Chest pain");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom8
        symptomModel = new SymptomModel();
        symptomModel.setId(31);
        symptomModel.setSymptomName("Chest tightness");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom9
        symptomModel = new SymptomModel();
        symptomModel.setId(175);
        symptomModel.setSymptomName("Chills");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom10
        symptomModel = new SymptomModel();
        symptomModel.setId(139);
        symptomModel.setSymptomName("Cold sweats");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom11
        symptomModel = new SymptomModel();
        symptomModel.setId(15);
        symptomModel.setSymptomName("Cough");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom12
        symptomModel = new SymptomModel();
        symptomModel.setId(207);
        symptomModel.setSymptomName("Dizziness");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom13
        symptomModel = new SymptomModel();
        symptomModel.setId(244);
        symptomModel.setSymptomName("Drooping eyelid");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom14
        symptomModel = new SymptomModel();
        symptomModel.setId(273);
        symptomModel.setSymptomName("Dry eyes");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom15
        symptomModel = new SymptomModel();
        symptomModel.setId(87);
        symptomModel.setSymptomName("Earache");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

//        //Symptom16
//        symptomModel = new SymptomModel();
//        symptomModel.setId(87);
//        symptomModel.setSymptomName("Earache");
//        symptomModel.setChecked(false);
//        symptomsData.add(symptomModel);

        //Symptom17
        symptomModel = new SymptomModel();
        symptomModel.setId(92);
        symptomModel.setSymptomName("Early satiety");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom18
        symptomModel = new SymptomModel();
        symptomModel.setId(287);
        symptomModel.setSymptomName("Eye pain");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom19
        symptomModel = new SymptomModel();
        symptomModel.setId(33);
        symptomModel.setSymptomName("Eye redness");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom20
        symptomModel = new SymptomModel();
        symptomModel.setId(153);
        symptomModel.setSymptomName("Fast, deepened breathing");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom21
        symptomModel = new SymptomModel();
        symptomModel.setId(76);
        symptomModel.setSymptomName("Feeling of foreign body in the eye");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom22
        symptomModel = new SymptomModel();
        symptomModel.setId(11);
        symptomModel.setSymptomName("Fever");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom23
        symptomModel = new SymptomModel();
        symptomModel.setId(57);
        symptomModel.setSymptomName("Going black before the eyes");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom24
        symptomModel = new SymptomModel();
        symptomModel.setId(9);
        symptomModel.setSymptomName("Headache");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom25
        symptomModel = new SymptomModel();
        symptomModel.setId(45);
        symptomModel.setSymptomName("Heartburn");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom26
        symptomModel = new SymptomModel();
        symptomModel.setId(122);
        symptomModel.setSymptomName("Hiccups");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom27
        symptomModel = new SymptomModel();
        symptomModel.setId(149);
        symptomModel.setSymptomName("Hot flushes");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom28
        symptomModel = new SymptomModel();
        symptomModel.setId(40);
        symptomModel.setSymptomName("Increased thirst");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom29
        symptomModel = new SymptomModel();
        symptomModel.setId(73);
        symptomModel.setSymptomName("Itching eyes");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom30
        symptomModel = new SymptomModel();
        symptomModel.setId(96);
        symptomModel.setSymptomName("Itching in the nose");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom31
        symptomModel = new SymptomModel();
        symptomModel.setId(35);
        symptomModel.setSymptomName("Lip swelling");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom32
        symptomModel = new SymptomModel();
        symptomModel.setId(235);
        symptomModel.setSymptomName("Memory gap");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom32
        symptomModel = new SymptomModel();
        symptomModel.setId(112);
        symptomModel.setSymptomName("Menstruation disorder");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom34
        symptomModel = new SymptomModel();
        symptomModel.setId(123);
        symptomModel.setSymptomName("Missed period");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom35
        symptomModel = new SymptomModel();
        symptomModel.setId(44);
        symptomModel.setSymptomName("Nausea");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom36
        symptomModel = new SymptomModel();
        symptomModel.setId(136);
        symptomModel.setSymptomName("Neck pain");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom37
        symptomModel = new SymptomModel();
        symptomModel.setId(114);
        symptomModel.setSymptomName("Nervousness");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom38
        symptomModel = new SymptomModel();
        symptomModel.setId(133);
        symptomModel.setSymptomName("Night cough");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom39
        symptomModel = new SymptomModel();
        symptomModel.setId(12);
        symptomModel.setSymptomName("Pain in the limbs");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom40
        symptomModel = new SymptomModel();
        symptomModel.setId(203);
        symptomModel.setSymptomName("Pain on swallowing");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom41
        symptomModel = new SymptomModel();
        symptomModel.setId(37);
        symptomModel.setSymptomName("Palpitations");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom42
        symptomModel = new SymptomModel();
        symptomModel.setId(140);
        symptomModel.setSymptomName("Paralysis");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom43
        symptomModel = new SymptomModel();
        symptomModel.setId(54);
        symptomModel.setSymptomName("Reduced appetite");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom44
        symptomModel = new SymptomModel();
        symptomModel.setId(14);
        symptomModel.setSymptomName("Runny nose");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom45
        symptomModel = new SymptomModel();
        symptomModel.setId(29);
        symptomModel.setSymptomName("Shortness of breath");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom46
        symptomModel = new SymptomModel();
        symptomModel.setId(124);
        symptomModel.setSymptomName("Skin rash");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom47
        symptomModel = new SymptomModel();
        symptomModel.setId(52);
        symptomModel.setSymptomName("Sleeplessness");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom48
        symptomModel = new SymptomModel();
        symptomModel.setId(95);
        symptomModel.setSymptomName("Sneezing");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom49
        symptomModel = new SymptomModel();
        symptomModel.setId(13);
        symptomModel.setSymptomName("Sore throat");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom50
        symptomModel = new SymptomModel();
        symptomModel.setId(64);
        symptomModel.setSymptomName("Sputum");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom51
        symptomModel = new SymptomModel();
        symptomModel.setId(179);
        symptomModel.setSymptomName("Stomach burning");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom52
        symptomModel = new SymptomModel();
        symptomModel.setId(28);
        symptomModel.setSymptomName("Stuffy nose");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom53
        symptomModel = new SymptomModel();
        symptomModel.setId(138);
        symptomModel.setSymptomName("Sweating");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom54
        symptomModel = new SymptomModel();
        symptomModel.setId(248);
        symptomModel.setSymptomName("Swollen glands in the armpits");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom55
        symptomModel = new SymptomModel();
        symptomModel.setId(169);
        symptomModel.setSymptomName("Swollen glands on the neck");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom56
        symptomModel = new SymptomModel();
        symptomModel.setId(211);
        symptomModel.setSymptomName("Tears");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom57
        symptomModel = new SymptomModel();
        symptomModel.setId(16);
        symptomModel.setSymptomName("Tiredness");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom58
        symptomModel = new SymptomModel();
        symptomModel.setId(115);
        symptomModel.setSymptomName("Tremor at rest");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom59
        symptomModel = new SymptomModel();
        symptomModel.setId(144);
        symptomModel.setSymptomName("Unconsciousness, short");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom60
        symptomModel = new SymptomModel();
        symptomModel.setId(101);
        symptomModel.setSymptomName("Vomiting");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom61
        symptomModel = new SymptomModel();
        symptomModel.setId(181);
        symptomModel.setSymptomName("Vomiting blood");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom62
        symptomModel = new SymptomModel();
        symptomModel.setId(56);
        symptomModel.setSymptomName("weakness");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom63
        symptomModel = new SymptomModel();
        symptomModel.setId(23);
        symptomModel.setSymptomName("Weight gain");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

        //Symptom64
        symptomModel = new SymptomModel();
        symptomModel.setId(30);
        symptomModel.setSymptomName("Wheezing");
        symptomModel.setChecked(false);
        symptomsData.add(symptomModel);

    }

}