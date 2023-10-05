package com.example.moija.chat;

import static com.example.moija.time.DateTime.getCurrentDateTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.moija.R;
import com.example.moija.room.RoomList;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {

    private String roomdata;

    TextView roomInfoText;

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;

    private Button sendButton;

    private ChatAdapter chatAdapter;
    private List<String> messageList = new ArrayList<>();

    private String username, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        username = getIntent().getStringExtra("username");
        profile = getIntent().getStringExtra("profile");

        roomdata = getIntent().getStringExtra("roomdata");

        roomInfoText = findViewById(R.id.roomInfoText);
        roomInfoText.setText(roomdata);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    messageList.add(message);
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    messageEditText.getText().clear();
                }
            }
        });
    }
}