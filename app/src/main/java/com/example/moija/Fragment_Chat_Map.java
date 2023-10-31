package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.moija.chat.Chat;
import com.example.moija.room.RoomList;

public class Fragment_Chat_Map extends AppCompatActivity {
    Button btn_map, btn_chat;

    private MapFragment mapFragment;
    private Chat chat;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_chat_map);

        fragmentManager = getSupportFragmentManager();

        String userId = getIntent().getStringExtra("userid");
        String username = getIntent().getStringExtra("username");
        String profile = getIntent().getStringExtra("profile");
        String roomCode = getIntent().getStringExtra("roomcode");

        btn_chat = findViewById(R.id.btn_chat);
        btn_map = findViewById(R.id.btn_map);

        // ChatList와 MapFragment 인스턴스 초기화
        chat = new Chat();
        mapFragment = new MapFragment();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer,chat).commit();
         // 데이터를 Intent로부터 가져옴
        String roomData = getIntent().getStringExtra("roomdata");
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그 메시지 출력 (맵 버튼 클릭 시)
                Log.d("MyApp", "맵버튼이 클릭되었습니다.");
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer, mapFragment).commit();

            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그 메시지 출력 ( 채팅 버튼 클릭 시)
                Log.d("MyApp", "채팅버튼이 클릭되었습니다.");
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer,chat).commit();

            }
        });
    }
}