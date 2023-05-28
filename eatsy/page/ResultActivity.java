package com.famous.eatsy.page;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.famous.eatsy.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ResultActivity extends AppCompatActivity {

    /**
     * 최종 화면
     * 일정 시간마다 애니메이션을 적용하는 부분과 도착 확인 버튼 동작
     */

    LinearProgressIndicator progress;
    TextView timeView;
    ArrayList<ImageView> pins = new ArrayList<>();

    private int time = 0;

    // 애니메이션 시간, 짧을수록 빠름
    private final static int ANIMATE_TIME = 1000;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        progress = findViewById(R.id.result_progress);
        timeView = findViewById(R.id.result_tv_time);
        findViewById(R.id.result_btn_arrive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.getInstance().qrButton.callOnClick();
                finish();
            }
        });

        pins.add((ImageView)findViewById(R.id.result_pin1));
        pins.add((ImageView)findViewById(R.id.result_pin2));
        pins.add((ImageView)findViewById(R.id.result_pin3));
        pins.add((ImageView)findViewById(R.id.result_pin4));

        handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if(time > 15){
                    time = 0;
                }

                progress.setProgress(time);
                timeView.setText((15 - time) + "분");

                // 시간마다 다음 마커를 표시
                if(time < 2){
                    pins.get(0).setVisibility(View.VISIBLE);
                    pins.get(3).setVisibility(View.GONE);
                } else if (time < 4){
                    pins.get(1).setVisibility(View.VISIBLE);
                    pins.get(0).setVisibility(View.GONE);
                }else if (time < 14){
                    pins.get(2).setVisibility(View.VISIBLE);
                    pins.get(1).setVisibility(View.GONE);
                }else {
                    pins.get(3).setVisibility(View.VISIBLE);
                    pins.get(2).setVisibility(View.GONE);
                }
                time++;
               handler.postDelayed(this, ANIMATE_TIME);
            }
        };

        run.run();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}