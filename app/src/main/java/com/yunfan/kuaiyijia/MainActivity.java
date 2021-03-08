package com.yunfan.kuaiyijia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Intent mIntentScanCarLoad;
    private Intent mIntentSearchCarNo;
    private Intent mIntentScanCarGo;
    private Intent mIntentScanUnload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIntentSearchCarNo = new Intent(this, searchCarNo.class);
        mIntentScanCarLoad = new Intent(this, scanCarLoad.class);
        mIntentScanCarGo = new Intent(this, scanCarGo.class);
        mIntentScanUnload = new Intent(this, scanUnload.class);

        Button btn1 = findViewById(R.id.button1);
        Button btn2 = findViewById(R.id.button2);
        Button btn3 = findViewById(R.id.button3);
        Button btn4 = findViewById(R.id.button4);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mIntentSearchCarNo);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mIntentScanCarLoad);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mIntentScanCarGo);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mIntentScanUnload);
            }
        });


    }
}
