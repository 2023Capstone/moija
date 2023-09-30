package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class chatList extends AppCompatActivity {

    // testDataSet와 customAdapter를 클래스 멤버 변수로 선언
    private ArrayList<String> testDataSet;
    private roomCustomAdapter roomCustomAdapter;
    private Dialog customDialog;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        //----- 테스트를 위한 더미 데이터 생성 --------------------
        testDataSet = new ArrayList<>(); // 클래스 멤버 변수로 초기화
//        for (int i = 0; i <= 5; i++) {
//            testDataSet.add((i + 1) + "번째 방");
//        }
        //--------------------------------------------------------

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        //--- LayoutManager는 아래 3가지중 하나를 선택하여 사용 ----
        // 1) LinearLayoutManager()
        // 2) GridLayoutManager()
        // 3) StaggeredGridLayoutManager()
        //---------------------------------------------------------
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);  // LayoutManager 설정

        roomCustomAdapter = new roomCustomAdapter(testDataSet); //ㅇ 클래스 멤버 변수로 초기화
        //===== [Click 이벤트 구현을 위해 추가된 코드] ==============
        roomCustomAdapter.setOnItemClickListener(new roomCustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, String data) {
                Toast.makeText(getApplicationContext(), "Position:" + position + ", Data:" + data, Toast.LENGTH_SHORT).show();
            }
        });
        //==========================================================
        recyclerView.setAdapter(roomCustomAdapter); // 어댑터 설정

        //----- 데이터 추가 버튼 -------------------------------
        Button buttonAddItem = findViewById(R.id.buttonAddItem);

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
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
        final EditText randomString = customDialog.findViewById(R.id.randomString);
        Button saveButton = customDialog.findViewById(R.id.saveButton);

        int length = 6;
        String randomStringValue = generateRandomString(length);
        randomString.setText(randomStringValue);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputRoomNameText = roomName.getText().toString();
                String currentDateTime = getCurrentDateTime();
                testDataSet.add(inputRoomNameText + "\n" + randomStringValue + "\n" + currentDateTime);
                roomCustomAdapter.notifyItemInserted(testDataSet.size());
                customDialog.dismiss();
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

    private String getCurrentDateTime() {
        // 현재 날짜와 시간을 가져오는 Date 객체 생성

        Date currentDate = new Date();
        // 원하는 날짜 및 시간 형식을 설정한 SimpleDateFormat 객체 생성

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        // SimpleDateFormat을 사용하여 Date 객체를 문자열로 변환
        String formattedDate = dateFormat.format(currentDate);

        return formattedDate;
    }
}