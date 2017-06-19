package com.epicodus.exquisite.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epicodus.exquisite.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.nickNameEditText) EditText mNicknameView;
    @Bind(R.id.emailEditText) EditText mEmailView;
    @Bind(R.id.passwordEditText) EditText mPasswordView;
    @Bind(R.id.passConfEditText) EditText mPassConfView;
    @Bind(R.id.createAccountButton) Button mCreateAccountButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        createAuthStateListener();

        mCreateAccountButton.setOnClickListener(this);
    }

    private void createAuthStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
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
    public void onClick(View v) {
        if (v == mCreateAccountButton) {
            createNewUser();
        }
    }

    private void createNewUser() {
        final String nickname = mNicknameView.getText().toString().trim();
        final String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        String passConf = mPassConfView.getText().toString().trim();

        if (!isValidEmail(email) || !isValidName(nickname) || !isValidPassword(password, passConf)) return;

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateAccountActivity.this, "Cool", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Oops", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        boolean isValid = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isValid) {
            mEmailView.setError("Valid email required");
            return false;
        }
        return true;
    }

    private boolean isValidName(String name) {
        if (name.equals("")) {
            mNicknameView.setError("Enter your nickname");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confPass) {
        if (password.length() < 6) {
            mPasswordView.setError("Password must be at least 6 characters");
            return false;
        } else if (!password.equals(confPass)) {
            mPassConfView.setError("Passwords must match");
            return false;
        }
        return true;
    }
}
