package com.example.moija;

import static com.example.moija.MapFragment.API_KEY;
import static com.example.moija.MapFragment.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.vectormap.KakaoMap;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
//검색 페이지에 관한 내용
public class SearchPage extends AppCompatActivity {
    //검색창
    private EditText startEditText;
    //시작점을 검색하는건지 도착지점을 검색하는지 나누는 변수 (0: 시작점 1: 도착점)
    private int Searchcode=0;
    //시작점을 정했는지
    private boolean Startsearched=false;
    //도착지점을 정했는지
    private boolean Goalsearched=false;
    private MapFragment.Place Startplace;
    private MapFragment.Place Goalplace;
    private EditText goalEditText;
    //검색결과를 담을 리스트뷰
    private ListView resultListView;

    private ImageButton backbutton;
    //REST API 에서 검색한 장소들을 Place 형태로 SearchResponse가 받음
    public class SearchResponse {
        private List<MapFragment.Place> documents;

        public List<MapFragment.Place> getDocuments() {return documents;}
    }
    //카카오맵 검색 API 쿼리셋팅
    public interface KakaoMapApi {
        @GET("/v2/local/search/keyword.json")
        Call<SearchResponse> searchPlaces(
                @Header("Authorization") String apiKey,
                @Query("query") String query,
                @Query("x") double x,
                @Query("y") double y

        );
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        startEditText = findViewById(R.id.startEditText);
        goalEditText=findViewById(R.id.goalEditText);
        resultListView = findViewById(R.id.resultListView);
        backbutton=findViewById(R.id.backbutton);
        //뒤로가기 버튼 누르면 맵 채팅 화면으로 이동
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(SearchPage.this, Fragment_Chat_Map.class);
                startActivity(myIntent);
            }
        });
        //시작점을 입력하는 EditText를 눌러 Focus되었을때
        startEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText에 포커스가 주어진 경우
                    String text =startEditText.getText().toString();
                    // 시작점을 정했으면 변경하려는것이기때문에
                    if (Startsearched==true) {
                        Startsearched=false;
                        startEditText.setText(""); // 편의를 위해 EditText의 텍스트를 모두 제거
                    }
                    //도착점이 안정해졌으면
                    if(Goalsearched==false){
                        //도착점 검색창에 쓰다만 것들 삭제
                        goalEditText.setText("");
                    }
                    //검색결과 숨김
                    resultListView.setVisibility(View.INVISIBLE);
                }
            }
        });
        //도착점을 검색하는 EditText에 focus되었을때, startEditText와 같은 로직
        goalEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String text =goalEditText.getText().toString();
                    if (Goalsearched==true) {
                        Goalsearched=false;
                        goalEditText.setText("");
                    }
                    if(Startsearched==false){
                        startEditText.setText("");
                    }
                    //검색결과 숨김
                    resultListView.setVisibility(View.INVISIBLE);
                }
            }
        });
        //startEditText에서 엔터키 누르면
        startEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //검색 실행
                Searchcode=0;
                search(startEditText,0);
                return true;
            }
        });
        //goalEditText에서 엔터키 누르면
        goalEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //검색 실행
                Searchcode=1;
                search(goalEditText,1);
                return true;
            }
        });
        //검색결과중 하나를 누르면 
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //검색결과 중 하나 클릭하면
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //만약 시작점을 찾는중이면
                if(Searchcode==0)
                {

                    MapFragment.Place selected=(MapFragment.Place) resultListView.getItemAtPosition(position);
                    //출발위치를 선택한 위치로 결정
                    startEditText.setText("출발 위치: " + selected.getPlaceName());
                    Startplace=selected;
                    Startsearched=true;
                    Mylocation.StartPlace=selected;
                    //아직 도착점 안정했으면
                    if(Goalsearched==false)
                    {
                        //도착점 검색창에 focus를 넘겨줌
                        goalEditText.requestFocus();
                        goalEditText.setText("");
                    }
                    //도착점 정해져있으면
                    else if(Goalsearched==true)
                    {
                        //인텐트를 이용해 메인액티비티로 넘어가는데
                        Intent myIntent=new Intent(SearchPage.this, Fragment_Chat_Map.class);
                        //FindGoal이라는 String Key를 넘겨줌
                        //메인액티비티에서 이 Key를 확인하고 길찾기 메서드를 실행시킴
                        myIntent.putExtra("key","FindGoal");
                        //선택했던 장소에 대한 정보를 메인으로 넘김

                        //액티비티 이동
                        startActivity(myIntent);
                    }
                    resultListView.setVisibility(View.INVISIBLE);

                    Startsearched=true;
                }
                //도착점을 찾는중이었으면
                else if(Searchcode==1)
                {
                    MapFragment.Place selected=(MapFragment.Place) resultListView.getItemAtPosition(position);
                    //도착위치를 선택한 위치로 결정
                    goalEditText.setText("도착 위치: " + selected.getPlaceName());
                    Goalplace=selected;
                    Goalsearched=true;
                    Mylocation.selectedPlace=selected;
                    resultListView.setVisibility(View.INVISIBLE);
                    //시작점 안정했으면
                    if(Startsearched==false)
                    {
                        //시작점 검색창으로 focus 이동
                        startEditText.requestFocus();
                        startEditText.setText("");
                    }
                    //시작점 정해져있으면
                    else if(Startsearched==true)
                    {
                        //인텐트를 이용해 메인액티비티로 넘어가는데
                        Intent myIntent=new Intent(SearchPage.this, Fragment_Chat_Map.class);
                        //FindGoal이라는 String Key를 넘겨줌
                        //메인액티비티에서 이 Key를 확인하고 길찾기 메서드를 실행시킴
                        myIntent.putExtra("key","FindGoal");
                        //선택했던 장소에 대한 정보를 메인으로 넘김

                        //액티비티 이동
                        startActivity(myIntent);
                    }

                    Goalsearched=true;
                }


            }

        });


    }
    //ListView의 CustomAdapter
    public class CustomAdapter extends ArrayAdapter<MapFragment.Place> {
        private LayoutInflater inflater;

        public CustomAdapter(Context context, List<MapFragment.Place> places) {
            super(context, R.layout.list_item_place, places);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_place, parent, false);
            }

            TextView placeNameTextView = view.findViewById(R.id.placeNameTextView);
            TextView placeAddressTextView = view.findViewById(R.id.placeAddressTextView);
            TextView placedistance=view.findViewById(R.id.distance);
            MapFragment.Place place = getItem(position);

            if (place != null) {
                // 장소 이름을 텍스트뷰에 설정
                placeNameTextView.setText(place.getPlaceName());
                placeNameTextView.setTextSize(20);
                //장소 주소를 설정
                placeAddressTextView.setText(place.getAddressName());
                //장소의 Location을 받아오고
                Location myplace=new Location("my location");
                Location findplace=new Location("finded location");
                findplace.setLatitude(place.getY());
                findplace.setLongitude(place.getX());
                //현재위치와의 거리를 나타냄 (시작점을 검색으로 하면 시작점과의 거리로 바꿔야할수도 있음)
                placedistance.setText(Math.floor(Mylocation.Lastlocation.distanceTo(findplace)/1000)+"km");
            }

            return view;
        }
    }
    //검색 로직
    public void search(EditText searchbox,int searchcode) {

        String query = searchbox.getText().toString();
        if (!query.isEmpty()) {
            // Retrofit2를 사용하여 카카오맵 REST API에 검색 요청을 보냄
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            KakaoMapApi kakaoMapApi = retrofit.create(KakaoMapApi.class);

            Call<SearchResponse> call = kakaoMapApi.searchPlaces("KakaoAK " + API_KEY, query,Mylocation.Lastlocation.getLongitude(),Mylocation.Lastlocation.getLatitude());

            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    if (response.isSuccessful()) {
                        //searchResponse에 api 응답을 받고
                        SearchResponse searchResponse = response.body();
                        List<MapFragment.Place> searchedplace = searchResponse.getDocuments();
                        List<MapFragment.Place> filteredplace = new ArrayList<>();
                        //도착점과 시작점 중복 설정 방지를 위한 코드들
                        if(searchcode==0 && Goalsearched==true) {
                            for (MapFragment.Place searchplace : searchedplace) {
                                if (!(searchplace.getPlaceName().equals(Mylocation.selectedPlace.getPlaceName()))) {
                                    filteredplace.add(searchplace);
                                }
                            }
                            if (searchResponse != null && searchResponse.getDocuments() != null) {
                                CustomAdapter adapter = new CustomAdapter(SearchPage.this, filteredplace);
                                resultListView.setAdapter(adapter);
                            }
                            resultListView.setVisibility(View.VISIBLE);
                        }
                        if(searchcode==1 && Startsearched==true) {

                            for (MapFragment.Place searchplace : searchedplace) {
                                if (!(searchplace.getPlaceName().equals(Mylocation.StartPlace.getPlaceName()))) {
                                    filteredplace.add(searchplace);
                                }
                            }
                            if (searchResponse != null && searchResponse.getDocuments() != null) {
                                CustomAdapter adapter = new CustomAdapter(SearchPage.this, filteredplace);
                                resultListView.setAdapter(adapter);
                            }
                            resultListView.setVisibility(View.VISIBLE);
                        }
                        else {
                            if (searchResponse != null && searchResponse.getDocuments() != null) {
                                CustomAdapter adapter = new CustomAdapter(SearchPage.this, searchResponse.getDocuments());
                                resultListView.setAdapter(adapter);
                            }
                            resultListView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        }
    }

}