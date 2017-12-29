package com.example.naxasurvay;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static com.example.naxasurvay.UrlClass.URL_DATA_SEND;
import static com.example.naxasurvay.UrlClass.URL_DATA_SEND_FILE_UPLOAD;
import static com.example.naxasurvay.UrlClass.URL_DATA_SEND_FILE_UPLOAD_TEST;


public interface ApiInterface {

    @POST(URL_DATA_SEND)
    @FormUrlEncoded
    Call<String> uploadForm(@Field("data") String jsonToSend);

    @Multipart
    @POST(URL_DATA_SEND_FILE_UPLOAD)
    Call<UploadResponse> uploadFormWithPhotoFile(@Part MultipartBody.Part file,
                                         @Part("data") RequestBody jsonToSend);

}
