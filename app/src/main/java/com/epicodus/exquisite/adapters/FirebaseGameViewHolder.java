package com.epicodus.exquisite.adapters;

import android.app.Activity;
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
import com.epicodus.exquisite.ui.InvitePlayerActivity;
import com.epicodus.exquisite.ui.UserGamesActivity;
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
        final TextView statusView = (TextView) mView.findViewById(R.id.statusView);
        String other = "";

        openingLineView.setText("    " + game.getOpeningLine() + "..");

        if (game.getCollaboratorName() == null) {
            statusView.setText("No Invitation");
        } else if (game.getCollaboratorUid() == null) {
            statusView.setText("Pending invitation");
        } else {

            if (mUser.getDisplayName().equals(game.getOwnerName())) {
                other = game.getCollaboratorName();
            } else {
                other = game.getOwnerName();
            }

            if (game.getCollaboratorSentences().size() < game.getOwnerSentences().size() && mUser.getDisplayName().equals(game.getOwnerName()) || game.getCollaboratorSentences().size() == game.getOwnerSentences().size() && mUser.getDisplayName().equals(game.getCollaboratorName())) {
                statusView.setText(other + "'s turn");
            } else {
                statusView.setText("Your turn");
            }
        }
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game.getCollaboratorName() == null) {
                    Intent intent = new Intent(mContext, InvitePlayerActivity.class);
                    intent.putExtra("game", Parcels.wrap(game));
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, GameActivity.class);
                    intent.putExtra("game", Parcels.wrap(game));
                    mContext.startActivity(intent);
                }
                Activity activity = (Activity) mContext;
                activity.overridePendingTransition(0, 0);
            }
        });
    }


}
