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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
                    // Get the Kakao user details

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String email = user.getKakaoAccount().getEmail();
                    String userId = user.getId().toString();

                    // Firebase 로그인 또는 회원가입
                    auth.signInWithEmailAndPassword(email, userId)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Firebase 로그인 성공
                                        FirebaseUser fbUser = auth.getCurrentUser();
                                        if (fbUser != null) {
                                            String fbUid = fbUser.getUid();
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                                            DatabaseReference userRef = databaseReference.child(fbUid);
                                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        // 이미 해당 아이디로 데이터가 존재하는 경우
                                                        Toast.makeText(LoginActivity.this, "이미 로그인되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(), chatList.class);
                                                        startActivity(intent);
                                                    } else {
                                                        // 새로 로그인 하는 경우
                                                        DatabaseReference sendUserData = databaseReference.child(fbUid);
                                                        // 데이터 쓰기
                                                        sendUserData.child("name").setValue(user.getKakaoAccount().getProfile().getNickname());
                                                        sendUserData.child("email").setValue(user.getKakaoAccount().getEmail());
                                                        sendUserData.child("thumbnail").setValue(user.getKakaoAccount().getProfile().getThumbnailImageUrl());
                                                        // Toast 알림
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
                                            // 사용자가 인증되어 있지 않습니다.
                                        }
                                    } else {
                                        // Firebase 로그인 실패
                                    }
                                }
                            });
                } else {
                    // 로그인이 되어 있지 않다면
                    Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });
    }


}
