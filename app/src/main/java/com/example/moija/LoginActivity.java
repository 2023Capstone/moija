package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private View loginButton;
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
                    //                    // 유저의 아이디D
                    Log.d(TAG,"invoke: id" +user.getId());
//                    // 유저의 어카운트정보에 이메일
                    Log.d(TAG,"invoke: nickname" + user.getKakaoAccount().getEmail());
//                    // 유저의 어카운트 정보의 프로파일에 닉네임
                    Log.d(TAG,"invoke:profile" + user.getKakaoAccount().getProfile());

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String userId = user.getId().toString();
                    String email = user.getKakaoAccount().getEmail();

                //로그인 시도
                    auth.signInWithEmailAndPassword(email,userId)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(getApplicationContext(), Fragment_Chat_Map.class);
                                        startActivity(intent);
                                    } else {
                                        //로그인 정보 없으면 회원가입
                                        auth.createUserWithEmailAndPassword(email,userId)
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser fbUser = auth.getCurrentUser();
                                                    // Firebase 데이터베이스 인스턴스 가져오기
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                                                    DatabaseReference userRef = databaseReference.child(fbUser.getUid());

//                                                     새로 로그인 하는 경우
                                                    DatabaseReference sendUserData = databaseReference.child(fbUser.getUid());
                                // 데이터 쓰기
                                                    sendUserData.child("name").setValue(user.getKakaoAccount().getProfile().getNickname());
                                                    sendUserData.child("email").setValue(user.getKakaoAccount().getEmail());
                                                    sendUserData.child("thumbnail").setValue(user.getKakaoAccount().getProfile().getThumbnailImageUrl());
                                                    // Toast 알림
                                                } else {
                                                }
                                            }
                                        });
                                    }
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

    }
