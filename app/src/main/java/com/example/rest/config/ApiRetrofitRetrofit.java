package com.example.rest.config;

import com.example.rest.AppConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRetrofitRetrofit {
//    public static final String BASE_URL = AppConfig.BASE_URL;

    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit==null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

            /**
             * 1. Simple
             */
//            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
//                    .connectTimeout(2, TimeUnit.MINUTES)
//                    .writeTimeout(2, TimeUnit.MINUTES)
//                    .readTimeout(2, TimeUnit.MINUTES)
//                    .build();


            /**
             * 2. With Changing Method
             */
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .build();


            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
