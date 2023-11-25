package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TempHttpActivity extends AppCompatActivity {
    ListView listView;
    private List<PathInfo> pathInfoList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private static final String BASE_URL = "https://api.odsay.com/";
    private static final String API_KEY = "Bk3FXTpa4bUs3dxTOsUxSFvLGFYhTaoBDPKfSPOLdwI"; // 여기에 자신의 API 키를 넣어야 합니다.

    private static class PathInfo {
        List<List<String>> busNos = new ArrayList<>();
        List<String> startNames = new ArrayList<>();
        List<String> endNames = new ArrayList<>();

        public List<List<String>> getBusNos() {
            return busNos;
        }

        public void setBusNos(List<List<String>> busNos) {
            this.busNos = busNos;
        }

        public List<String> getStartNames() {
            return startNames;
        }

        public void setStartNames(List<String> startNames) {
            this.startNames = startNames;
        }

        public List<String> getEndNames() {
            return endNames;
        }

        public void setEndNames(List<String> endNames) {
            this.endNames = endNames;
        }

        public void addSubPath(List<String> busNos, String startName, String endName) {
            this.busNos.add(busNos);
            startNames.add(startName);
            endNames.add(endName);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < busNos.size(); i++) {
                sb.append("Bus Nos: ").append(String.join(", ", busNos.get(i))).append("\n");
                sb.append("Start: ").append(startNames.get(i)).append("\n");
                sb.append("End: ").append(endNames.get(i)).append("\n");
            }
            return sb.toString();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_http);

        listView = findViewById(R.id.listView);

        // ArrayAdapter 초기화
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        Boolean Startsearched =true;
        Boolean Goalsearched = true;

        if(Startsearched && Goalsearched) {
            searchpath();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 아이템의 데이터 가져오기
                PathInfo clickedItem = pathInfoList.get(position);

                // 새 액티비티로 이동하는 Intent 생성
                Intent intent = new Intent(TempHttpActivity.this, NewActivity.class);

                // Intent에 데이터 추가
                intent.putStringArrayListExtra("busNos", new ArrayList<>(clickedItem.getBusNos().stream().map(list -> String.join(", ", list)).collect(Collectors.toList())));
                intent.putStringArrayListExtra("startNames", new ArrayList<>(clickedItem.getStartNames()));
                intent.putStringArrayListExtra("endNames", new ArrayList<>(clickedItem.getEndNames()));

                // 새 액티비티 시작
                startActivity(intent);
            }
        });
    }

    public void searchpath(){
        // 로깅 인터셉터 생성
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 로그 수준 설정

        // OkHttp 클라이언트에 로깅 인터셉터 추가
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ODsayService odsayApi=retrofit.create(ODsayService.class);
        // 처음 출발지 목적지에 대한

        // 클래스를 apiData 만들어서 setData, getData
        // 처음에 출발지 도착지에 대한 위도 경도를 apiData에 저장
        // api를 호출해서 pathType이 12 or 2인 경우
        // pathType이 2인 경우 시내 인경우 PublicTransitResponse 에서 busNo, FirstName, endName, startName, endName, startX, startY, endX, endY를 가져와서
        // pathInfo 클래스에 저장해서 출력한다

        // pathType이 12인 경우 시외 인경우 PublicTransitResponse 에서 FirstName, endName, startX, startY, endX, endY를 apiData에 저장한다
        // 그리고 첫번째 api 호출 출발질 위도 경도 , startX, startY | FirstName, endName | 두번쨰 api 호출 endX, endY와 도착지 위도 경도
        Call<PublicTransitResponse> call =
                odsayApi.searchPublicTransitPath(API_KEY,128.12538305797565,35.1870971949491,128.1005714223781,35.16430600625013);
        call.enqueue(new Callback<PublicTransitResponse>() {
            @Override
            public void onResponse(Call<PublicTransitResponse> call, Response<PublicTransitResponse> response) {
                if (response.isSuccessful()) {
                    PublicTransitResponse searchResult = response.body();
                    StringBuilder displayText = new StringBuilder();
                    List<String> pathInfoStrings = new ArrayList<>();

                    //
                    for (PublicTransitResponse.Path path : searchResult.getResult().getPath()) {
                        PathInfo pathInfo = new PathInfo();
                        for (PublicTransitResponse.SubPath subPath : path.getSubPath()) {
                            if (subPath.getTrafficType() == 2) { // 버스 경로인 경우
                                List<String> busNos = subPath.getLane().stream()
                                        .map(PublicTransitResponse.Lane::getBusNo)
                                        .collect(Collectors.toList());

                                pathInfo.addSubPath(busNos, subPath.getStartName(), subPath.getEndName());
                            }
                        }
                        pathInfoList.add(pathInfo);
                        pathInfoStrings.add(pathInfo.toString());

                        displayText.append(pathInfo.toString()).append("\n");
                    }

                    adapter.clear();
                    adapter.addAll(pathInfoStrings);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PublicTransitResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}