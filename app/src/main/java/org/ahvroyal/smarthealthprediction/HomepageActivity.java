package org.ahvroyal.smarthealthprediction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomepageActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private DatabaseReference dbReference;
    private String userID;

    private ImageButton logout, robot, devOps;
    private CardView cardView;
    private TextView cvText;

    private int ageUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        logout = findViewById(R.id.ibLogout);
        robot = findViewById(R.id.ibRobot);
        devOps = findViewById(R.id.ibDevOps);
        cardView = findViewById(R.id.cardView);
        cvText = findViewById(R.id.tvUserInfo);

        logout.setOnClickListener(this);
        robot.setOnClickListener(this);
        devOps.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        fetchData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }

    private void fetchData() {

        dbReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    String name = userProfile.getName();
                    String age = userProfile.getAge();
                    String email = userProfile.getEmail();
                    String height = userProfile.getHeight();
                    String weight = userProfile.getWeight();

                    writeData(name, age, email, height, weight);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomepageActivity.this, "Couldn't fetch your data !", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void writeData(String name, String age, String email, String height, String weight) {

        StringBuilder userInfo = new StringBuilder();
        userInfo.append("<b>Name : </b>").append(name).append("<br>").append("<b>Age : </b>").append(age).append("<br>");
        userInfo.append("<b>Email : </b>").append(email).append("<br>");
        userInfo.append("<b>Height : </b>").append(height).append("<br>").append("<b>Weight : </b>").append(weight);

        cvText.setText(Html.fromHtml(String.valueOf(userInfo)));
        ageUser = (2021 - Integer.parseInt(age));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
            case R.id.ibRobot:
                Intent intent = new Intent(HomepageActivity.this, HealthPredictionActivity.class);
                intent.putExtra("age", ageUser);
                intent.putExtra("gender", "male");
                startActivity(intent);
                break;
            case R.id.ibDevOps:
                receiveToken();
                break;
        }
    }

    private void receiveToken() {

        AlertDialog.Builder alertDialogToken = new AlertDialog.Builder(HomepageActivity.this);
        View view = getLayoutInflater().inflate(R.layout.developer_options, null);

        EditText etUserToken = view.findViewById(R.id.etUserToken);
        Button btnSubmitToken = view.findViewById(R.id.btnSubmitToken);

        alertDialogToken.setView(view);
        AlertDialog dialog = alertDialogToken.create();
        dialog.show();

        btnSubmitToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save to shared prefs
                String tokenValue = etUserToken.getText().toString();

                SharedPreferences sharedPreferences = HomepageActivity.this.getSharedPreferences("shared_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token_key", tokenValue);
                editor.apply();

                dialog.dismiss();
            }
        });


    }

    public void editInfo(View view) {
        Intent intent = new Intent(HomepageActivity.this, EditUserInfoActivity.class);
        startActivity(intent);
    }

}