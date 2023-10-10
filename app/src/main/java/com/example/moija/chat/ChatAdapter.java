package com.example.moija.chat;

import static com.example.moija.time.DateTime.getCurrentDateTime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moija.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<String> messageList;

    public ChatAdapter(List<String> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String message = messageList.get(position);
        holder.messageTextView.setText(message);
        holder.textViewTime.setText("" + );

        // 긴 아이템의 경우 오른쪽으로 정렬
        if (message.length() > 20) {
            holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView, textViewTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }
    }
}
