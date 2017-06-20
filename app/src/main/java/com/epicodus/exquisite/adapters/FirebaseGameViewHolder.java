package com.epicodus.exquisite.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;

public class FirebaseGameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    Context mContext;

    public FirebaseGameViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindGame(Game game) {
        Log.d("bindGame: ", mContext.getPackageName());
        TextView openingLineView = (TextView) mView.findViewById(R.id.openingLineTextView);
        TextView statusView = (TextView) mView.findViewById(R.id.statusView);

        openingLineView.setText(game.getOpeningLine());
        if (game.getCollaboratorName() == null) {
            statusView.setText("Pending invitation...");
        } else if (game.getCollaboratorSentences().size() < game.getOwnerSentences().size()) {
            statusView.setText("Waiting for " + game.getCollaboratorName());
        } else {
            statusView.setText("Your turn!");
        }
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "Go to game activity", Toast.LENGTH_SHORT).show();
    }
}
