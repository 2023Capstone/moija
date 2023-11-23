package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class NewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new2);

        TextView textView = findViewById(R.id.textView);
        // Intent에서 데이터 가져오기
        ArrayList<String> busNos = getIntent().getStringArrayListExtra("busNos");
        ArrayList<String> startNames = getIntent().getStringArrayListExtra("startNames");
        ArrayList<String> endNames = getIntent().getStringArrayListExtra("endNames");



        textView.setText(formatPathInfo(busNos, startNames, endNames));

    }
    private String formatPathInfo(ArrayList<String> busNos, ArrayList<String> startNames, ArrayList<String> endNames) {
        StringBuilder sb = new StringBuilder();

        if (busNos == null || busNos.isEmpty()) {
            sb.append("No bus data\n");
        } else {
            for (int i = 0; i < busNos.size(); i++) {
                sb.append("Bus Nos: ").append(busNos.get(i)).append("\n");
                sb.append("Start: ").append(i < startNames.size() ? startNames.get(i) : "Unknown").append("\n");
                sb.append("End: ").append(i < endNames.size() ? endNames.get(i) : "Unknown").append("\n");
            }
        }

        return sb.toString();
    }
}