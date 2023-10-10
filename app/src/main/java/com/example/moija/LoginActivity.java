package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.checkerframework.checker.nullness.qual.NonNull;

import kotlin.Unit;

import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private View loginButton, logoutButton;
    private TextView nickName;
    private ImageView profileImage;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login);


        // 카카오가 설치되어 있는지 확인 하는 메서드또한 카카오에서 제공 콜백 객체를 이용함
        Function2<OAuthToken, Throwable, Unit> callback = new  Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                // 이때 토큰이 전달이 되면 로그인이 성공한 것이고 토큰이 전달되지 않았다면 로그인 실패
                if(oAuthToken != null) {

                }
                if (throwable != null) {

                }
                updateKakaoLoginUi();
                return null;
            }
        };

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

    // 로그인 버튼
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                }else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                }
            }
        });

        updateKakaoLoginUi();
    }


    private void updateKakaoLoginUi() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {
                    // 유저의 아이디
                    String userId = user.getId().toString();

                    // Firebase 데이터베이스 인스턴스 가져오기
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");

                    // 해당 아이디로 데이터베이스에서 데이터 가져오기
                    DatabaseReference userRef = databaseReference.child(userId);

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // 이미 해당 아이디로 데이터가 존재하는 경우
                                Toast.makeText(LoginActivity.this, "이미 로그인되어 있습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), chatList.class);
                                startActivity(intent);
                            } else {
                                // 해당 아이디로 데이터가 존재하지 않는 경우, 데이터 저장
//                                Log.d(TAG, "invoke: id" + userId);
//                                Log.d(TAG, "invoke: nickname" + user.getKakaoAccount().getEmail());
//                                Log.d(TAG, "invoke:" + user.getKakaoAccount().getProfile().getNickname());
//                                Log.d(TAG, "invoke:" + user.getKakaoAccount().getProfile());

                                DatabaseReference sendUserData = databaseReference.child(userId);
                                // 데이터 쓰기
                                sendUserData.child("name").setValue(user.getKakaoAccount().getProfile().getNickname());
                                sendUserData.child("email").setValue(user.getKakaoAccount().getEmail());
                                sendUserData.child("thumbnail").setValue(user.getKakaoAccount().getProfile().getThumbnailImageUrl());

                                //Toast 알림
                                Toast.makeText(LoginActivity.this, "로그인됨", Toast.LENGTH_SHORT).show();
                                //화면이동
                                Intent intent = new Intent(getApplicationContext(), chatList.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // 오류 처리
                            Log.d(TAG, "Database Error: " + databaseError.getMessage());
                        }
                    });
                } else {
                    // 로그인이 되어 있지 않다면
                    Toast.makeText(LoginActivity.this, "로그인실패", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });
    }

//    private  void updateKakaoLoginUi(){
//        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
//            @Override
//            public Unit invoke(User user, Throwable throwable) {
//                // 로그인이 되어있으면
//                if (user!=null){
//
//                    // 유저의 아이디D
//                    Log.d(TAG,"invoke: id" +user.getId());
//                    // 유저의 어카운트정보에 이메일
//                    Log.d(TAG,"invoke: nickname" + user.getKakaoAccount().getEmail());
//                    // 유저의 어카운트 정보의 프로파일에 닉네임
//                    Log.d(TAG,"invoke:" + user.getKakaoAccount().getProfile().getNickname());
//                    Log.d(TAG,"invoke:" + user.getKakaoAccount().getProfile());
//
//                    //database에 로그인 정보 전송
//
//                    databaseReference= FirebaseDatabase.getInstance().getReference().child("user");
//
//                    DatabaseReference sendUserData=databaseReference.child(user.getId().toString());
//                    //쓰기
//                    sendUserData.child("name").setValue(user.getKakaoAccount().getProfile().getNickname());
//                    sendUserData.child("email").setValue(user.getKakaoAccount().getEmail());
//                    sendUserData.child("thumbnail").setValue(user.getKakaoAccount().getProfile().getThumbnailImageUrl());
//                    Toast.makeText(LoginActivity.this, "로그인됨", Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(getApplicationContext(), test2.class);
//                    startActivity(intent);
//                }else {
//                    // 로그인이 되어 있지 않다면 위와 반대로
//                    Toast.makeText(LoginActivity.this, "로그인실패", Toast.LENGTH_SHORT).show();
//
//                }
//                return null;
//            }
//        });


    }
