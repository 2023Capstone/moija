//package com.example.moija;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import static com.example.moija.ODsayApiKey.ODsayAPI_KEY;
//
//public class temp_listview extends AppCompatActivity {
//    TextView textView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_temp_listview);
//
//        textView = findViewById(R.id.textView);
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.odsay.com/v1/api/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        ODsayService odsayService = retrofit.create(ODsayService.class);
//        Call<PublicTransitResponse> call = odsayService.searchPublicTransitPath(
//                35.1870971949491,128.12538305797565, 35.16430600625013, 128.1005714223781,"Bk3FXTpa4bUs3dxTOsUxSFvLGFYhTaoBDPKfSPOLdwI");
//        call.enqueue(new Callback<PublicTransitResponse>() {
//            @Override
//            public void onResponse(Call<PublicTransitResponse> call, Response<PublicTransitResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    PublicTransitResponse.Result result = response.body().getResult();
//                    List<PublicTransitResponse.Path> paths = result.getPath();
//                    Log.d("ODsay" , response.body().toString());
//                    textView.setText(response.body().toString());
//
//                    for (PublicTransitResponse.Path path : paths) {
//                        int pathType = path.getPathType();
//                        String startStation = path.getInfo().getFirstStartStation();
//                        String endStation = path.getInfo().getLastEndStation();
//
//                        // 로그로 각 경로의 정보 출력
//                        Log.d("ODsay", "Path Type: " + pathType);
//                        Log.d("ODsay", "Start Station: " + startStation);
//                        Log.d("ODsay", "End Station: " + endStation);
//
//                        // 버스 번호 로그에 추가
//                        for (PublicTransitResponse.SubPath subPath : path.getSubPath()) {
//                            PublicTransitResponse.Lane lane = subPath.getLane();
//                            if (lane != null) { // 'lane' 객체가 null이 아닌 경우에만 처리
//                                Log.d("ODsay", "Bus Number: " + lane.getBusNo());
//                            }
//                        }
//                    }
//                }
//            }
//            @Override
//            public void onFailure(Call<PublicTransitResponse> call, Throwable t) {
//                Log.d("ODsay", "실패" + t);
//            }
//        });
//    }
//}
//
