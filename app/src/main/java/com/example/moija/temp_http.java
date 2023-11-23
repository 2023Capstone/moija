package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.moija.ODsayApiKey.ODsayAPI_KEY;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class temp_http extends AppCompatActivity {
    TextView textView;
    ListView listView;
    ArrayList<String> listItems;

    ArrayList<String> dataList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_http);

        listView = findViewById(R.id.listView); // ListView의 ID를 정확히 지정해야 합니다.
        listItems = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 항목 처리
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        new FetchDataTask().execute(ODsayAPI_KEY);
    }

    public class FetchDataTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {

            String apiKey = params[0];
            String urlString = "https://api.odsay.com/v1/api/searchPubTransPathT?SX=128.12538305797565&SY=35.1870971949491&EX=128.1005714223781&EY=35.16430600625013&apiKey=" + ODsayAPI_KEY;
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("ODsay", result.toString() + "\n");
            return result.toString();

        }

        protected void onPostExecute(String result) {
            try {
                // 결과 문자열을 JSON 객체로 변환
                JSONObject jsonResponse = new JSONObject(result);

                JSONArray paths = jsonResponse.getJSONObject("result").getJSONArray("path");

                StringBuilder extractedData = new StringBuilder();

                listItems.clear();
                int maxItems = Math.min(paths.length(), 3);
                for (int i = 0; i < maxItems; i++) {
                    JSONObject path = paths.getJSONObject(i);
                    JSONObject info = path.getJSONObject("info");
                    String firstStartStation = info.getString("firstStartStation");
                    String lastEndStation = info.getString("lastEndStation");

                    JSONArray subPaths = path.getJSONArray("subPath");
                    for (int j = 0; j < subPaths.length(); j++) {
                        JSONObject subPath = subPaths.getJSONObject(j);
                        if (subPath.has("lane")) {
                            JSONArray lanes = subPath.getJSONArray("lane");
                            for (int k = 0; k < lanes.length(); k++) {
                                JSONObject lane = lanes.getJSONObject(k);
                                String busNo = lane.getString("busNo");

                                // 필요한 정보만 추가
                                extractedData.append("Bus No: ").append(busNo)
                                        .append(", First Start Station: ").append(firstStartStation)
                                        .append(", Last End Station: ").append(lastEndStation)
                                        .append("\n");
                                break; // 첫번째 busNo만 사용
                            }
                        }
                        break; // 첫번째 subPath만 사용
                    }
                    listItems.add(extractedData.toString());
                }

                // 어댑터에 변경 사항 알림
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
                // 에러 처리
            }

            Log.d("FetchDataTask", "Response: " + result + "\n");
        }
    }
}