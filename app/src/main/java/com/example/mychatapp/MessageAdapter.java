package com.example.mychatapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private Context mContext;
    private List<ChatMessage> messageList;
    private String TAG = "MessageAdapter";
    public MessageAdapter(Context mContext, List<ChatMessage> mData) {
        this.mContext = mContext;
        this.messageList = mData;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.message1,parent,false);
        return new MessageViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.name.setText(messageList.get(position).getMessageUser());
        holder.message.setText(messageList.get(position).getMessageText());
        holder.time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", messageList.get(position).getMessageTime()));

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView name, message, time;

        public MessageViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.message_user);
            message = itemView.findViewById(R.id.message_text);
            time = itemView.findViewById(R.id.message_time);
        }
    }
}
