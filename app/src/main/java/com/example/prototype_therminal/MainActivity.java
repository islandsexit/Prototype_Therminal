package com.example.prototype_therminal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototype_therminal.data.GET_API;
import com.example.prototype_therminal.model.GET_CODE;
import com.example.prototype_therminal.serversocket.ServerThread;
import com.google.gson.GsonBuilder;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    public static final Boolean debag = false;

    public static final String APP_TAG = "retrofit-json-variable";
    private static String BASE_URL = "";



    SocketCameraView cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    private Mat mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;
    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;


    private EditText invite_code_ET;
    private EditText invite_code_ET1;
    private EditText invite_code_ET2;
    private EditText invite_code_ET3;
    private EditText invite_code_ET4;
    private EditText invite_code_ET5;

    private static final long TIMER_DURATION = 60000L;
    private static final long TIMER_INTERVAL = 1000L;

    private CountDownTimer mCountDownTimer;

    private ConstraintLayout SETTINGS;
    int COUNT_FOR_SETTINGS = 0;
    private SharedPreferences sp;

    private ImageButton btn_hint_icon;
    private ConstraintLayout hint_view;


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

    private ImageButton btn_X;

    private Button btn_sbmt;

    private String invite_code;
    private String name_txt;
    private String id_txt;
    private TextView Result_TV;

    // To keep track of activity's window focus
    boolean currentFocus;
    private Button btn_hint;

    private ServerSocket serverSocket;

    Handler updateConversationHandler;

    Thread serverThread = null;


    // To keep track of activity's foreground/background status
    boolean isPaused;

    Handler collapseNotificationHandler;

    public void collapseNow() {

        // Initialize 'collapseNotificationHandler'
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }

        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity
        if (!currentFocus && !isPaused) {

            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    // Use reflection to trigger a method from 'StatusBarManager'

                    @SuppressLint("WrongConstant")
                    Object statusBarService = getSystemService("statusbar");
                    Class<?> statusBarManager = null;

                    try {
                        statusBarManager = Class.forName("android.app.StatusBarManager");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    Method collapseStatusBar = null;

                    try {

                        // Prior to API 17, the method to call is 'collapse()'
                        // API 17 onwards, the method to call is `collapsePanels()`

                        if (Build.VERSION.SDK_INT > 16) {
                            collapseStatusBar = statusBarManager.getMethod("collapsePanels");
                        } else {
                            collapseStatusBar = statusBarManager.getMethod("collapse");
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    collapseStatusBar.setAccessible(true);

                    try {
                        collapseStatusBar.invoke(statusBarService);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    // Check if the window focus has been returned
                    // If it hasn't been returned, post this Runnable again
                    // Currently, the delay is 100 ms. You can change this
                    // value to suit your needs.
                    if (!currentFocus && !isPaused) {
                        collapseNotificationHandler.postDelayed(this, 10L);
                    }

                }
            }, 300L);
        }
    }




    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        currentFocus = hasFocus;

        if (!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            // Method that handles loss of window focus
            collapseNow();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        screensaver();
        hint_view.setVisibility(View.GONE);
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 2);
        }
        SETTINGS = findViewById(R.id.SETTINGS);
        btn_hint_icon = findViewById(R.id.hint_icon);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        BASE_URL = sp.getString("ipget", "");
        hint_view = findViewById(R.id.hint_view);
        hint_view.setVisibility(View.GONE);


        updateConversationHandler = new Handler();

        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();


        mCountDownTimer = new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                 // Saving timeRemaining in Activity for pause/resume of CountDownTimer.
            }

            @Override
            public void onFinish() {
                COUNT_FOR_SETTINGS=0;
                Intent intent = new Intent(MainActivity.this, ScreenSaver.class);
                startActivity(intent);
            }
        }.start();

        cameraBridgeViewBase = (SocketCameraView) findViewById(R.id.javaCameraView);
