package com.epicodus.exquisite.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;

import org.parceler.Parcels;

public class InvitePlayerActivity extends AppCompatActivity {
    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_player);

        Intent intent = getIntent();
        mGame = Parcels.unwrap(intent.getParcelableExtra("newGame"));

    }
}
