package com.epicodus.exquisite.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {
    @Bind(R.id.ownerView) TextView mOwnerView;
    @Bind(R.id.collaboratorView) TextView mCollaboratorView;
    @Bind(R.id.storyTextView) TextView mStoryView;
    @Bind(R.id.newSentenceView) EditText mNewSentenceView;
    @Bind(R.id.submitButton) Button mSubmitButton;

    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mGame = Parcels.unwrap(intent.getParcelableExtra("game"));

        mOwnerView.setText("Game owner: " + mGame.getOwnerName());
        mCollaboratorView.setText("Collaborator: " + mGame.getCollaboratorName());

        ArrayList<String> ownerSentences = (ArrayList<String>) mGame.getOwnerSentences();
        ArrayList<String> collaboratorSentences = (ArrayList<String>) mGame.getCollaboratorSentences();
        if (collaboratorSentences.size() < ownerSentences.size()) {
            collaboratorSentences.add("");
        }
        String story = "";
        for (int i = 0; i < ownerSentences.size(); i++) {
            story = story.concat(ownerSentences.get(i) + " ");
            story = story.concat(collaboratorSentences.get(i) + " ");
        }
        mStoryView.setText(story);
    }
}
