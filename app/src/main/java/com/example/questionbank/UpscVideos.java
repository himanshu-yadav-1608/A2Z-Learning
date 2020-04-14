package com.example.questionbank;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class UpscVideos extends AppCompatActivity {

    Button b1,b2,b3,b4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upsc_videos);

        b1 = findViewById(R.id.b11);
        b2 = findViewById(R.id.b12);
        b3 = findViewById(R.id.b13);
        b4 = findViewById(R.id.b14);
        final Button[] bgrp = new Button[]{b1,b2,b3,b4};

        final String[] linklist = new String[]{"qhz6_nGD4Rw","7jZ4RtMaU9s","yzeJ7tVcHQA","IxpPoVc7y3U"};

        for(int i = 0 ; i < bgrp.length ; i++) {
            int finalI = i;
            bgrp[i].setOnClickListener(v -> {
                String str = "https://www.youtube.com/watch?v=" + linklist[finalI];
                Uri webpage = Uri.parse(str);
                Intent webIntent = new Intent(Intent.ACTION_VIEW,webpage);
                startActivity(webIntent);
            });
        }

    }

}