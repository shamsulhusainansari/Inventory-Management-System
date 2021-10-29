package com.knoxtech.valves;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextInputEditText email, pass;
    FirebaseAuth mAuth;
    LinearProgressIndicator progressIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            finish();
        } else {
            Log.i("MainActivity","No user is sign in");
        }
        email = findViewById(R.id.getEmail);
        pass = findViewById(R.id.getPass);
        progressIndicator = findViewById(R.id.ProgressBar);
        findViewById(R.id.authLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(pass.getText())) {
                    Toast.makeText(MainActivity.this, "Please Enter Details!!!", Toast.LENGTH_SHORT).show();
                }else {
                    progressIndicator.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(String.valueOf(email.getText()), String.valueOf(pass.getText()))
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Please Enter Correct Details", Toast.LENGTH_SHORT).show();
                                        progressIndicator.setVisibility(View.GONE);
                                    }else {
                                        progressIndicator.setVisibility(View.GONE);
                                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}