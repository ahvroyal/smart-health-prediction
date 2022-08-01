package org.ahvroyal.smarthealthprediction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.ArrayList;

public class TreatmentActivity extends AppCompatActivity {

    private Cure cure;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);

        textView = findViewById(R.id.tvTreatment);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("myBundle2");
        if (args != null) {
            cure = (Cure) args.getSerializable("data2");

            textView.setMovementMethod(new ScrollingMovementMethod());

            if (cure != null) {

                StringBuilder result = new StringBuilder();
                result.append("<font color=#167A1A><b>Issue name : </b></font>").append(cure.getName()).append("<br>").append("<br>");
                result.append("<font color=#167A1A><b>Description : </b></font>").append(cure.getDescription()).append("<br>").append("<br>");
                result.append("<font color=#167A1A><b>Possible symptoms : </b></font>").append(cure.getPossibleSymptoms()).append("<br>").append("<br>");
                result.append("<font color=#167A1A><b>Treatment : </b></font>").append(cure.getTreatmentDescription()).append("<br>").append("<br>").append("<br>").append("<br>");

                textView.setText(Html.fromHtml(String.valueOf(result)));

            } else {
                textView.setText("Something wrong happened !");
                textView.setTextColor(Color.RED);
            }
        }
    }

}