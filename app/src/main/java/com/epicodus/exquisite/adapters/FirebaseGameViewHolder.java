package com.epicodus.exquisite.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;
import com.epicodus.exquisite.ui.GameActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

public class FirebaseGameViewHolder extends RecyclerView.ViewHolder {
    View mView;
    Context mContext;
    FirebaseUser mUser;

    public FirebaseGameViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void bindGame(final Game game) {
        TextView openingLineView = (TextView) mView.findViewById(R.id.openingLineTextView);
        TextView statusView = (TextView) mView.findViewById(R.id.statusView);

        openingLineView.setText(game.getOpeningLine());

        if (game.getCollaboratorName() == null) {
            statusView.setText("Pending invitation...");
        } else if (game.getCollaboratorSentences().size() < game.getOwnerSentences().size()) {
            if (mUser.getDisplayName().equals(game.getOwnerName())) {
                statusView.setText("Waiting for " + game.getCollaboratorName() + "...");
            } else {
                statusView.setText("Your turn!");
            }
        } else {
            if (mUser.getDisplayName().equals(game.getCollaboratorName())) {
                statusView.setText("Waiting for " + game.getOwnerName() + "...");
            } else {
                statusView.setText("Your turn!");
            }
        }

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GameActivity.class);
                intent.putExtra("game", Parcels.wrap(game));
                mContext.startActivity(intent);
            }
        });
    }
}
