package com.example.prototype_therminal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextClock;


public class ScreenSaver extends AppCompatActivity {

    TextClock clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_saver);
//        clock.findViewById(R.id.textClock2);
//
//        clock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ScreenSaver.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        Intent intent = new Intent(ScreenSaver.this, MainActivity.class);
        startActivity(intent);
        return false;
    }
}