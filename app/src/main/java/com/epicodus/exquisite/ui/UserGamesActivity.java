package com.epicodus.exquisite.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.adapters.FirebaseGameViewHolder;
import com.epicodus.exquisite.models.Game;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserGamesActivity extends AppCompatActivity {
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.listTitleView) TextView mTitleView;

    private DatabaseReference mGamesReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_games);
        ButterKnife.bind(this);

        mTitleView.setText("My Games");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        mGamesReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(uid);
        setUpFirebaseAdapter();
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Game, FirebaseGameViewHolder>(Game.class, R.layout.game_list_item, FirebaseGameViewHolder.class, mGamesReference) {
            @Override
            protected void populateViewHolder(FirebaseGameViewHolder viewHolder, Game model, int position) {
                viewHolder.bindGame(model);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserGamesActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
