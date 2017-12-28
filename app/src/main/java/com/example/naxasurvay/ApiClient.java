package com.example.naxasurvay;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.naxasurvay.UrlClass.BASE_URL;


public class ApiClient {

    private static Retrofit retrofit = null;


    public static ApiInterface getAPIService() {
        return ApiClient.getClient().create(ApiInterface.class);
    }


    public static Retrofit getClient() {


        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }


        return retrofit;
    }


}
