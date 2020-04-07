package com.example.questionbank;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class CppFullLength extends TestLayout {

    View test;
    long timeofExercise;
    Boolean teststate;

    @Override
    public void onBackPressed() {
        if(teststate) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Quit App")
                    .setCancelable(false)
                    .setMessage("Are You Sure to quit the app?")
                    .setPositiveButton("Ok",(dialog, which) -> {
                        finishAffinity();
                        System.out.println("Exiting the app!!!");
                    })
                    .setNegativeButton("Cancel",null);

            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpp_full_length);
        test = findViewById(R.id.inflated_test);
        queCount = 4;
        teststate = false;
        timeofExercise = 300000;
        StartActivities(test,queCount,timeofExercise);
        allGone();
        String[] choseTest = new String[] {"CppFullTestA.txt","CppFullTestB.txt","CppFullTestC.txt"};
        int idx = new Random().nextInt(choseTest.length);
        String random = (choseTest[idx]);
        startup.setOnClickListener(v -> {
            startup.setVisibility(View.GONE);
            endTest.setVisibility(View.VISIBLE);
            teststate = true;
            StringBuilder rawText = new StringBuilder();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(getAssets().open(random)));
                String mLine;
                while((mLine = reader.readLine())!= null){
                    rawText.append(mLine);
                    rawText.append("qq");
                }
                setQuestions(rawText);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Error reading file !", Toast.LENGTH_LONG).show();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                allCame();
                startTimer();
            }
        });

        endTest.setOnClickListener(v -> {
            teststate = false;
            showResults();
            timer.cancel();
            endTest.setVisibility(View.GONE);
        });

    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long l) {
                timeLeft = l;
                updateText();
            }

            @Override
            public void onFinish() {
                showResults();
                teststate = false;
                endTest.setVisibility(View.GONE);
                timer.cancel();
            }
        }.start();
    }

    private void updateText() {
        int minutes =(int) timeLeft/60000;
        int seconds = (int) timeLeft % 60000 / 1000 ;
        String TimeText = "";
        if(minutes < 10) TimeText += "0";
        TimeText += minutes+":";
        if(seconds < 10) TimeText += "0";
        TimeText += seconds;
        timeText.setText(TimeText);
    }
}