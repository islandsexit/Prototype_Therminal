package com.example.prototype_therminal.data;

import com.example.prototype_therminal.model.POST_PHOTO;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface POST_API {

    @FormUrlEncoded
    @POST("docreateguest")
//    @POST(" /t/x6f1l-1650729514/post")
    Call<POST_PHOTO> Post_img64(@Field("ID") String ID, @Field("img64") String img64, @Field("name") String name);



}
