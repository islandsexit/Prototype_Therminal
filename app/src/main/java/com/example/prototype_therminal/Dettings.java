package com.example.prototype_therminal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Dettings extends AppCompatActivity {

    private EditText ET_WHERE;
    private EditText ET_IPGET;
    private EditText ET_IPPOST;
    private EditText ET_TEL;

    private Button BTN_SAVE;
    private Button BTN_HOME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettings);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        BTN_SAVE = findViewById(R.id.BTN_SAVE);
        ET_WHERE = findViewById(R.id.ET_WHERE);
        ET_IPGET = findViewById(R.id.ET_IPGET);
        ET_IPPOST = findViewById(R.id.ET_IPPOST);
        BTN_HOME = findViewById(R.id.BTN_HOME);
        ET_TEL = findViewById(R.id.telephone);

        String where = sp.getString("where", "Подъезд номер ");
        String ipget = sp.getString("ipget", "http://192.168.48.131:8000/");
        String ippost = sp.getString("ippost", "http://192.168.48.144:8080");
        String tel = sp.getString("tel", "+7");

        ET_IPPOST.setText( ippost, TextView.BufferType.EDITABLE);
        ET_WHERE.setText( where, TextView.BufferType.EDITABLE);
        ET_IPGET.setText( ipget, TextView.BufferType.EDITABLE);
        ET_TEL.setText( tel, TextView.BufferType.EDITABLE);

        BTN_SAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor e = sp.edit();
                e.putString("where", ET_WHERE.getText().toString());
                e.putString("ipget", ET_IPGET.getText().toString());
                e.putString("ippost", ET_IPPOST.getText().toString());
                e.putString("tel", ET_TEL.getText().toString());
                e.commit();
            }
        });

        BTN_HOME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dettings.this, MainActivity.class);


                // показываем новое Activity
                startActivity(intent);
            }
        });







    }
}