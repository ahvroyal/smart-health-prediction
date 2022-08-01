package org.ahvroyal.smarthealthprediction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditUserInfoActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<Void>{

    private EditText name, age, height, weight;
    private Button submit;
    private ProgressBar progressBar;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Users");
    private String userID = user.getUid();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        name = findViewById(R.id.etNameE);
        age = findViewById(R.id.etAgeE);
        height = findViewById(R.id.etHeightE);
        weight = findViewById(R.id.etWeightE);
        submit = findViewById(R.id.btnPushEdit);
        progressBar = findViewById(R.id.pbEdit);

        settingCurrentUserInfo();

        submit.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    private void settingCurrentUserInfo() {
        // setting current user info (read from database) to the displayed edit texts.
        dbReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    String nameDB = userProfile.getName();
                    String ageDB = userProfile.getAge();
                    String heightDB = userProfile.getHeight();
                    String weightDB = userProfile.getWeight();

                    updateETs(nameDB, ageDB, heightDB, weightDB);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditUserInfoActivity.this, "Couldn't fetch your data !", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void updateETs(String nameDB, String ageDB, String heightDB, String weightDB) {
        name.setText(nameDB);
        age.setText(ageDB);
        height.setText(heightDB);
        weight.setText(weightDB);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPushEdit:
                editUserInfo();
                break;
        }
    }

    private void editUserInfo() {

        String strName = name.getText().toString().trim();
        String strAge = age.getText().toString().trim();
        String strHeight = height.getText().toString().trim();
        String strWeight = weight.getText().toString().trim();

        if (strName.isEmpty()){
            name.setError("Full Name is required !");
            name.requestFocus();
            return;
        }

        if (strAge.isEmpty()){
            age.setError("Age is required !");
            age.requestFocus();
            return;
        }

        if (strHeight.isEmpty()){
            height.setError("Height is required !");
            height.requestFocus();
            return;
        }

        if (strWeight.isEmpty()){
            weight.setError("Weight is required !");
            weight.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // editing user name, age, height, weight from database
        dbReference.child(userID).child("name").setValue(strName).addOnCompleteListener(this);
        dbReference.child(userID).child("age").setValue(strAge).addOnCompleteListener(this);
        dbReference.child(userID).child("height").setValue(strHeight).addOnCompleteListener(this);
        dbReference.child(userID).child("weight").setValue(strWeight).addOnCompleteListener(this);

    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            Toast.makeText(EditUserInfoActivity.this, "Your profile updated successfully !", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            finish();
        }else {
            Toast.makeText(EditUserInfoActivity.this, "Something wrong happened, please try again !", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

}