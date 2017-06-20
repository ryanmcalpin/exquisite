package com.epicodus.exquisite.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseInviteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    Context mContext;
    FirebaseUser mUser;

    public FirebaseInviteViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void bindGame(final Game game) {
        TextView openingLineView = (TextView) mView.findViewById(R.id.openingLineTextView);
        TextView statusView = (TextView) mView.findViewById(R.id.statusView);
        Button acceptButton = (Button) mView.findViewById(R.id.acceptButton);

        openingLineView.setText(game.getOpeningLine());
        if (game.getCollaboratorName() == null) {
            statusView.setText("Pending invitation...");
        } else if (game.getCollaboratorSentences().size() < game.getOwnerSentences().size()) {
            statusView.setText("Waiting for " + game.getCollaboratorName());
        } else {
            statusView.setText("Your turn!");
        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.setCollaboratorName(mUser.getDisplayName());
                game.setCollaboratorUid(mUser.getUid());
                DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(game.getOwnerUid()).child(game.getFirebaseKey());
                gameRef.setValue(game);
                Toast.makeText(mContext, "YAAY", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
    }
}
