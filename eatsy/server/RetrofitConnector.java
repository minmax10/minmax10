package com.famous.eatsy.server;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnector {
    /**
     * Server 접근을 위한 Retrofit 세팅
     */
    final static String DEVELOP_URL_1 = "http://112.214.108.69:5005"; // 서버 주소

    public static Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(DEVELOP_URL_1)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // 여기 소스 안탐 
    public static Retrofit createRetrofit(Interceptor interceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();

        return new Retrofit.Builder()
                .baseUrl(DEVELOP_URL_1)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

}
