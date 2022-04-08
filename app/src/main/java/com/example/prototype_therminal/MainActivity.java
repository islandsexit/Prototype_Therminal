package com.example.prototype_therminal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.prototype_therminal.data.GET_API;
import com.example.prototype_therminal.model.GET_CODE;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String APP_TAG = "retrofit-json-variable";
    private static final String BASE_URL = "http://192.168.48.131/";

    private EditText invite_code_ET;
    private Button btn_sbmt;

    private String invite_code;
    private String name_txt;
    private String id_txt;
    private TextView Result_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        invite_code_ET = findViewById(R.id.invite_code_ET);
        btn_sbmt = findViewById(R.id.btn_sbmt);
        Result_TV = findViewById(R.id.RESULT_TV);
        goNewView();
        btn_sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invite_code = invite_code_ET.getText().toString();
                loadProfile(invite_code);
            }
        });
    }


    private void loadProfile(String invite_code) {
        GsonBuilder gsonBuilder = new GsonBuilder();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();

        GET_API GETApi = retrofit.create(GET_API.class);
        Call<GET_CODE> call = GETApi.Check_code(invite_code, "secretmasterpasswordvig");
        call.enqueue(new Callback<GET_CODE>() {
            @Override
            public void onResponse(Call<GET_CODE> call, Response<GET_CODE> response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200) {
                        GET_CODE GETCODE = response.body();

                        final List data_get = GETCODE.getData();
                        String RESULT = (String) data_get.get(0);
                        String id = (String) data_get.get(1);
                        String name = (String) data_get.get(2);
                        Log.w(APP_TAG, "onResponse| response: RESULT:" + RESULT+" name:"+ " id: "+id);
                        runOnUiThread(() -> {
                            if(RESULT.equals("SUCCESS")) {
                                name_txt = name;
                                id_txt = id;
                                Result_TV.setText(RESULT);

                            }
                            else {
                                    Result_TV.setText(RESULT);
                            }

                        });
                        if (RESULT.equals("SUCCESS")){
                            goNewView();
                        }
                    } else {
                        Log.e(APP_TAG, "onResponse | status: " + statusCode);
                    }
                } catch (Exception e) {
                    Log.e(APP_TAG, "onResponse | exception", e);
                }
            }

            @Override
            public void onFailure(Call<GET_CODE> call, Throwable t) {
                runOnUiThread(()->{
                    Result_TV.setText("Network ERROR");
                });
                Log.e(APP_TAG, "onFailure", t);
            }
        });
    } //GET


    public void goNewView(){

                // Говорим между какими Activity будет происходить связь
                Intent intent = new Intent(this, Photo.class);

                // указываем первым параметром ключ, а второе значение
                // по ключу мы будем получать значение с Intent
                intent.putExtra("name", name_txt);
                intent.putExtra("id", id_txt);

                // показываем новое Activity
                startActivity(intent);
        }
    }


