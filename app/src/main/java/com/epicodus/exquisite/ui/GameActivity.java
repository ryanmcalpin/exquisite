package com.epicodus.exquisite.ui;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

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
    @Bind(R.id.scrollView) ScrollView mScrollView;
    @Bind(R.id.checkBox) CheckBox mAddParagraphCheckBox;
    @Bind(R.id.shareStory) Button mShareStoryButton;

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

        mOwnerView.setText("Game owner: " + mGame.getOwnerName());
        mCollaboratorView.setText("Collaborator: " + mGame.getCollaboratorName());

        ArrayList<String> ownerSentences = (ArrayList<String>) mGame.getOwnerSentences();
        ArrayList<String> collaboratorSentences = (ArrayList<String>) mGame.getCollaboratorSentences();
        if (collaboratorSentences.size() < ownerSentences.size()) {
            collaboratorSentences.add("");
        }
        String story = "    ";
        for (int i = 0; i < ownerSentences.size(); i++) {
            story = story.concat(ownerSentences.get(i) + " ");
            story = story.concat(collaboratorSentences.get(i) + " ");
        }
        mStoryView.setText(story);

        if (mGame.getCollaboratorSentences().get(mGame.getCollaboratorSentences().size()-1).equals("")) {
            if (mUser.getUid().equals(mGame.getOwnerUid())) {
                mNewSentenceView.setVisibility(View.GONE);
                mSubmitButton.setVisibility(View.GONE);
                mAddParagraphCheckBox.setVisibility(View.GONE);
            }
        } else {
            if (!mUser.getUid().equals(mGame.getOwnerUid())) {
                mNewSentenceView.setVisibility(View.GONE);
                mSubmitButton.setVisibility(View.GONE);
                mAddParagraphCheckBox.setVisibility(View.GONE);
            }
        }

        scrollToBottom();
        mNewSentenceView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                scrollToBottom();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSubmitButton.setOnClickListener(this);
        mShareStoryButton.setOnClickListener(this);
    }

    private void scrollToBottom() {
        new CountDownTimer(500, 20) {
            public void onTick(long millisUntilFinished) {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
            public void onFinish() {
            }
        }.start();
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
            if (mAddParagraphCheckBox.isChecked()) {
                sentence = "\n    " + sentence;
            }

            ArrayList<String> userSentences;
            if (mGame.getCollaboratorSentences().get(mGame.getCollaboratorSentences().size()-1).equals("")) {
                userSentences = (ArrayList<String>) mGame.getCollaboratorSentences();
                userSentences.set(userSentences.size()-1, sentence);
                mGame.setCollaboratorSentences(userSentences);
            } else {
                userSentences = (ArrayList<String>) mGame.getOwnerSentences();
                userSentences.add(sentence);
                mGame.setOwnerSentences(userSentences);
            }

            DatabaseReference ownerGameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(mGame.getOwnerUid()).child(mGame.getFirebaseKey());
            DatabaseReference collaboratorGameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(mGame.getCollaboratorUid()).child(mGame.getFirebaseKey());
            ownerGameRef.setValue(mGame);
            collaboratorGameRef.setValue(mGame).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(GameActivity.this, GameActivity.class);
                        intent.putExtra("game", Parcels.wrap(mGame));
                        startActivity(intent);
                    }
                }
            });
        }

        if (view == mShareStoryButton) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {mUser.getEmail()}); // recipients
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "An Exquisite story for you!");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "    " + mStoryView.getText().toString());
            startActivity(emailIntent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GameActivity.this, UserGamesActivity.class);
        startActivity(intent);
    }
}
