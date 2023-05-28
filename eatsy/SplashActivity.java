package com.famous.eatsy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.famous.eatsy.page.MapActivity;
import com.famous.eatsy.server.RetrofitConnector;
import com.famous.eatsy.server.ServiceApi;
import com.famous.eatsy.server.model.StoreModel;
import com.famous.eatsy.server.model.StoreRes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {

    /**
     * Splash 적용 시간을 변경하는 부분
     * 변경은 ms 단위로 1.5초 => 1500 으로 적용
     */
    private final static int SPLASH_TIME = 1500;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        intent = new Intent(getApplicationContext(), MapActivity.class);

        // Splash 적용하는 부분""
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME);

        getStoreOnServer();
    }

    private void getStoreOnServer(){

        // 서버에 접근해 매장 목록을 모두 가져옴
        Retrofit retrofit = RetrofitConnector.createRetrofit();
        Call<StoreRes> call = retrofit.create(ServiceApi.class).getStore();
        call.enqueue(new Callback<StoreRes>() {
            @Override
            public void onResponse(Call<StoreRes> call, Response<StoreRes> response) {
                if(response.isSuccessful()){
                    StoreRes result = response.body();
                    if(result.isSuc()){
                        intent.putExtra("stores", new ArrayList<>(result.getStore()));
                    }
                }
            }

            @Override
            public void onFailure(Call<StoreRes> call, Throwable t) {

            }
        });

    }
}