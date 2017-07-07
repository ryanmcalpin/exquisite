package com.epicodus.exquisite.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.exquisite.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.createAccountLink) TextView mCreateAccountLink;
    @Bind(R.id.emailEditText) EditText mEmailView;
    @Bind(R.id.passwordEditText) EditText mPasswordView;
    @Bind(R.id.logInButton) Button mLogInButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mAuthProgDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        };

        createAuthProgDialog();

        mCreateAccountLink.setOnClickListener(this);
        mLogInButton.setOnClickListener(this);
    }

    private void createAuthProgDialog() {
        mAuthProgDialog = new ProgressDialog(this);
        mAuthProgDialog.setTitle("Loading...");
        mAuthProgDialog.setMessage("Authenticating with Firebase...");
        mAuthProgDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        if (v == mCreateAccountLink) {
            Intent intent = new Intent(LogInActivity.this, CreateAccountActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        if (v == mLogInButton) {
            loginWithPassword();
        }
    }

    private void loginWithPassword() {
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        if (email.equals("")) {
            mEmailView.setError("Enter your email");
            return;
        }
        if (password.equals("")) {
            mPasswordView.setError("Enter your password");
            return;
        }
        mAuthProgDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mAuthProgDialog.dismiss();
                if (!task.isSuccessful()) {
                    Toast.makeText(LogInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
