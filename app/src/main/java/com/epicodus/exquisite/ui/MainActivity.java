package com.epicodus.exquisite.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.epicodus.exquisite.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.newGameButton) Button mNewGameButton;
    @Bind(R.id.logOutLink) TextView mLogOutLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mNewGameButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mNewGameButton) {
            Intent intent = new Intent(MainActivity.this, CreateGameActivity.class);
            startActivity(intent);
        }
        if (v == mLogOutLink) {
            Intent intent = new Intent(MainActivity.this, CreateGameActivity.class);
            startActivity(intent);
        }
    }
}
