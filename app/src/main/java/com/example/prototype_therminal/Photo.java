package com.example.prototype_therminal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class Photo extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    public static final String APP_TAG = "retrofit-json-variable";
    public static final String LOG_TAG = "Camera";
    public static final String FACE_TAG = "FACE_DETECTION";

    private TextureView camera_view = null;

    private String name;
    private String id;
    private String img64;
    private TextView RESULT_TV;
    private String RESULT_FROM_POST;
    private String MSG_FROM_POST;

    private TextView hint;
    private Button btn_home;

    myCameraView                   cameraBridgeViewBase;
    BaseLoaderCallback             baseLoaderCallback;

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    private Mat mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;
    private float                  mRelativeFaceSize   = 0.5f;
    private int                    mAbsoluteFaceSize   = 450;
    private int                    count = 0;

    private static final long      TIMER_DURATION = 2000L;
    private static final long      TIMER_INTERVAL = 100L;

    private CountDownTimer mCountDownTimer;
    private CountDownTimer         mCountDownTimer2;
    private boolean         can_take_photo;

    private boolean count_for_proccessing = false;

    private ImageView img_face;

    private ImageView img_done;
    private Button btn_error;

    private int count_for_error = 0;





    // To keep track of activity's window focus
    boolean currentFocus;

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
                            collapseStatusBar = statusBarManager .getMethod("collapsePanels");
                        } else {
                            collapseStatusBar = statusBarManager .getMethod("collapse");
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        btn_error = findViewById(R.id.btn_error);



         name = getIntent().getStringExtra("name");
         id = getIntent().getStringExtra("id");
         RESULT_TV = findViewById(R.id.TV_RESULT);
         hint = findViewById(R.id.hint);

         img_face = findViewById(R.id.appCompatImageView);
         img_done = findViewById(R.id.Done);
//        ImageView imageView = (ImageView) findViewById(R.id.load);
//        Glide.with(this).asGif().load(R.drawable.loading).into(imageView);




         btn_home = findViewById(R.id.btn_home);
         btn_home.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 goNewView();
             }
         });

         btn_error.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(Photo.this, ErrorPage.class);

                 // показываем новое Activity
                 startActivity(intent);
             }
         });

         RESULT_TV.setText("Пожалуйста стойте по границам");










        Handler hint_hand = new Handler();
        hint_hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                hint.setVisibility(View.GONE);

            }
        },5000);

        can_take_photo = true;
        mCountDownTimer = new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                can_take_photo=false;
            }

            @Override
            public void onFinish() {
                count=0;
                can_take_photo=true;

            }
        }.start();
        mCountDownTimer2 = new CountDownTimer(5000L, TIMER_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                count=0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RESULT_TV.setText("Встаньте ровно по рисунку");
                    }
                });

            }
        }.start();


        cameraBridgeViewBase = (myCameraView) findViewById(R.id.myCameraView);
