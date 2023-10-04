package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class chat extends AppCompatActivity {

    private String roomdata;

    TextView roomInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        roomdata = getIntent().getStringExtra("roomdata");

        roomInfoText = findViewById(R.id.roomInfoText);
        roomInfoText.setText(roomdata);



    }
}