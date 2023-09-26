package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class chatList extends AppCompatActivity {

    // testDataSet와 customAdapter를 클래스 멤버 변수로 선언
    private ArrayList<String> testDataSet;
    private CustomAdapter customAdapter;
    private Dialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        //----- 테스트를 위한 더미 데이터 생성 --------------------
        testDataSet = new ArrayList<>(); // 클래스 멤버 변수로 초기화
        for (int i = 0; i <= 5; i++) {
            testDataSet.add((i + 1) + "번째 방");
        }
        //--------------------------------------------------------

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        //--- LayoutManager는 아래 3가지중 하나를 선택하여 사용 ----
        // 1) LinearLayoutManager()
        // 2) GridLayoutManager()
        // 3) StaggeredGridLayoutManager()
        //---------------------------------------------------------
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);  // LayoutManager 설정

        customAdapter = new CustomAdapter(testDataSet); // 클래스 멤버 변수로 초기화
        //===== [Click 이벤트 구현을 위해 추가된 코드] ==============
        customAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, String data) {
                Toast.makeText(getApplicationContext(), "Position:" + position + ", Data:" + data, Toast.LENGTH_SHORT).show();
            }
        });
        //==========================================================
        recyclerView.setAdapter(customAdapter); // 어댑터 설정

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
        customDialog.setContentView(R.layout.custom_dialog);

        // 다이얼로그의 크기 설정
        Window window = customDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        final EditText roomName = customDialog.findViewById(R.id.roomName);
        Button saveButton = customDialog.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputRoomNameText = roomName.getText().toString();
                testDataSet.add(inputRoomNameText);
                customAdapter.notifyItemInserted(testDataSet.size());
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }
}