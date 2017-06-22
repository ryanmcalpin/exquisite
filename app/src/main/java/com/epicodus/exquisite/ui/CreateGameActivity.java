package com.epicodus.exquisite.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

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

            Game newGame = new Game(openingLine, userUid, userName);
            DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(userUid);
            DatabaseReference pushRef = gamesRef.push();
            String key = pushRef.getKey();
            newGame.setFirebaseKey(key);
            pushRef.setValue(newGame);
            Intent intent = new Intent(CreateGameActivity.this, InvitePlayerActivity.class);
            intent.putExtra("newGame", Parcels.wrap(newGame));
            startActivity(intent);
        }
    }
}