//        cameraBridgeViewBase.setVisibility(View.VISIBLE);
        cameraBridgeViewBase.setCameraIndex(1);
        cameraBridgeViewBase.setCvCameraViewListener(MainActivity.this);

        baseLoaderCallback = new BaseLoaderCallback(this) {

            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch(status){
                    case BaseLoaderCallback.SUCCESS:
                    {
                        Log.i(TAG, "OpenCV loaded successfully");

                        // Load native library after(!) OpenCV initialization
//                        System.loadLibrary("ndklibrarysample");

                        try {
                            // load cascade file from application resources
                            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                            mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");
                            FileOutputStream os = new FileOutputStream(mCascadeFile);

                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                            is.close();
                            os.close();

                            mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                            if (mJavaDetector.empty()) {
                                Log.e(TAG, "Failed to load cascade classifier");
                                mJavaDetector = null;
                            } else
                                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());



                            cascadeDir.delete();

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                        }

                        cameraBridgeViewBase.enableView();
                    }
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };




        ///getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);




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
        btn_X = findViewById(R.id.btn_X);

        btn_hint = findViewById(R.id.hint);
        btn_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hint_view.setVisibility(View.VISIBLE);
            }
        });

        btn_able(true);



        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                input_num(btn_1);
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                input_num(btn_2);
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                input_num(btn_3);
            }
        });
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                input_num(btn_4);
            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                input_num(btn_5);
            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                input_num(btn_6);
            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                input_num(btn_7);
            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                input_num(btn_8);
            }
        });
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                input_num(btn_9);
            }
        });
        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                input_num(btn_0);
            }
        });



        btn_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                if(!invite_code_ET4.getText().toString().equals("")){
                    invite_code_ET4.setText("");
                }else if(!invite_code_ET3.getText().toString().equals("")){
                    invite_code_ET3.setText("");
                }else if(!invite_code_ET2.getText().toString().equals("")){
                invite_code_ET2.setText("");
            }else if(!invite_code_ET1.getText().toString().equals("")){
                invite_code_ET1.setText("");
            }else{invite_code_ET.setText("");}}
        });

        btn_hint_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hint_view.setVisibility(View.VISIBLE);
            }
        });




        Result_TV = findViewById(R.id.RESULT_TV);


        btn_sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screensaver();
                get_code();
                if (invite_code.length()!=6){
                    change_text(Result_TV, "red", "Код неполный");
                    code_del();
                }else{
                    change_text(Result_TV, "black", "Проверяем ваш код....");
                    loadProfile(invite_code);
                    code_del();
                    btn_able(false);
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
                        change_text(Result_TV, "red", "Код неполный");
                        code_del();
                    }else{
                        change_text(Result_TV, "black", "Проверяем ваш код...");
                        loadProfile(invite_code);
                        code_del();
                        btn_able(false);



                    }
                }
                else if (invite_code_ET5.length() > 0){
                    invite_code_ET5.setText(invite_code_ET5.getText().toString().charAt(0));
                }
                return false;
            }
        });

        SETTINGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                COUNT_FOR_SETTINGS++;
                if(COUNT_FOR_SETTINGS==10 && invite_code_ET.getText().toString().equals("5") && invite_code_ET1.getText().toString().equals("6") && invite_code_ET2.getText().toString().equals("8")){
                    COUNT_FOR_SETTINGS=0;
                    Intent intent = new Intent(MainActivity.this, Dettings.class);

                    // показываем новое Activity
                    startActivity(intent);
                }


            }
        });

    }

    private void get_code(){
         invite_code = invite_code_ET.getText().toString() + invite_code_ET1.getText().toString() + invite_code_ET2.getText().toString() + invite_code_ET3.getText().toString() + invite_code_ET4.getText().toString() + invite_code_ET5.getText().toString();
         Log.i(APP_TAG, "CODE: "+invite_code);
        COUNT_FOR_SETTINGS=0;
        if(debag==true){
            name_txt = "Test";
            id_txt = "81";
            goNewView();
        }
    }


    private void loadProfile(String invite_code) {
        change_text(Result_TV, "green", "Спасибо");







        GsonBuilder gsonBuilder = new GsonBuilder();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();

        GET_API GETApi = retrofit.create(GET_API.class);
        Call<GET_CODE> call = GETApi.Check_code(invite_code);
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
                                change_text(Result_TV, "green", "Спасибо");
                                goNewView();

                            }
                            else {

                                change_text(Result_TV, "red", "Неправильный код");
                                btn_able(true);
                                Handler hand = new Handler();
                                hand.postDelayed(new Runnable() {
                                    public void run() {
                                        change_text(Result_TV, "black", "Оформление пропуска");
                                    }
                                }, 2000);

                            }

                        });
                    } else {
                        Log.e(APP_TAG, "onResponse | status: " + statusCode);
                        runOnUiThread(()-> {
                            change_text(Result_TV, "red", "Ошибка");
                            btn_able(true);
                            Handler hand = new Handler();
                            hand.postDelayed(new Runnable() {
                                public void run() {
                                    change_text(Result_TV, "black", "Оформление пропуска");
                                }
                            }, 2000);
                        });
                    }
                } catch (Exception e) {
                    Log.e(APP_TAG, "onResponse | exception", e);
                    runOnUiThread(()-> {
                        Result_TV.setText("ОШИБКА");
                        Result_TV.setTextColor(Color.parseColor("red"));
                        btn_able(true);
                        Handler hand = new Handler();
                        hand.postDelayed(new Runnable() {
                            public void run() {
                                change_text(Result_TV, "black", "Оформление пропуска");
                            }
                        }, 2000);
                    });
                }
            }

            @Override
            public void onFailure(Call<GET_CODE> call, Throwable t) {
                runOnUiThread(()->{
                    Result_TV.setText("Сервер недоступен");
                    Result_TV.setTextColor(Color.parseColor("red"));
                    btn_able(true);
                    Handler hand = new Handler();
                    hand.postDelayed(new Runnable() {
                        public void run() {
                            change_text(Result_TV, "black", "Оформление пропуска");

                        }
                    }, 2000);
                });
                Log.e(APP_TAG, "onFailure", t);
            }
        });
    } //GET


    public void goNewView(){

                // Говорим между какими Activity будет происходить связь
                Intent intent = new Intent(this, Photo.class);//todo go new view

                // указываем первым параметром ключ, а второе значение
                // по ключу мы будем получать значение с Intent
                intent.putExtra("name", name_txt);
                intent.putExtra("id", id_txt);

                // показываем новое Activity
                startActivity(intent);
        }



        public void check_nums(){
        if (!invite_code_ET.getText().toString().equals("") &&
                !invite_code_ET1.getText().toString().equals("") &&
                !invite_code_ET2.getText().toString().equals("") &&
                !invite_code_ET3.getText().toString().equals("") &&
                !invite_code_ET4.getText().toString().equals("") &&
                !invite_code_ET5.getText().toString().equals("")){
            change_text(Result_TV, "black", "Проверяем ваш код...");
            btn_able(false);

            get_code();
            code_del();
            loadProfile(invite_code);
        }
        }



    @Override
    public void onBackPressed() {

    }
    public void screensaver(){
        try{
            mCountDownTimer.cancel();
        }
        catch(Exception e){
            Log.e("APP_LOG", e.toString());
        }
        try{
            mCountDownTimer.start();
        }
        catch(Exception e){
            Log.e("APP_LOG", e.toString());
        }



    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        mCountDownTimer.cancel();
        mCountDownTimer = null;
        if(cameraBridgeViewBase!=null){

            cameraBridgeViewBase.disableView();
        }
        try {
            serverSocket.close();
            this.serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
//            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (true) {
            if (true)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }


        else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
            Log.e(TAG, "face detected!");
            //TODO face detected
            //Intent intent = new Intent(ScreenSaver.this, MainActivity.class);
           // startActivity(intent);
            screensaver();
        }
        return mRgba;
    }

    //todo onCameraViewStarted
    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    //todo on cameraviewstop
    @Override
    public void onCameraViewStopped(){
        mGray.release();
        mRgba.release();
    }

    //todo onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
            mCountDownTimer.cancel();
        }
    }

    private void btn_able(Boolean isEnabled){
        if(!isEnabled) {
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
        else{
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
        }
    }


    private void code_del() {
        invite_code_ET.setText("");
        invite_code_ET1.setText("");
        invite_code_ET2.setText("");
        invite_code_ET3.setText("");
        invite_code_ET4.setText("");
        invite_code_ET5.setText("");
    }

    private void input_num(Button btn){
        if(invite_code_ET.getText().toString().trim().matches("")){
            invite_code_ET.setText(btn.getText().toString());
            check_nums();

        }
        else if(invite_code_ET1.getText().toString().trim().matches("")){
            invite_code_ET1.setText(btn.getText().toString());
            check_nums();
        }
        else if(invite_code_ET2.getText().toString().trim().matches("")){
            invite_code_ET2.setText(btn.getText().toString());check_nums();

        }
        else if(invite_code_ET3.getText().toString().trim().matches("")){
            invite_code_ET3.setText(btn.getText().toString());
            check_nums();
        }
        else if(invite_code_ET4.getText().toString().trim().matches("")){
            invite_code_ET4.setText(btn.getText().toString());
            check_nums();
        }
        else if(invite_code_ET5.getText().toString().trim().matches("")){
            invite_code_ET5.setText(btn.getText().toString());
            check_nums();
        }
    }

    private void change_text(TextView textview, String color, String text){
        textview.setTextColor(Color.parseColor(color));
        textview.setText(text);
    }


    class ServerThread implements Runnable {
        public static final int SERVERPORT = 1234;
        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {

                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;
        public ArrayList<String> list = new ArrayList<String>();
        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;
            cameraBridgeViewBase.clientSocket = clientSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            list.clear();
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    String read = input.readLine();
                    list.add(read);
                    if(read.length() == 0){
                        break;
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cameraBridgeViewBase.takePicture(UUID.randomUUID().toString() + ".png");

                    }
                });
        }

    }

//    class updateUIThread implements Runnable {
//        private String msg;
//
//        public updateUIThread(String str) {
//            this.msg = str;
//        }
//
//        @Override
//        public void run() {
//            runOnUiThread(new updateUIThread());
//            // Говорим между какими Activity будет происходить связь
//
//        }
//    }


    }


