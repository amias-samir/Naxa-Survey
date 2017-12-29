package com.example.naxasurvay;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 12/29/17
 * by nishon.tan@gmail.com
 */

public class UploadResponse {


    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @SerializedName("data")
    private String data;
}
