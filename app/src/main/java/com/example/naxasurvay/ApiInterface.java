package com.example.naxasurvay;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.example.naxasurvay.UrlClass.URL_DATA_SEND;


public interface ApiInterface {

    @POST(URL_DATA_SEND)
    @FormUrlEncoded
    Call<String> uploadForm(@Field("data") String jsonToSend);


}
