package com.epicodus.exquisite.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.adapters.FirebaseGameViewHolder;
import com.epicodus.exquisite.adapters.FirebaseInviteViewHolder;
import com.epicodus.exquisite.models.Game;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InvitesActivity extends AppCompatActivity {
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.listTitleView) TextView mTitleView;

    private DatabaseReference mInvitesReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_games);
        ButterKnife.bind(this);

        mTitleView.setText("My Invites");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        mInvitesReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_COLLABORATOR_INVITES).child(uid);
        setUpFirebaseAdapter();
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Game, FirebaseInviteViewHolder>(Game.class, R.layout.invite_list_item, FirebaseInviteViewHolder.class, mInvitesReference) {
            @Override
            protected void populateViewHolder(FirebaseInviteViewHolder viewHolder, Game model, int position) {
                viewHolder.bindGame(model);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
