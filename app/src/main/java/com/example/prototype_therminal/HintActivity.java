package com.example.prototype_therminal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class HintActivity extends AppCompatActivity {

    private Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        btn_next = findViewById(R.id.button_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(HintActivity.this, MainActivity.class);
                    // показываем новое Activity
                    startActivity(intent);
                }



        });
    }
    @Override
    public void onBackPressed() {

    }
}