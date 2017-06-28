package com.epicodus.exquisite.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InvitePlayerActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.openingLineTextView) TextView mOpeningLineView;
    @Bind(R.id.friendAutoComplete) AutoCompleteTextView mFriendView;
    @Bind(R.id.inviteButton) Button mInviteButton;

    private Game mGame;
    private boolean mExists;
    private String mInviteeUid;
    private ProgressDialog mProgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_player);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mGame = Parcels.unwrap(intent.getParcelableExtra("newGame"));
        mOpeningLineView.setText(mGame.getOpeningLine());

        createProgDialog();

        mInviteButton.setOnClickListener(this);
        mFriendView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    doesPlayerExist(s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    doesPlayerExist(s.toString().trim());
                }
            }
        });
        mFriendView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mFriendView.length() != 0) {
                    doesPlayerExist(mFriendView.getText().toString().trim());
                }
            }
        });
    }

    private void createProgDialog() {
        mProgDialog = new ProgressDialog(this);
        mProgDialog.setTitle("Loading...");
        mProgDialog.setMessage("Inviting friend...");
        mProgDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        if (v == mInviteButton) {
            if (mFriendView.getError() != null) {
                mFriendView.requestFocus();
                return;
            }
            mProgDialog.show();
            DatabaseReference collaboratorInvitesRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_COLLABORATOR_INVITES).child(mInviteeUid);
            DatabaseReference pushRefCollab = collaboratorInvitesRef.child(mGame.getFirebaseKey());
            //mGame.setCollaboratorName(mFriendView.getText().toString()); //indicates a pending invite
            DatabaseReference ownerGameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(mGame.getOwnerUid()).child(mGame.getFirebaseKey());
            ownerGameRef.child("collaboratorName").setValue(mFriendView.getText().toString());
            pushRefCollab.setValue(mGame).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mProgDialog.dismiss();
                    Intent intent = new Intent(InvitePlayerActivity.this, UserGamesActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    private boolean doesPlayerExist(final String name) {
        DatabaseReference nicknamesRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_NICKNAMES);
        nicknamesRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(name)) {
                    mFriendView.setError("No such player");
                    mFriendView.requestFocus();
                    mExists = false;
                } else if (name.equals(mGame.getOwnerName())) {
                    mFriendView.setError("That's you!");
                    mFriendView.requestFocus();
                    mExists = false;
                } else {
                    mExists = true;
                    mInviteeUid = dataSnapshot.child(name).getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mExists;
    }
}
