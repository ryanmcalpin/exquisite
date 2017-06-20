package com.epicodus.exquisite.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;
import com.epicodus.exquisite.ui.GameActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseInviteViewHolder extends RecyclerView.ViewHolder{
    View mView;
    Context mContext;
    FirebaseUser mUser;

    public FirebaseInviteViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();

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
                DatabaseReference ownerGameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(game.getOwnerUid()).child(game.getFirebaseKey());
                ownerGameRef.setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference collabGameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(game.getCollaboratorUid()).child(game.getFirebaseKey());
                            collabGameRef.setValue(game);
                            DatabaseReference inviteRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_COLLABORATOR_INVITES).child(game.getCollaboratorUid()).child(game.getFirebaseKey());
                            inviteRef.removeValue();
                            Intent intent = new Intent(mContext, GameActivity.class);
                            mContext.startActivity(intent);
                        }
                    }
                });

            }
        });
    }
}
