package com.example.prototype_therminal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
    private EditText invite_code_ET1;
    private EditText invite_code_ET2;
    private EditText invite_code_ET3;
    private EditText invite_code_ET4;
    private EditText invite_code_ET5;

    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_0;

    private Button btn_X;

    private Button btn_sbmt;

    private String invite_code;
    private String name_txt;
    private String id_txt;
    private TextView Result_TV;
    private Boolean debag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        invite_code_ET = findViewById(R.id.invite_code_ET);
        invite_code_ET1 = findViewById(R.id.invite_code_ET1);
        invite_code_ET2 = findViewById(R.id.invite_code_ET2);
        invite_code_ET3 = findViewById(R.id.invite_code_ET3);
        invite_code_ET4 = findViewById(R.id.invite_code_ET4);
        invite_code_ET5 = findViewById(R.id.invite_code_ET5);

        btn_sbmt = findViewById(R.id.btn_sbmt);

        btn_1 = findViewById(R.id.button);
        btn_2 = findViewById(R.id.button2);
        btn_3 = findViewById(R.id.button3);
        btn_4 = findViewById(R.id.button4);
        btn_5 = findViewById(R.id.button5);
        btn_6 = findViewById(R.id.button6);
        btn_7 = findViewById(R.id.button7);
        btn_8 = findViewById(R.id.button8);
        btn_9 = findViewById(R.id.button9);
        btn_0 = findViewById(R.id.button_0);

        btn_0.setClickable(true);
        btn_9.setClickable(true);
        btn_8.setClickable(true);
        btn_7.setClickable(true);
        btn_6.setClickable(true);
        btn_5.setClickable(true);
        btn_4.setClickable(true);
        btn_3.setClickable(true);
        btn_2.setClickable(true);
        btn_1.setClickable(true);
        btn_sbmt.setClickable(true);


        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invite_code_ET.getText().toString().trim().matches("")){
                    invite_code_ET.setText(btn_1.getText().toString());
                }
                else if(invite_code_ET1.getText().toString().trim().matches("")){
                    invite_code_ET1.setText(btn_1.getText().toString());
                }
                else if(invite_code_ET2.getText().toString().trim().matches("")){
                    invite_code_ET2.setText(btn_1.getText().toString());
                }
                else if(invite_code_ET3.getText().toString().trim().matches("")){
                    invite_code_ET3.setText(btn_1.getText().toString());
                }
                else if(invite_code_ET4.getText().toString().trim().matches("")){
                    invite_code_ET4.setText(btn_1.getText().toString());
                }
                else if(invite_code_ET5.getText().toString().trim().matches("")){
                    invite_code_ET5.setText(btn_1.getText().toString());
                }
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invite_code_ET.getText().toString().trim().matches("")){
                    invite_code_ET.setText(btn_2.getText().toString());
                }
                else if(invite_code_ET1.getText().toString().trim().matches("")){
                    invite_code_ET1.setText(btn_2.getText().toString());
                }
                else if(invite_code_ET2.getText().toString().trim().matches("")){
                    invite_code_ET2.setText(btn_2.getText().toString());
                }
                else if(invite_code_ET3.getText().toString().trim().matches("")){
                    invite_code_ET3.setText(btn_2.getText().toString());
                }
                else if(invite_code_ET4.getText().toString().trim().matches("")){
                    invite_code_ET4.setText(btn_2.getText().toString());
                }
                else if(invite_code_ET5.getText().toString().trim().matches("")){
                    invite_code_ET5.setText(btn_2.getText().toString());
                }
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invite_code_ET.getText().toString().trim().matches("")){
                    invite_code_ET.setText(btn_3.getText().toString());
                }
                else if(invite_code_ET1.getText().toString().trim().matches("")){
                    invite_code_ET1.setText(btn_3.getText().toString());
                }
                else if(invite_code_ET2.getText().toString().trim().matches("")){
                    invite_code_ET2.setText(btn_3.getText().toString());
                }
                else if(invite_code_ET3.getText().toString().trim().matches("")){
                    invite_code_ET3.setText(btn_3.getText().toString());
                }
                else if(invite_code_ET4.getText().toString().trim().matches("")){
                    invite_code_ET4.setText(btn_3.getText().toString());
                }
                else if(invite_code_ET5.getText().toString().trim().matches("")){
                    invite_code_ET5.setText(btn_3.getText().toString());
                }
            }
        });
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invite_code_ET.getText().toString().trim().matches("")){
                    invite_code_ET.setText(btn_4.getText().toString());
                }
                else if(invite_code_ET1.getText().toString().trim().matches("")){
                    invite_code_ET1.setText(btn_4.getText().toString());
                }
                else if(invite_code_ET2.getText().toString().trim().matches("")){
                    invite_code_ET2.setText(btn_4.getText().toString());
                }
                else if(invite_code_ET3.getText().toString().trim().matches("")){
                    invite_code_ET3.setText(btn_4.getText().toString());
                }
                else if(invite_code_ET4.getText().toString().trim().matches("")){
                    invite_code_ET4.setText(btn_4.getText().toString());
                }
                else if(invite_code_ET5.getText().toString().trim().matches("")){
                    invite_code_ET5.setText(btn_4.getText().toString());
                }
            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invite_code_ET.getText().toString().trim().matches("")){
                    invite_code_ET.setText(btn_5.getText().toString());
                }
                else if(invite_code_ET1.getText().toString().trim().matches("")){
                    invite_code_ET1.setText(btn_5.getText().toString());
                }
                else if(invite_code_ET2.getText().toString().trim().matches("")){
                    invite_code_ET2.setText(btn_5.getText().toString());
                }
                else if(invite_code_ET3.getText().toString().trim().matches("")){
                    invite_code_ET3.setText(btn_5.getText().toString());
                }
                else if(invite_code_ET4.getText().toString().trim().matches("")){
                    invite_code_ET4.setText(btn_5.getText().toString());
                }
                else if(invite_code_ET5.getText().toString().trim().matches("")){
                    invite_code_ET5.setText(btn_5.getText().toString());
                }
            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invite_code_ET.getText().toString().trim().matches("")){
                    invite_code_ET.setText(btn_6.getText().toString());
                }
                else if(invite_code_ET1.getText().toString().trim().matches("")){
                    invite_code_ET1.setText(btn_6.getText().toString());
                }
                else if(invite_code_ET2.getText().toString().trim().matches("")){
                    invite_code_ET2.setText(btn_6.getText().toString());
                }
                else if(invite_code_ET3.getText().toString().trim().matches("")){
                    invite_code_ET3.setText(btn_6.getText().toString());
                }
                else if(invite_code_ET4.getText().toString().trim().matches("")){
                    invite_code_ET4.setText(btn_6.getText().toString());
                }
                else if(invite_code_ET5.getText().toString().trim().matches("")){
                    invite_code_ET5.setText(btn_6.getText().toString());
                }
            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invite_code_ET.getText().toString().trim().matches("")){
                    invite_code_ET.setText(btn_7.getText().toString());
                }
                else if(invite_code_ET1.getText().toString().trim().matches("")){
                    invite_code_ET1.setText(btn_7.getText().toString());
                }
                else if(invite_code_ET2.getText().toString().trim().matches("")){
                    invite_code_ET2.setText(btn_7.getText().toString());
                }
                else if(invite_code_ET3.getText().toString().trim().matches("")){
                    invite_code_ET3.setText(btn_7.getText().toString());
                }
                else if(invite_code_ET4.getText().toString().trim().matches("")){
                    invite_code_ET4.setText(btn_7.getText().toString());
                }
                else if(invite_code_ET5.getText().toString().trim().matches("")){
                    invite_code_ET5.setText(btn_7.getText().toString());
                }
            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invite_code_ET.getText().toString().trim().matches("")){
                    invite_code_ET.setText(btn_8.getText().toString());
                }
                else if(invite_code_ET1.getText().toString().trim().matches("")){
                    invite_code_ET1.setText(btn_8.getText().toString());
                }
                else if(invite_code_ET2.getText().toString().trim().matches("")){
                    invite_code_ET2.setText(btn_8.getText().toString());
                }
                else if(invite_code_ET3.getText().toString().trim().matches("")){
                    invite_code_ET3.setText(btn_8.getText().toString());
                }
                else if(invite_code_ET4.getText().toString().trim().matches("")){
                    invite_code_ET4.setText(btn_8.getText().toString());
                }
                else if(invite_code_ET5.getText().toString().trim().matches("")){
                    invite_code_ET5.setText(btn_8.getText().toString());
                }
            }
        });
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invite_code_ET.getText().toString().trim().matches("")){
                    invite_code_ET.setText(btn_9.getText().toString());
                }
                else if(invite_code_ET1.getText().toString().trim().matches("")){
                    invite_code_ET1.setText(btn_9.getText().toString());
                }
                else if(invite_code_ET2.getText().toString().trim().matches("")){
                    invite_code_ET2.setText(btn_9.getText().toString());
                }
                else if(invite_code_ET3.getText().toString().trim().matches("")){
                    invite_code_ET3.setText(btn_9.getText().toString());
                }
                else if(invite_code_ET4.getText().toString().trim().matches("")){
                    invite_code_ET4.setText(btn_9.getText().toString());
                }
                else if(invite_code_ET5.getText().toString().trim().matches("")){
                    invite_code_ET5.setText(btn_9.getText().toString());
                }
            }
        });
        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invite_code_ET.getText().toString().trim().matches("")){
                    invite_code_ET.setText(btn_0.getText().toString());
                }
                else if(invite_code_ET1.getText().toString().trim().matches("")){
                    invite_code_ET1.setText(btn_0.getText().toString());
                }
                else if(invite_code_ET2.getText().toString().trim().matches("")){
                    invite_code_ET2.setText(btn_0.getText().toString());
                }
                else if(invite_code_ET3.getText().toString().trim().matches("")){
                    invite_code_ET3.setText(btn_0.getText().toString());
                }
                else if(invite_code_ET4.getText().toString().trim().matches("")){
                    invite_code_ET4.setText(btn_0.getText().toString());
                }
                else if(invite_code_ET5.getText().toString().trim().matches("")){
                    invite_code_ET5.setText(btn_0.getText().toString());
                }
            }
        });

        btn_X = findViewById(R.id.btn_X);
        btn_X.setClickable(true);
        btn_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invite_code_ET.setText("");
                invite_code_ET1.setText("");
                invite_code_ET2.setText("");
                invite_code_ET3.setText("");
                invite_code_ET4.setText("");
                invite_code_ET5.setText("");

            }
        });




        Result_TV = findViewById(R.id.RESULT_TV);


        if(debag == true){
            name_txt = "Test";
            id_txt = "81";
            goNewView();
        }
        btn_sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_code();
                if (invite_code.length()!=6){
                    Result_TV.setText("КОД НЕПОЛНЫЙ");
                    Result_TV.setTextColor(Color.parseColor("red"));
                    invite_code_ET.setText("");
                    invite_code_ET1.setText("");
                    invite_code_ET2.setText("");
                    invite_code_ET3.setText("");
                    invite_code_ET4.setText("");
                    invite_code_ET5.setText("");
                }else{
                    Result_TV.setTextColor(Color.parseColor("gray"));
                    Result_TV.setText("Подождите...");
                    loadProfile(invite_code);
                    invite_code_ET.setText("");
                    invite_code_ET1.setText("");
                    invite_code_ET2.setText("");
                    invite_code_ET3.setText("");
                    invite_code_ET4.setText("");
                    invite_code_ET5.setText("");
                    btn_0.setClickable(false);
                    btn_9.setClickable(false);
                    btn_8.setClickable(false);
                    btn_7.setClickable(false);
                    btn_6.setClickable(false);
                    btn_5.setClickable(false);
                    btn_4.setClickable(false);
                    btn_3.setClickable(false);
                    btn_2.setClickable(false);
                    btn_1.setClickable(false);
                    btn_sbmt.setClickable(false);
                    btn_X.setClickable(false);
                }

            }
        });

        invite_code_ET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(invite_code_ET.length() == 1){
                    invite_code_ET1.requestFocus();
                }
                else if (invite_code_ET.length() > 1){
                    invite_code_ET.setText(invite_code_ET.getText().toString().charAt(0));
                }
                return false;
            }
        });
        invite_code_ET1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(invite_code_ET1.length() == 1){
                    invite_code_ET2.requestFocus();
                }
                else if (invite_code_ET1.length() > 1){
                    invite_code_ET1.setText(invite_code_ET1.getText().toString().charAt(0));
                }
                return false;
            }
        });
        invite_code_ET2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(invite_code_ET2.length() == 1){
                    invite_code_ET3.requestFocus();
                }
                else if (invite_code_ET2.length() > 1){
                    invite_code_ET2.setText(invite_code_ET2.getText().toString().charAt(0));
                }
                return false;
            }
        });
        invite_code_ET3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(invite_code_ET3.length() == 1){
                    invite_code_ET4.requestFocus();
                }
                else if (invite_code_ET3.length() > 1){
                    invite_code_ET3.setText(invite_code_ET3.getText().toString().charAt(0));
                }
                return false;
            }
        });
        invite_code_ET4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(invite_code_ET4.length() == 1){
                    invite_code_ET5.requestFocus();
                }
                else if (invite_code_ET4.length() > 1){
                    invite_code_ET4.setText(invite_code_ET4.getText().toString().charAt(0));
                }
                return false;
            }
        });
        invite_code_ET5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(invite_code_ET5.length() == 1){
                    get_code();
                    if (invite_code.length()!=6){
                        Result_TV.setText("КОД НЕПОЛНЫЙ");
                        Result_TV.setTextColor(Color.parseColor("red"));
                        invite_code_ET.setText("");
                        invite_code_ET1.setText("");
                        invite_code_ET2.setText("");
                        invite_code_ET3.setText("");
                        invite_code_ET4.setText("");
                        invite_code_ET5.setText("");
                    }else{
                        Result_TV.setText("Подождите...");
                        Result_TV.setTextColor(Color.parseColor("gray"));
                        loadProfile(invite_code);
                        invite_code_ET.setText("");
                        invite_code_ET1.setText("");
                        invite_code_ET2.setText("");
                        invite_code_ET3.setText("");
                        invite_code_ET4.setText("");
                        invite_code_ET5.setText("");
                        btn_0.setClickable(false);
                        btn_9.setClickable(false);
                        btn_8.setClickable(false);
                        btn_7.setClickable(false);
                        btn_6.setClickable(false);
                        btn_5.setClickable(false);
                        btn_4.setClickable(false);
                        btn_3.setClickable(false);
                        btn_2.setClickable(false);
                        btn_1.setClickable(false);
                        btn_sbmt.setClickable(false);



                    }
                }
                else if (invite_code_ET5.length() > 0){
                    invite_code_ET5.setText(invite_code_ET5.getText().toString().charAt(0));
                }
                return false;
            }
        });

    }

    private void get_code(){
         invite_code = invite_code_ET.getText().toString() + invite_code_ET1.getText().toString() + invite_code_ET2.getText().toString() + invite_code_ET3.getText().toString() + invite_code_ET4.getText().toString() + invite_code_ET5.getText().toString();
         Log.i(APP_TAG, "CODE: "+invite_code);
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
                                Result_TV.setTextColor(Color.parseColor("green"));

                            }
                            else {
                                    Result_TV.setText(RESULT);
                                Result_TV.setTextColor(Color.parseColor("red"));

                            }

                        });
                        if (RESULT.equals("SUCCESS")){
                            goNewView();
                        }
                    } else {
                        Log.e(APP_TAG, "onResponse | status: " + statusCode);
                        runOnUiThread(()-> {
                        Result_TV.setText("ОШИБКА");
                        Result_TV.setTextColor(Color.parseColor("red"));
                            btn_0.setClickable(true);
                            btn_9.setClickable(true);
                            btn_8.setClickable(true);
                            btn_7.setClickable(true);
                            btn_6.setClickable(true);
                            btn_5.setClickable(true);
                            btn_4.setClickable(true);
                            btn_3.setClickable(true);
                            btn_2.setClickable(true);
                            btn_1.setClickable(true);
                            btn_sbmt.setClickable(true);
                            btn_X.setClickable(true);
                        });
                    }
                } catch (Exception e) {
                    Log.e(APP_TAG, "onResponse | exception", e);
                    runOnUiThread(()-> {
                        Result_TV.setText("ОШИБКА");
                        Result_TV.setTextColor(Color.parseColor("red"));
                        btn_0.setClickable(true);
                        btn_9.setClickable(true);
                        btn_8.setClickable(true);
                        btn_7.setClickable(true);
                        btn_6.setClickable(true);
                        btn_5.setClickable(true);
                        btn_4.setClickable(true);
                        btn_3.setClickable(true);
                        btn_2.setClickable(true);
                        btn_1.setClickable(true);
                        btn_sbmt.setClickable(true);
                        btn_X.setClickable(true);
                    });
                }
            }

            @Override
            public void onFailure(Call<GET_CODE> call, Throwable t) {
                runOnUiThread(()->{
                    Result_TV.setText("Network ERROR");
                    Result_TV.setTextColor(Color.parseColor("red"));
                    btn_0.setClickable(true);
                    btn_9.setClickable(true);
                    btn_8.setClickable(true);
                    btn_7.setClickable(true);
                    btn_6.setClickable(true);
                    btn_5.setClickable(true);
                    btn_4.setClickable(true);
                    btn_3.setClickable(true);
                    btn_2.setClickable(true);
                    btn_1.setClickable(true);
                    btn_sbmt.setClickable(true);
                    btn_X.setClickable(true);
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


