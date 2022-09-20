package com.example.prototype_therminal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ErrorPage extends AppCompatActivity {

    private Button btn_next;
    private Button btn_previous;
    private ImageView img_error;
    private int count;
    private String tel;
    private TextView tv_ip;
    private TextView TV_TEl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_page);
        btn_next = findViewById(R.id.button_next);
        btn_previous = findViewById(R.id.button_next2);
        btn_previous.setVisibility(View.INVISIBLE);
        tv_ip = findViewById(R.id.IP_TV);
        img_error = findViewById(R.id.errorimage);
        count=0;
        TV_TEl = findViewById(R.id.TV_TEL);

        TV_TEl.setVisibility(View.GONE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        tel = sp.getString("tel", "+7");
        TV_TEl.setText(tel);

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count=0;
                btn_previous.setVisibility(View.INVISIBLE);
                img_error.setImageDrawable(getResources().getDrawable(R.drawable.first));
                btn_next.setText("-->");
                TV_TEl.setVisibility(View.GONE);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if(count==0) {
                    btn_previous.setVisibility(View.VISIBLE);
                    btn_previous.setText("<--");
                    img_error.setImageDrawable(getResources().getDrawable(R.drawable.error2));
                    btn_next.setText("Домой");
                    count++;
                    TV_TEl.setVisibility(View.VISIBLE);
                }
                else{
                    Intent intent = new Intent(ErrorPage.this, MainActivity.class);
                    // показываем новое Activity
                    startActivity(intent);
                }

                }


        });

    }
    @Override
    public void onBackPressed() {

    }
}