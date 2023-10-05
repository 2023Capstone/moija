package com.example.moija.room;

import static com.example.moija.time.DateTime.getCurrentDateTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moija.R;
import com.example.moija.chat.Chat;

import java.security.SecureRandom;
import java.util.ArrayList;

public class RoomList extends AppCompatActivity {

    // testDataSet와 customAdapter를 클래스 멤버 변수로 선언
    private ArrayList<String> testDataSet;
    private RoomCustomAdapter roomCustomAdapter;
    private Dialog customDialog;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    private TextView username_Text;
    private ImageView profile_Image;

    private String username, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        username = getIntent().getStringExtra("username");
        profile = getIntent().getStringExtra("profile");

        profile_Image = findViewById(R.id.profile_Image);
        username_Text = findViewById(R.id.username_Text);
        username_Text.setText(username);
        Glide.with(profile_Image).load(profile).circleCrop().into(profile_Image);

        //----- 테스트를 위한 더미 데이터 생성 --------------------
        testDataSet = new ArrayList<>(); // 클래스 멤버 변수로 초기화
//        for (int i = 0; i <= 5; i++) {
//            testDataSet.add((i + 1) + "번째 방");
//        }
        //--------------------------------------------------------

        RecyclerView room_recyclerView = findViewById(R.id.room_recyclerView);

        //--- LayoutManager는 아래 3가지중 하나를 선택하여 사용 ----
        // 1) LinearLayoutManager()
        // 2) GridLayoutManager()
        // 3) StaggeredGridLayoutManager()
        //---------------------------------------------------------
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        room_recyclerView.setLayoutManager(linearLayoutManager);  // LayoutManager 설정

        roomCustomAdapter = new RoomCustomAdapter(testDataSet); // 클래스 멤버 변수로 초기화
        //===== [Click 이벤트 구현을 위해 추가된 코드] ==============
        roomCustomAdapter.setOnItemClickListener(new RoomCustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, String data) {
                Toast.makeText(getApplicationContext(), "Position:" + position + ", Data:" + data, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(RoomList.this, Chat.class);
                    intent.putExtra("roomdata", data);
                    intent.putExtra("username", username);
                    intent.putExtra("profile",profile);
                startActivity(intent);
            }
        });
        //==========================================================
        room_recyclerView.setAdapter(roomCustomAdapter); // 어댑터 설정

        //----- 데이터 추가 버튼 -------------------------------
        Button button_AddItem = findViewById(R.id.button_AddItem);

        button_AddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });
    }

    private void showCustomDialog() {
        customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.room_custom_dialog);

        // 다이얼로그의 크기 설정
        Window window = customDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        final EditText roomName = customDialog.findViewById(R.id.roomName);
        final EditText roomCode = customDialog.findViewById(R.id.roomCode);
        Button saveButton = customDialog.findViewById(R.id.saveButton);

        int length = 6;
        String randomStringValue = generateRandomString(length);
        roomCode.setText(randomStringValue);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputRoomNameText = roomName.getText().toString();
                if(inputRoomNameText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "방 이름이 작성되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    String currentDateTime = getCurrentDateTime();
                    testDataSet.add("방 이름: " + inputRoomNameText + "\n["
                            + randomStringValue + "] " + currentDateTime);
                    roomCustomAdapter.notifyItemInserted(testDataSet.size());
                    customDialog.dismiss();
                }
            }
        });

        customDialog.show();
    }

    private String generateRandomString(int length) {
        StringBuilder randomString = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            randomString.append(randomChar);
        }
        return randomString.toString();
    }

}