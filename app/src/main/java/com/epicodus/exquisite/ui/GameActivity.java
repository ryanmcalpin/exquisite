package com.epicodus.exquisite.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.ownerView) TextView mOwnerView;
    @Bind(R.id.collaboratorView) TextView mCollaboratorView;
    @Bind(R.id.storyTextView) TextView mStoryView;
    @Bind(R.id.newSentenceView) EditText mNewSentenceView;
    @Bind(R.id.submitButton) Button mSubmitButton;

    private Game mGame;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        mGame = Parcels.unwrap(intent.getParcelableExtra("game"));
        Log.d("LOGADOG1: ", mGame.getCollaboratorSentences().size() + "");

        mOwnerView.setText("Game owner: " + mGame.getOwnerName());
        mCollaboratorView.setText("Collaborator: " + mGame.getCollaboratorName());

        ArrayList<String> ownerSentences = (ArrayList<String>) mGame.getOwnerSentences();
        ArrayList<String> collaboratorSentences = (ArrayList<String>) mGame.getCollaboratorSentences();
        if (collaboratorSentences.size() < ownerSentences.size()) {
            collaboratorSentences.add("");
        }
        Log.d("LOGADOG2: ", mGame.getCollaboratorSentences().size() + "");
        String story = "";
        for (int i = 0; i < ownerSentences.size(); i++) {
            story = story.concat(ownerSentences.get(i) + " ");
            story = story.concat(collaboratorSentences.get(i) + " ");
        }
        mStoryView.setText(story);

        if (mGame.getCollaboratorSentences().size() < mGame.getOwnerSentences().size()) {
            if (!mUser.getDisplayName().equals(mGame.getOwnerName())) {
                mNewSentenceView.setVisibility(View.GONE);
                mSubmitButton.setVisibility(View.GONE);
            }
        } else {
            if (!mUser.getDisplayName().equals(mGame.getCollaboratorName())) {
                mNewSentenceView.setVisibility(View.GONE);
                mSubmitButton.setVisibility(View.GONE);
            }
        }

        mSubmitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mSubmitButton) {
            String sentence = mNewSentenceView.getText().toString();
            if (sentence.trim().length() < 2) {
                mNewSentenceView.setError("Enter the next line of the story");
                return;
            }
            while (sentence.substring(0, 1).equals(" ")) {
                sentence = sentence.substring(1);
            }
            while (sentence.substring(sentence.length()-1).equals(" ")) {
                sentence = sentence.substring(0, sentence.length()-1);
            }

            Pattern punctuation = Pattern.compile("[.?!]");
            Pattern endQuote = Pattern.compile("[\"\']");
            Matcher punctuationAsLast = punctuation.matcher(sentence.substring(sentence.length()-1));
            Matcher punctuationSecondToLast = punctuation.matcher(sentence.substring(sentence.length()-2, sentence.length()-1));
            Matcher quoteAsLast = endQuote.matcher(sentence.substring(sentence.length()-1));
            if (!quoteAsLast.matches() && !punctuationSecondToLast.matches() && !punctuationAsLast.matches()) {
                sentence = sentence.concat(".");
            } else if (quoteAsLast.matches() && !punctuationSecondToLast.matches()){
                sentence = sentence.substring(0, sentence.length()-1) + "." + sentence.substring(sentence.length()-1);
            }

            ArrayList<String> userSentences;
            if (mGame.getCollaboratorSentences().size() < mGame.getOwnerSentences().size()) {
                userSentences = (ArrayList<String>) mGame.getCollaboratorSentences();
                Log.d("LOGADOG: ", mGame.getCollaboratorSentences().size() + "");
                userSentences.add(sentence);
                Log.d("LOGADOG: ", mGame.getCollaboratorSentences().size() + "");
                mGame.setCollaboratorSentences(userSentences);
            } else {
                userSentences = (ArrayList<String>) mGame.getOwnerSentences();
                userSentences.add(sentence);
                mGame.setOwnerSentences(userSentences);
            }

            DatabaseReference ownerGameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(mGame.getOwnerUid()).child(mGame.getFirebaseKey());
            DatabaseReference collaboratorGameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(mGame.getCollaboratorUid()).child(mGame.getFirebaseKey());
            ownerGameRef.setValue(mGame);
            collaboratorGameRef.setValue(mGame);
        }
    }
}
