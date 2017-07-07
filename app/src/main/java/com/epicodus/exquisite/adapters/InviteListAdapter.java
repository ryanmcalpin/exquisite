package com.epicodus.exquisite.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.epicodus.exquisite.Constants;
import com.epicodus.exquisite.R;
import com.epicodus.exquisite.models.Game;
import com.epicodus.exquisite.ui.GameActivity;
import com.epicodus.exquisite.ui.InvitesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.util.ArrayList;

public class InviteListAdapter extends RecyclerView.Adapter<InviteListAdapter.InviteViewHolder> {
    private ArrayList<Game> mInvites = new ArrayList<>();
    private Context mContext;
    private FirebaseUser mUser;

    public InviteListAdapter(Context context, ArrayList<Game> invites) {
        mContext = context;
        mInvites = invites;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public InviteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_list_item, parent, false);
        InviteViewHolder viewHolder = new InviteViewHolder(view);
        return viewHolder;    }

    @Override
    public void onBindViewHolder(InviteViewHolder holder, int position) {
        holder.bindInvite(mInvites.get(position));
    }

    @Override
    public int getItemCount() {
        return mInvites.size();
    }

    public class InviteViewHolder extends RecyclerView.ViewHolder {

        public InviteViewHolder(View itemView) {
            super(itemView);
        }

        public void bindInvite(final Game game) {

            TextView openingLineView = (TextView) itemView.findViewById(R.id.openingLineTextView);
            Button acceptButton = (Button) itemView.findViewById(R.id.acceptButton);
            Button declineButton = (Button) itemView.findViewById(R.id.declineButton);
            TextView inviterView = (TextView) itemView.findViewById(R.id.inviterView);

            openingLineView.setText("    \"" + game.getOpeningLine() + "\"");
            inviterView.setText("-" + game.getOwnerName());

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    game.setCollaboratorName(mUser.getDisplayName());
                    game.setCollaboratorUid(mUser.getUid());



                    DatabaseReference gameReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES).child(game.getFirebaseKey());
                    gameReference.setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference inviteReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_INVITES).child(game.getCollaboratorUid()).child(game.getFirebaseKey());
                                inviteReference.removeValue();

                                DatabaseReference userGamesRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_GAMES).child(game.getCollaboratorUid()).child(game.getFirebaseKey());
                                userGamesRef.setValue(true);

                                Intent intent = new Intent(mContext, GameActivity.class);
                                intent.putExtra("game", Parcels.wrap(game));
                                mContext.startActivity(intent);
                            }
                        }
                    });
                }
            });

            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference inviteReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_INVITES).child(mUser.getUid()).child(game.getFirebaseKey());

                    inviteReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(mContext, InvitesActivity.class);
                            mContext.startActivity(intent);
                        }
                    });
                }
            });

        }
    }
}
