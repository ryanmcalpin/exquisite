package com.epicodus.exquisite.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InvitePlayerActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.openingLineTextView) TextView mOpeningLineView;
    @Bind(R.id.friendAutoComplete) EditText mFriendView;
    @Bind(R.id.inviteButton) Button mInviteButton;

    private Game mGame;
    private String mInviteeUid;
    private ProgressDialog mProgDialog;
    private String mFriendName;
    private DatabaseReference mNicknamesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_player);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mGame = Parcels.unwrap(intent.getParcelableExtra("game"));
        mOpeningLineView.setText(mGame.getOpeningLine());

        mNicknamesRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_NICKNAMES);

        createProgDialog();

        mInviteButton.setOnClickListener(this);

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
            validateFriend();
        }
    }

    private void validateFriend() {
        mFriendName = mFriendView.getText().toString().trim();
        if (mFriendName.equals("")) {
            mFriendView.setError("Enter friend's nickname");
            return;
        }

        mNicknamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mFriendName)) {
                    mFriendView.setError("No such player");
                    mFriendView.requestFocus();

                } else if (mFriendName.equals(mGame.getOwnerName())) {
                    mFriendView.setError("That's you!");
                    mFriendView.requestFocus();

                } else {
                    mInviteeUid = dataSnapshot.child(mFriendName).getValue().toString();
                    inviteFriend();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void inviteFriend() {
        mProgDialog.show();

        DatabaseReference collaboratorInvitesRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_COLLABORATOR_INVITES).child(mInviteeUid);
        DatabaseReference pushRefCollab = collaboratorInvitesRef.child(mGame.getFirebaseKey());
        DatabaseReference ownerGameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(mGame.getOwnerUid()).child(mGame.getFirebaseKey());
        ownerGameRef.child("collaboratorName").setValue(mFriendView.getText().toString());

        pushRefCollab.setValue(mGame).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgDialog.dismiss();
                Intent intent = new Intent(InvitePlayerActivity.this, UserGamesActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
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
