package com.example.prototype_therminal.data;

import com.example.prototype_therminal.model.GET_CODE;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GET_API {

    // ?invite_code={invite_code}&master_password=secretmasterpasswordvig
    @GET("getguestbyiclite")
    Call<GET_CODE> Check_code(@Query("ic") String invite_code);



}
