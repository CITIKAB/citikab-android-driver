package com.trioangle.goferdriver.firebaseChat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;

import java.util.ArrayList;

import javax.inject.Inject;

public class AdapterFirebaseRecylcerview extends RecyclerView.Adapter<AdapterFirebaseRecylcerview.RecyclerViewHolder>  {

    public @Inject
    SessionManager sessionManager;

    private LayoutInflater inflater;
    private ArrayList<FirebaseChatModelClass> chatList = new ArrayList<>();
    private Context mContext;

    public AdapterFirebaseRecylcerview(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        AppController.getAppComponent().inject(this);

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_firebase_chat_single_row, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        if(chatList.get(position).type.equals(CommonKeys.FIREBASE_CHAT_TYPE_DRIVER)){
            holder.myMessageCard.setVisibility(View.VISIBLE);
            holder.opponentChatMessageLayout.setVisibility(View.GONE);
            holder.myMessage.setText(chatList.get(position).message);
        }else {
            holder.opponentChatMessageLayout.setVisibility(View.VISIBLE);
            holder.myMessageCard.setVisibility(View.GONE);
            holder.opponentMessage.setText(chatList.get(position).message);

            handleOpponentProfilePicture(holder,position);
        }

    }

    private void handleOpponentProfilePicture(RecyclerViewHolder holder, int position) {
        try{
            if(position!=0){
                if(chatList.get(position).type != null && chatList.get(position-1).type.equals(CommonKeys.FIREBASE_CHAT_TYPE_DRIVER)){
                    Picasso.with(mContext).load(sessionManager.getRiderProfilePic()).error(R.drawable.car).into(holder.opponentProfileImageView);
                }else{
                    holder.opponentProfileImageView.setVisibility(View.INVISIBLE);
                }
            }else{
                Picasso.with(mContext).load(sessionManager.getRiderProfilePic()).error(R.drawable.car).into(holder.opponentProfileImageView);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void updateChat(FirebaseChatModelClass firebaseChatModelClass){
        chatList.add(firebaseChatModelClass);
        notifyItemChanged(chatList.size()-1);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView opponentMessage, myMessage;
        ImageView opponentProfileImageView;
        CardView myMessageCard;
        LinearLayout opponentChatMessageLayout;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            opponentMessage = itemView.findViewById(R.id.tv_opponent_message);
            myMessage = itemView.findViewById(R.id.tv_my_message);
            opponentProfileImageView = itemView.findViewById(R.id.imgv_opponent_profile_pic);
            myMessageCard= itemView.findViewById(R.id.cv_my_messages);
            opponentChatMessageLayout = itemView.findViewById(R.id.lv_opponnent_chat_messages);
        }
    }
}
