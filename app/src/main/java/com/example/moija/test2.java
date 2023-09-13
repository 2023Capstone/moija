package com.example.moija;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class test2 extends AppCompatActivity {

    TextView readText;

    EditText writeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        writeEdit = findViewById(R.id.write_edit);

        readText = findViewById(R.id.read_text);

        Button readBtn = findViewById(R.id.read_btn);
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //쓰기
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");

                myRef.setValue(writeEdit.getText().toString());

                // 읽기
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
                        readText.setText("value: " + value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        readText.setText("error: " + error.toException());
                    }
                });
            }
        });
    }
}