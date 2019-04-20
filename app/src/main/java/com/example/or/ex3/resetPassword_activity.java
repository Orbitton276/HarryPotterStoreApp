package com.example.or.ex3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class resetPassword_activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView resetPasswordEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        resetPasswordEmail =findViewById(R.id.tvEmailToReset);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onResetPasswordClicked(View v){

        String email = resetPasswordEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            displayMessage("Enter your email");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            displayMessage("Reset email has been sent");
                            Intent intent = new Intent(resetPassword_activity.this, signIn_activity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            displayMessage("Fail to reset password\ndetails: "+task.getException().getMessage());
                        }
                    }
                });
    }

    public void displayMessage(String message) {
        if (message != null) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

}
