package com.example.moija.room;

import static com.example.moija.time.DateTime.getCurrentDateTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.moija.sqlite.RoomListDB;

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

    private String roomCode, username, profile;

    private Long userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        userid = getIntent().getLongExtra("userid", 0);
        username = getIntent().getStringExtra("username");
        profile = getIntent().getStringExtra("profile");

        profile_Image = findViewById(R.id.profile_Image);
        username_Text = findViewById(R.id.username_Text);
        username_Text.setText(username);
        Glide.with(profile_Image).load(profile).circleCrop().into(profile_Image);

        //----- 테스트를 위한 더미 데이터 생성 --------------------
        RoomListDB roomlistDB = new RoomListDB(getApplicationContext());
        SQLiteDatabase database = roomlistDB.getReadableDatabase();

        testDataSet = new ArrayList<>(); // 클래스 멤버 변수로 초기화

        Cursor cursor = database.rawQuery("SELECT " + RoomListDB.COLUMN_NAME + ", " + RoomListDB.COLUMN_CODE + ", " + RoomListDB.COLUMN_DATE + " FROM " + RoomListDB.TABLE_NAME, null);
        while (cursor.moveToNext()) {
            String roomName = cursor.getString(cursor.getColumnIndexOrThrow(RoomListDB.COLUMN_NAME));
            String roomCode = cursor.getString(cursor.getColumnIndexOrThrow(RoomListDB.COLUMN_CODE));
            String roomDate = cursor.getString(cursor.getColumnIndexOrThrow(RoomListDB.COLUMN_DATE));
            testDataSet.add("방 이름: " + roomName + "\n["
                    + roomCode + "] " + roomDate);
        }

        cursor.close();
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
                    roomCode = getRoomCodeFromDataSet(data);
                    intent.putExtra("roomdata", data);
                    intent.putExtra("userid", userid);
                    intent.putExtra("username", username);
                    intent.putExtra("profile",profile);
                    intent.putExtra("roomcode", roomCode);

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

    private String getRoomCodeFromDataSet(String data) {
        // 데이터셋에서 방 코드를 추출하는 로직을 작성합니다.
        // 예시: 데이터가 "방 이름: My Room\n[ABC123] 2023-10-10"일 때 "[ABC123]" 부분을 추출
        String roomCode = data.substring(data.indexOf("[") + 1, data.indexOf("]"));
        return roomCode;
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

                    RoomListDB roomlistDB = new RoomListDB(getApplicationContext());
                    SQLiteDatabase database = roomlistDB.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(RoomListDB.COLUMN_NAME, inputRoomNameText);
                    values.put(RoomListDB.COLUMN_CODE, randomStringValue);
                    values.put(RoomListDB.COLUMN_DATE, currentDateTime);
                    long newRowId = database.insert(RoomListDB.TABLE_NAME, null, values);

                    if (newRowId == -1) {
                        // 데이터베이스에 추가 실패한 경우
                        Toast.makeText(getApplicationContext(), "데이터베이스에 정보를 추가하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 데이터베이스에 성공적으로 추가한 경우
                        Toast.makeText(getApplicationContext(), "데이터베이스에 정보를 추가했습니다.", Toast.LENGTH_SHORT).show();
                    }
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