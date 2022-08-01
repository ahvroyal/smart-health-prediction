package org.ahvroyal.smarthealthprediction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button resetPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.etEmailReset);
        resetPassword = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.pbResetPassword);

        mAuth = FirebaseAuth.getInstance();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rstPassword();
            }
        });
    }

    private void rstPassword() {

        String strEmail = email.getText().toString().trim();

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

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your email to reset your password !", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(ForgotPasswordActivity.this, "Reset password failed !, please try again !", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

}