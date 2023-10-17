package com.example.moija.chat;

import static com.example.moija.time.DateTime.getCurrentDateTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moija.R;
import com.example.moija.sqlite.ChatDB;

import java.util.ArrayList;
import java.util.List;

public class Chat extends Fragment {

    private String roomdata;

    TextView roomInfoText;

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;

    private Button sendButton;

    private ChatAdapter chatAdapter;

    private List<String> messageList;

    private String roomcode, username, profile;

    private Long userid;


//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, container, false);
        userid = getArguments().getLong("userid", 0);
        roomcode = getArguments().getString("roomcode");
        username = getArguments().getString("username");
        profile = getArguments().getString("profile");

        roomdata = getArguments().getString("roomdata");

        roomInfoText = view.findViewById(R.id.roomInfoText);
        roomInfoText.setText(roomdata);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);

        ChatDB chatDB = new ChatDB(getActivity(), roomcode);
        SQLiteDatabase database = chatDB.getWritableDatabase();
//        chatDB.createChatRoom(database);

        messageList = new ArrayList<>();

        Cursor cursor = null;
        try {
            // 데이터베이스를 읽기 전용 모드로 열기
            database = chatDB.getReadableDatabase();

            cursor = database.rawQuery("SELECT " + ChatDB.COLUMN_ID + ", " + ChatDB.COLUMN_NAME + ", " + ChatDB.COLUMN_PROFILE + "," + ChatDB.COLUMN_MESSAGE + "," + ChatDB.COLUMN_DATE + " FROM " + chatDB.tableName, null);

            while (cursor.moveToNext()) {
                String userId = cursor.getString(cursor.getColumnIndexOrThrow(chatDB.COLUMN_ID));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow(chatDB.COLUMN_NAME));
                String userProfile = cursor.getString(cursor.getColumnIndexOrThrow(chatDB.COLUMN_PROFILE));
                String chatMessage = cursor.getString(cursor.getColumnIndexOrThrow(chatDB.COLUMN_MESSAGE));
                String chatDate = cursor.getString(cursor.getColumnIndexOrThrow(chatDB.COLUMN_DATE));
                messageList.add(userName + ": " + chatMessage);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (database != null && database.isOpen()) {
                database.close();
            }
        }

        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    messageList.add(username + ": " + message);
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    messageEditText.getText().clear();

                    // ChatDB 인스턴스를 다시 생성
                    ChatDB chatDB = new ChatDB(getActivity(), roomcode);
                    // 데이터베이스를 쓰기 모드로 열기
                    SQLiteDatabase database = chatDB.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(ChatDB.COLUMN_ID, userid);
                    values.put(ChatDB.COLUMN_NAME, username);
                    values.put(ChatDB.COLUMN_PROFILE, profile);
                    values.put(ChatDB.COLUMN_MESSAGE, message);
                    values.put(ChatDB.COLUMN_DATE, getCurrentDateTime());

                    long newRowId = database.insert(chatDB.tableName, null, values);

                    if (newRowId == -1) {
                        // 데이터베이스에 추가 실패한 경우
                        Toast.makeText(getActivity(), "데이터베이스에 정보를 추가하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 데이터베이스에 성공적으로 추가한 경우
                        Toast.makeText(getActivity(), "데이터베이스에 정보를 추가했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return inflater.inflate(R.layout.activity_chat_fragment, container, false);
    }
}