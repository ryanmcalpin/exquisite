package com.epicodus.exquisite.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.w3c.dom.Text;

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
    @Bind(R.id.shareStory) AppCompatImageView mShareStoryButton;
    @Bind(R.id.titleView) TextView mTitleView;
    @Bind(R.id.alternateTitleView) TextView mAltTitleView;
    @Bind(R.id.orView) TextView mOrView;
    @Bind(R.id.titleTextView) TextView mTitleTextView;
    @Bind(R.id.andView) TextView mAndView;

    private Game mGame;
    private FirebaseUser mUser;
    private SpannableStringBuilder mStringBuilder;
    private SpannableStringBuilder mStringBuilderOwner;
    private SpannableStringBuilder mStringBuilderCollaborater;
    private boolean mUserOwner;
    private boolean mUserTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        mGame = Parcels.unwrap(intent.getParcelableExtra("game"));

        //user is owner?
        if (mGame.getOwnerUid().equals(mUser.getUid())) {
            mUserOwner = true;
        } else {
            mUserOwner = false;
        }


        if (mUserOwner) {
            if (mGame.getOwnerTitle() != null) {
                mTitleView.setText(mGame.getOwnerTitle());
                mTitleTextView.setText(mGame.getOwnerTitle());
            }
            if (mGame.getCollaboratorTitle() != null) {
                mAltTitleView.setText(mGame.getCollaboratorTitle());
            } else {
                mOrView.setVisibility(View.GONE);
                mAltTitleView.setVisibility(View.GONE);
            }
        } else { //user is collaborator
            if (mGame.getCollaboratorTitle() != null) {
                mTitleView.setText(mGame.getCollaboratorTitle());
                mTitleTextView.setText(mGame.getCollaboratorTitle());
            }
            if (mGame.getOwnerTitle() != null) {
                mAltTitleView.setText(mGame.getOwnerTitle());
            } else {
                mOrView.setVisibility(View.GONE);
                mAltTitleView.setVisibility(View.GONE);
            }
        }

        mAndView.setText("&");

        mOwnerView.setText(mGame.getOwnerName());
        mCollaboratorView.setText(mGame.getCollaboratorName());

        ArrayList<String> ownerSentences = (ArrayList<String>) mGame.getOwnerSentences();
        ArrayList<String> collaboratorSentences = (ArrayList<String>) mGame.getCollaboratorSentences();
        if (collaboratorSentences.size() < ownerSentences.size()) {
            collaboratorSentences.add("");
        }


        mStringBuilder = new SpannableStringBuilder("    ");
        mStringBuilderOwner = new SpannableStringBuilder("    ");
        mStringBuilderCollaborater = new SpannableStringBuilder("    ");


        for (int i = 0; i < ownerSentences.size(); i++) {
            int start = mStringBuilder.length();
            mStringBuilder.append(ownerSentences.get(i) + " ");
            mStringBuilder.append(collaboratorSentences.get(i) + " ");
        }
for (int i = 0; i < ownerSentences.size(); i++) {
            int start = mStringBuilderOwner.length();
            mStringBuilderOwner.append(ownerSentences.get(i) + " ");
            mStringBuilderOwner.setSpan(new StyleSpan(Typeface.BOLD), start, mStringBuilderOwner.length() - 1, 0);
            mStringBuilderOwner.append(collaboratorSentences.get(i) + " ");
        }

        for (int i = 0; i < ownerSentences.size(); i++) {
            mStringBuilderCollaborater.append(ownerSentences.get(i) + " ");
            int start = mStringBuilderCollaborater.length();
            mStringBuilderCollaborater.append(collaboratorSentences.get(i) + " ");
            mStringBuilderCollaborater.setSpan(new StyleSpan(Typeface.BOLD), start, mStringBuilderCollaborater.length() - 1, 0);
        }


        mStoryView.setText(mStringBuilder);

        if (mGame.getCollaboratorSentences().get(mGame.getCollaboratorSentences().size() - 1).equals("")) { //if collaborator's turn
            if (mUserOwner) { //if user is owner
                mNewSentenceView.setVisibility(View.GONE);
                mSubmitButton.setVisibility(View.GONE);
                mAddParagraphCheckBox.setVisibility(View.GONE);

                mStoryView.setFocusable(true);
                mStoryView.setFocusableInTouchMode(true);
                mTitleView.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
            }
        } else {                                              //if owner's turn
            if (!mUserOwner) { //if user is collaborator
                mNewSentenceView.setVisibility(View.GONE);
                mSubmitButton.setVisibility(View.GONE);
                mAddParagraphCheckBox.setVisibility(View.GONE);

                mStoryView.setFocusable(true);
                mStoryView.setFocusableInTouchMode(true);
                mTitleView.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
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
        mOwnerView.setOnClickListener(this);
        mCollaboratorView.setOnClickListener(this);
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
            while (sentence.substring(sentence.length() - 1).equals(" ")) {
                sentence = sentence.substring(0, sentence.length() - 1);
            }

            Pattern punctuation = Pattern.compile("[.?!]");
            Pattern endQuote = Pattern.compile("[\"\']");
            Matcher punctuationAsLast = punctuation.matcher(sentence.substring(sentence.length() - 1));
            Matcher punctuationSecondToLast = punctuation.matcher(sentence.substring(sentence.length() - 2, sentence.length() - 1));
            Matcher quoteAsLast = endQuote.matcher(sentence.substring(sentence.length() - 1));
            if (!quoteAsLast.matches() && !punctuationSecondToLast.matches() && !punctuationAsLast.matches()) {
                sentence = sentence.concat(".");
            } else if (quoteAsLast.matches() && !punctuationSecondToLast.matches()) {
                sentence = sentence.substring(0, sentence.length() - 1) + "." + sentence.substring(sentence.length() - 1);
            }
            if (mAddParagraphCheckBox.isChecked()) {
                sentence = "\n    " + sentence;
            }

            ArrayList<String> userSentences;
            if (mGame.getCollaboratorSentences().get(mGame.getCollaboratorSentences().size() - 1).equals("")) {
                userSentences = (ArrayList<String>) mGame.getCollaboratorSentences();
                userSentences.set(userSentences.size() - 1, sentence);
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
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mUser.getEmail()}); // recipients
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "An Exquisite story for you!");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "    " + mStoryView.getText().toString());
            startActivity(emailIntent);
        }

        if (view == mOwnerView) {
            if (mOwnerView.getTypeface() == null) {
                mOwnerView.setTypeface(null, Typeface.BOLD);
                mCollaboratorView.setTypeface(null, Typeface.NORMAL);
                mStoryView.setText(mStringBuilderOwner);
            } else {
                mOwnerView.setTypeface(null, Typeface.NORMAL);
                mStoryView.setText(mStringBuilder);
            }
        }

        if (view == mCollaboratorView) {
            if (mCollaboratorView.getTypeface() == null) {
                mCollaboratorView.setTypeface(null, Typeface.BOLD);
                mOwnerView.setTypeface(null, Typeface.NORMAL);
                mStoryView.setText(mStringBuilderCollaborater);
            } else {
                mCollaboratorView.setTypeface(null, Typeface.NORMAL);
                mStoryView.setText(mStringBuilder);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, UserGamesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
