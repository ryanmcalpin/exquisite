package com.epicodus.exquisite.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateGameActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.openingSentenceEditText) EditText mOpeningLineView;
    @Bind(R.id.createGameButton) Button mCreateGameButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgDialog;
    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        createProgDialog();

        mCreateGameButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mCreateGameButton) {
            String openingLine = mOpeningLineView.getText().toString();
            if (openingLine.trim().length() < 2) {
                mOpeningLineView.setError("Enter the first line of the story");
                return;
            }
            String userUid = mUser.getUid();
            String userName = mUser.getDisplayName();
            if (userName == null || userUid == null) {
                Intent intent = new Intent(CreateGameActivity.this, LogInActivity.class);
                startActivity(intent);
                return;
            }
            mProgDialog.show();
            while (openingLine.substring(0, 1).equals(" ")) {
                openingLine = openingLine.substring(1);
            }
            while (openingLine.substring(openingLine.length()-1).equals(" ")) {
                openingLine = openingLine.substring(0, openingLine.length()-1);
            }

            Pattern punctuation = Pattern.compile("[.?!]");
            Pattern endQuote = Pattern.compile("[\"\']");
            Matcher punctuationAsLast = punctuation.matcher(openingLine.substring(openingLine.length()-1));
            Matcher punctuationSecondToLast = punctuation.matcher(openingLine.substring(openingLine.length()-2, openingLine.length()-1));
            Matcher quoteAsLast = endQuote.matcher(openingLine.substring(openingLine.length()-1));
            if (!quoteAsLast.matches() && !punctuationSecondToLast.matches() && !punctuationAsLast.matches()) {
                openingLine = openingLine.concat(".");
            } else if (quoteAsLast.matches() && !punctuationSecondToLast.matches()){
                openingLine = openingLine.substring(0, openingLine.length()-1) + "." + openingLine.substring(openingLine.length()-1);
            }

            mGame = new Game(openingLine, userUid, userName);
            DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(userUid);
            DatabaseReference pushRef = gamesRef.push();
            String key = pushRef.getKey();
            mGame.setFirebaseKey(key);
            pushRef.setValue(mGame).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mProgDialog.dismiss();
                    Intent intent = new Intent(CreateGameActivity.this, InvitePlayerActivity.class);
                    intent.putExtra("game", Parcels.wrap(mGame));
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

        }
    }

    private void createProgDialog() {
        mProgDialog = new ProgressDialog(this);
        mProgDialog.setTitle("Loading...");
        mProgDialog.setMessage("Creating story...");
        mProgDialog.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