//        cameraBridgeViewBase.setVisibility(View.VISIBLE);
        cameraBridgeViewBase.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);


        cameraBridgeViewBase.setCvCameraViewListener( Photo.this);


        baseLoaderCallback = new BaseLoaderCallback(Photo.this) {

            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch (status) {
                    case BaseLoaderCallback.SUCCESS: {
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
                        cameraBridgeViewBase.setMaxFrameSize(3000, 2000);
                        cameraBridgeViewBase.setMinimumHeight(1600);
                        cameraBridgeViewBase.setMinimumWidth(1200);
                        cameraBridgeViewBase.enableView();
                    }
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };




    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        try{
            if(cameraBridgeViewBase.POST.RESULT_FROM_POST !=null && cameraBridgeViewBase.photo_taken){

                if (cameraBridgeViewBase.POST.RESULT_FROM_POST.equals("ERROR")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(count_for_error<=5) {
                                RESULT_TV.setText("Повторное сканирование");
                                cameraBridgeViewBase.POST.setRESULT_FROM_POST(null);
                                ////////TODO вот тут берутся значения message from post
                                cameraBridgeViewBase.setPhoto_taken(false);
                                count_for_error++;
                            }
                            else{
                                RESULT_TV.setText((cameraBridgeViewBase.POST.RESULT_FROM_POST));
                                cameraBridgeViewBase.POST.setRESULT_FROM_POST(null);
                                cameraBridgeViewBase.disableView();
                                cameraBridgeViewBase.setVisibility(View.INVISIBLE);
                                img_done.setImageResource(R.drawable.error);
                                img_done.setVisibility(View.VISIBLE);

                                img_face.setVisibility(View.GONE);
                                RESULT_TV.setText("Невозможно  завершить сканирование \n Обратитесь к администратору");
                                count_for_error=0;
                                btn_error.setVisibility(View.VISIBLE);

                            }

                        }
                    });

                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    RESULT_TV.setText((cameraBridgeViewBase.POST.RESULT_FROM_POST));
                    cameraBridgeViewBase.POST.setRESULT_FROM_POST(null);
                    cameraBridgeViewBase.disableView();
                    cameraBridgeViewBase.setVisibility(View.INVISIBLE);
                    img_done.setVisibility(View.VISIBLE);

                    img_face.setVisibility(View.GONE);
                    mCountDownTimer.cancel();
                    mCountDownTimer2.cancel();
                    RESULT_TV.setText("Сканирование завершено.\n Можете проходить через турникеты по лицу");
                    Handler new_view = new Handler();
                    new_view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goNewView();
                        }
                    }, 10000);



                        }
                    });


                }
            }

        }catch (Exception e){
            Log.i(APP_TAG, "waiting for response");
        }

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
        } else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {


            Log.i("MainActivity.this", "y:"+facesArray[0].y);
//            Log.i("MainActivity.this", "w:"+facesArray[0].width);
//            Log.i("MainActivity.this", "h:"+facesArray[0].height);
            Log.i("MainActivity.this", "x:"+facesArray[0].x);
            Imgproc.circle(mRgba, new Point(640,360), 350, new Scalar(255,0,94,0), 20);
            if(900>facesArray[i].x && facesArray[i].x>300 && facesArray[i].x+facesArray[i].width <950&& 150>facesArray[i].y && facesArray[i].y>100){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RESULT_TV.setText("Выполняется сканирование");
                    }
                });
                Imgproc.circle(mRgba, new Point(640,360), 350, new Scalar(0,255,94,0), 20);
            Log.e(TAG, "face detected!");
            count++;
            if (can_take_photo) {
                switch (count) {
                    case 30:
                        Log.i(TAG, "toast wait---------------------------------------------------------------------------------------------------");

                        //RESULT_TV.setText("Подождите");
                        mCountDownTimer2.cancel();
                        mCountDownTimer2.start();
                        if (count_for_proccessing) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RESULT_TV.setText("Встаньте ровно по рисунку");
                                    count_for_proccessing = false;
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RESULT_TV.setText("Выполняется сканирование");
                                    count_for_proccessing = true;
                                }
                            });
                        }
                        count++;

                        break;


                    case 100:

                        cameraBridgeViewBase.setFace_array(facesArray);
                        String uuid = UUID.randomUUID().toString() + ".png";
                        cameraBridgeViewBase.setId(id);
                        cameraBridgeViewBase.setName(name); ///Теперь я передаю в camerabridgebase id & name, тюк внутри этого класса отправляю пост запрос
                        if (!cameraBridgeViewBase.photo_taken) {
                            cameraBridgeViewBase.takePicture(uuid);
                        }
                        mCountDownTimer.cancel();
                        mCountDownTimer2.cancel();
                        ///////disable view
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RESULT_TV.setText(cameraBridgeViewBase.POST.RESULT_FROM_POST);
                            }
                        });


                        count = 0;

                        //RESULT_TV.setText("Фото сделано");
                        break;
                    default:
                }
            }
//                if (count == 10) {
//
//                    cameraBridgeViewBase.setFace_array(facesArray);
//                    String uuid = UUID.randomUUID().toString() + ".png";
//                    cameraBridgeViewBase.takePicture(uuid);
//                    mCountDownTimer.start();
//                    count=0;
//
//
//
//                }

            }

        }












//            cameraBridgeViewBase.takePicture("test1.jpg");
        //TODO face detected
        //Intent intent = new Intent(ScreenSaver.this, MainActivity.class);
        // startActivity(intent);


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
        }
    }
    public static Bitmap rotateBitmap(Bitmap srcBitmap, String path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(0));
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                break;
        }
        Bitmap destBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                srcBitmap.getHeight(), matrix, true);
        return destBitmap;
    }
























    public void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();

    }


    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }

    }


    public void goNewView(){

        // Говорим между какими Activity будет происходить связь
        Intent intent = new Intent(this, MainActivity.class);

        // показываем новое Activity
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {

    }
}












