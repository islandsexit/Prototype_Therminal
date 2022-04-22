package com.example.prototype_therminal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ErrorPage extends AppCompatActivity {

    private Button btn_next;
    private Button btn_previous;
    private ImageView img_error;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_page);
        btn_next = findViewById(R.id.button_next);
        btn_previous = findViewById(R.id.button_next2);
        btn_previous.setVisibility(View.INVISIBLE);
        img_error = findViewById(R.id.errorimage);
        count=0;

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count=0;
                btn_previous.setVisibility(View.INVISIBLE);
                img_error.setImageDrawable(getResources().getDrawable(R.drawable.first));
                btn_next.setText("-->");
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