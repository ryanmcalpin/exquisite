package com.epicodus.exquisite.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.adapters.FirebaseGameViewHolder;
import com.epicodus.exquisite.adapters.FirebaseInviteViewHolder;
import com.epicodus.exquisite.adapters.InviteListAdapter;
import com.epicodus.exquisite.models.Game;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InvitesActivity extends AppCompatActivity {
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.listTitleView) TextView mTitleView;
    @Bind(R.id.statusView) TextView mStatusView;

    private DatabaseReference mInvitesReference;
//    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private InviteListAdapter mAdapter;

    private ArrayList<String> mInviteNodes;
    private ArrayList<Game> mInvites;

    private DatabaseReference mGamesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_games);
        ButterKnife.bind(this);


        mInvites = new ArrayList<>();

        mTitleView.setText("My Invites");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        mGamesReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES);

        mInvitesReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_INVITES).child(uid);

        mInvitesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(), "DATA CHANGED", Toast.LENGTH_SHORT).show();

                if (!dataSnapshot.hasChildren()) {
                    mStatusView.setText("You don't have any invites.");
                } else {
                    mStatusView.setVisibility(View.GONE);

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        mInviteNodes = new ArrayList<>();
                        mInviteNodes.add(child.getKey());
                    }

                    mGamesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (String node : mInviteNodes) {
                                Game game = dataSnapshot.child(node).getValue(Game.class);
                                mInvites.add(game);
                            }

                            mAdapter = new InviteListAdapter(getApplicationContext(), mInvites);
                            mRecyclerView.setAdapter(mAdapter);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(InvitesActivity.this);
                            mRecyclerView.setLayoutManager(layoutManager);
                            mRecyclerView.setHasFixedSize(true);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mInvitesReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Toast.makeText(getApplicationContext(), "DATA CHANGED", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });







//        setUpFirebaseAdapter();
    }

//    private void setUpFirebaseAdapter() {
//        mFirebaseAdapter = new FirebaseRecyclerAdapter<Game, FirebaseInviteViewHolder>(Game.class, R.layout.invite_list_item, FirebaseInviteViewHolder.class, mGamesReference) {
//            @Override
//            protected void populateViewHolder(FirebaseInviteViewHolder viewHolder, Game model, int position) {
//                viewHolder.bindGame(model);
//            }
//        };
//
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setAdapter(mFirebaseAdapter);
//    }

}
