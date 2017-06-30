package com.spx.smoothscrolltest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button linearLayoutBtn = (Button) findViewById(R.id.linearlayoutBtn);
        linearLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LinearLayoutTest.class);
                startActivity(intent);
            }
        });
        Button listviewBtn = (Button) findViewById(R.id.listviewBtn);
        Button recyclerviewBtn = (Button) findViewById(R.id.recyclerviewBtn);
    }
}
