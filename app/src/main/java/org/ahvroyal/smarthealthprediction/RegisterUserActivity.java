package org.ahvroyal.smarthealthprediction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name, age, email, password, height, weight;
    private Button register;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        name = findViewById(R.id.etName);
        age = findViewById(R.id.etAge);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        height = findViewById(R.id.etHeight);
        weight = findViewById(R.id.etWeight);
        register = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.pbRegister);

        register.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                registerUser();
                break;
        }
    }

    private void registerUser() {

        String strName = name.getText().toString().trim();
        String strAge = age.getText().toString().trim();
        String strEmail = email.getText().toString().trim();
        String strPassword = password.getText().toString().trim();
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

        if (strEmail.isEmpty()){
            email.setError("Email Address is required !");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){
            email.setError("Please provide valid email !");
            email.requestFocus();
            return;
        }

        if (strPassword.isEmpty()){
            password.setError("Password is required !");
            password.requestFocus();
            return;
        }

        if (strPassword.length() < 6){
            password.setError("Password length must be at least 6 characters !");
            password.requestFocus();
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

        mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(strName, strAge, strEmail, strHeight, strWeight);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterUserActivity.this, "Registration has completed successfully !", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        finish();
                                        Log.d("debug", "onComplete: register succeeded");
                                        // redirect to login screen

                                    }else {
                                        Toast.makeText(RegisterUserActivity.this, "Registration failed, Please try again !", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        Log.d("debug", "onComplete: database failed");
                                    }

                                }
                            });
                        }else {
                            Toast.makeText(RegisterUserActivity.this, "Registration failed, Please try again !", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            Log.d("debug", "onComplete: register failed");
                        }
                    }
                });

    }

}