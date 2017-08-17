package com.epicodus.exquisite.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.epicodus.exquisite.ConfirmDialogFragment;
import com.epicodus.exquisite.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.newGameButton) Button mNewGameButton;
    @Bind(R.id.logOutLink) TextView mLogOutLink;
    @Bind(R.id.welcomeView) TextView mWelcomeView;
    @Bind(R.id.myGamesButton) Button mMyGamesButton;
    @Bind(R.id.myInvitesButton) Button mMyInvitesButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mWelcomeView.setText("Welcome, " + user.getDisplayName() + "!");
                } else {

                }
            }
        };

        mNewGameButton.setOnClickListener(this);
        mLogOutLink.setOnClickListener(this);
        mMyGamesButton.setOnClickListener(this);
        mMyInvitesButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mNewGameButton) {
            Intent intent = new Intent(MainActivity.this, CreateGameActivity.class);
            startActivity(intent);
        }
        if (v == mLogOutLink) {
            logout();
        }
        if (v == mMyGamesButton) {
            Intent intent = new Intent(MainActivity.this, UserGamesActivity.class);
            startActivity(intent);
        }
        if (v == mMyInvitesButton) {
            Intent intent = new Intent(MainActivity.this, InvitesActivity.class);
            startActivity(intent);
        }
        overridePendingTransition(0, 0);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        confirmDialogFragment.show(fm, "Fragment");
    }
}
