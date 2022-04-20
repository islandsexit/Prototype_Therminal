package com.example.prototype_therminal;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.example.prototype_therminal.data.POST_API;
import com.example.prototype_therminal.model.POST_PHOTO;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Post_class {
    public String RESULT_FROM_POST;
    public String MSG_FROM_POST;

    public void setRESULT_FROM_POST(String RESULT_FROM_POST) {
        this.RESULT_FROM_POST = RESULT_FROM_POST;
    }

    //TODO POST_IMG_64
    public void POST_img64(String ID, String img64, String name) {
        GsonBuilder gsonBuilder = new GsonBuilder();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.48.114:8080")
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();

        POST_API post_api = retrofit.create(POST_API.class);
        Call<POST_PHOTO> call = post_api.Post_img64(ID, img64, name);
        Log.e("POST", "img64:"+img64.charAt(2)+" id= "+ID+" name: "+name);
        call.enqueue(new Callback<POST_PHOTO>() {
            @Override
            public void onResponse(Call<POST_PHOTO> call, Response<POST_PHOTO> response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200) {
                        POST_PHOTO POST_PHOTO = response.body();

                        final List data_get = POST_PHOTO.getResponse();
                        String RESULT = (String) data_get.get(0);
                        String msg = (String) data_get.get(1);
                        Log.w("POST", "onResponse| response: " + "Result: " + RESULT+" msg: " + msg);

                        RESULT_FROM_POST = POST_PHOTO.getRESULT();
                        MSG_FROM_POST = POST_PHOTO.getMsg();


                            if(RESULT_FROM_POST.equals("SUCCESS")){

                            }
                            else {


                            }





                    } else {
                        Log.e("POST", "onResponse | status: " + statusCode);

                            RESULT_FROM_POST = "ERROR";
                            MSG_FROM_POST = "ОШИБКА СЕРВЕРА";




                    }
                } catch (Exception e) {
                    Log.e("POST", "onResponse | exception", e);

                        RESULT_FROM_POST = "ERROR";
                        MSG_FROM_POST = "ВНУТРЕННЯЯ ОШИБКА";


                }
            }

            @Override
            public void onFailure(Call<POST_PHOTO> call, Throwable t) {
                Log.e("POST", "onFailure", t);


            }
        });
    }
}
