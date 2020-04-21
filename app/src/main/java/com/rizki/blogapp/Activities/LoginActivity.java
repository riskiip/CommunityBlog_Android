package com.rizki.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rizki.blogapp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etMail, etPass;
    private Button btnLogin;
    private ProgressBar prosesBarLogin;
    private ImageView loginImageUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etMail          = findViewById(R.id.login_mail);
        etPass          = findViewById(R.id.login_pass);
        btnLogin        = findViewById(R.id.login_btn);
        prosesBarLogin  = findViewById(R.id.login_progressBar);
        loginImageUser  = findViewById(R.id.login_photo);
        mAuth           = FirebaseAuth.getInstance();

        prosesBarLogin.setVisibility(View.INVISIBLE);

        loginImageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesBarLogin.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String email = etMail.getText().toString();
                final String password = etPass.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    showMessage("Tolong lengkapi data nya gan");
                    btnLogin.setVisibility(View.VISIBLE);
                    prosesBarLogin.setVisibility(View.INVISIBLE);
                }
                else{
                    signIn(email, password);
                }
            }
        });
    }

    private void signIn(String email, String password) {
        //TODO: Signin pake firebase auth dengan parameter email dan password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    prosesBarLogin.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();
                    Toast.makeText(getApplicationContext(), "Selamat Datang", Toast.LENGTH_LONG).show();
                }
                else{
                    showMessage(task.getException().getMessage());
                    btnLogin.setVisibility(View.VISIBLE);
                    prosesBarLogin.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateUI() {
        //TODO: Intent ke Home Activity
        Intent i = new Intent(LoginActivity.this, CobaActivity.class);
        startActivity(i);
        finish();
    }

    private void showMessage(String message) {
        //TODO: Toast message untuk melengkapi data
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO: Ngecek session
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            updateUI();
        }
    }
}
