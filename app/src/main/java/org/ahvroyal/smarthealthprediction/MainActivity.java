package org.ahvroyal.smarthealthprediction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private EditText email, password;
    private Button logIn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = findViewById(R.id.tvLaunchRegister);
        forgotPassword = findViewById(R.id.tvForgotPassword);
        email = findViewById(R.id.etEmailLogin);
        password = findViewById(R.id.etPasswordLogin);
        logIn = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.pbLogin);

        register.setOnClickListener(this);
        logIn.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvLaunchRegister:
                startActivity(new Intent(this, RegisterUserActivity.class));
                break;
            case R.id.tvForgotPassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            case R.id.btnLogin:
                loginUser();
                break;

        }
    }

    private void loginUser() {

        String strEmail = email.getText().toString().trim();
        String strPassword = password.getText().toString().trim();

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

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()){
                        startActivity(new Intent(MainActivity.this, HomepageActivity.class));
                    }else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Email is not verified ! please check your email for verification", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(MainActivity.this, "Login failed ! please check your credentials", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}