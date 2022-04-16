package com.example.prototype_therminal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.prototype_therminal.data.POST_API;
import com.example.prototype_therminal.model.POST_PHOTO;

import com.google.gson.GsonBuilder;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Photo extends AppCompatActivity {

    public static final String APP_TAG = "retrofit-json-variable";
    public static final String LOG_TAG = "Camera";
    public static final String FACE_TAG = "FACE_DETECTION";

    private TextureView camera_view = null;
    private Button btn_take_photo;
    private String name;
    private String id;
    private String img64;
    private TextView RESULT_TV;
    private String RESULT_FROM_POST;
    private String MSG_FROM_POST;

    private TextView hint;
    private Button btn_home;

    private CascadeClassifier cascadeClassifier;

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler = null;

    private CameraManager mCameraManager    = null;
    private final int CAMERA1   = 0;
    private final int CAMERA2   = 1;
    CameraService[] myCameras = null;

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
        camera_view = findViewById(R.id.camera_viev);
        btn_take_photo = findViewById(R.id.btn_take_photo);
         name = getIntent().getStringExtra("name");
         id = getIntent().getStringExtra("id");
         RESULT_TV = findViewById(R.id.TV_RESULT);
         hint = findViewById(R.id.hint);



         btn_home = findViewById(R.id.btn_home);
         btn_home.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 goNewView();
             }
         });

         RESULT_TV.setText("Пожалуйста сделайте фото своего лица");




        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try{

            // Получение списка камер с устройства

            myCameras = new CameraService[mCameraManager.getCameraIdList().length];

            for (String cameraID : mCameraManager.getCameraIdList()) {
                Log.i(LOG_TAG, "cameraID: "+cameraID);
                int id = Integer.parseInt(cameraID);

                // создаем обработчик для камеры
                myCameras[id] = new CameraService(mCameraManager,cameraID);
            }
        }
        catch(CameraAccessException e){
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||
                (ContextCompat.checkSelfPermission(Photo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        )
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        myCameras[CAMERA2].openCamera();

        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RESULT_TV.setText("Подождите");
                btn_take_photo.setClickable(false);
                btn_home.setClickable(false);
                if (myCameras[CAMERA2].isOpen()){
                    myCameras[CAMERA2].makePhoto();
                    btn_take_photo.setClickable(false);
                    RESULT_TV.setTextColor(Color.parseColor("gray"));
                }
                else{
                    myCameras[CAMERA2].openCamera();
                    myCameras[CAMERA2].makePhoto();
                    btn_take_photo.setClickable(false);
                    RESULT_TV.setTextColor(Color.parseColor("gray"));
                }




            }
        });

        camera_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                camera_view.setClickable(false);
                Handler hand_timer = new Handler();
                hand_timer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myCameras[CAMERA2].openCamera();
                        camera_view.setClickable(true);
                    }
                }, 3000);
            }
        });
        btn_take_photo.setClickable(false);
        btn_take_photo.setEnabled(false);
        Handler hint_hand = new Handler();
        hint_hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                hint.setVisibility(View.GONE);
                btn_take_photo.setClickable(true);
                btn_take_photo.setEnabled(true);

            }
        },5000);


    }




    public class FaceRtspUtil {

        private static final String TAG = "FaceUtil";
        private Mat grayscaleImage;
        private CascadeClassifier cascadeClassifier = null;

        public FaceRtspUtil(CascadeClassifier cascadeClassifier, int width, int height) {
            this.cascadeClassifier = cascadeClassifier;
            // Ширина и высота лица должны быть не менее 10% от высоты исходного изображения
            grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        }



        public Rect[] detectFrame2(Bitmap oldBitmap) {
            Mat aInputFrame = new Mat();
            if (oldBitmap == null) {
                return null;
            }
            Utils.bitmapToMat(oldBitmap, aInputFrame);
            if (grayscaleImage == null) {
                Log.i(TAG, "detectFrame: aInputFrame == null || grayscaleImage == null");
                return null;
            }
            Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB);

            MatOfRect faces = new MatOfRect();

            // Используем каскадный классификатор для обнаружения объектов
            if (cascadeClassifier != null) {
                // Не получаем объекты ниже 60 * 60
                cascadeClassifier.detectMultiScale(grayscaleImage, faces, 1.1, 2, 2 , new Size(60, 60), new Size());
            }
            // Положение и размеры всех обнаруженных лиц сохраняются в facesArray
            Rect[] facesArray = faces.toArray();
            if (facesArray == null || facesArray.length == 0) {
                // Если лица нет, просто выходим
                Log.i (TAG, "detectFrame: на картинке нет лица");
                return null;
            }else{
                Log.i (TAG, "detectFrame: OK! Лицо обнаружено");
                return facesArray;
            }
        }

    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    initializeOpenCVDependencies();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };


//TODO initialize OpenCVDependencies

    private void initializeOpenCVDependencies() {
        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//           File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            // Load the cascade classifier
            CascadeClassifier cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());

            if (cascadeClassifier.empty()) {
                Log.e("initializeOpenCVDependencies : ", "Failed to load cascade classifier");
                cascadeDir = null;
            } else{
                Log.i("initializeOpenCVDependencies : ", "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
            }

        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }}
//        openCvCameraView.enableView();





        private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //TODO POST_IMG_64
    private void POST_img64(String ID, String img64, String name) {
        GsonBuilder gsonBuilder = new GsonBuilder();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.48.114:8080")
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();

        POST_API post_api = retrofit.create(POST_API.class);
        Call<POST_PHOTO> call = post_api.Post_img64(ID, img64, name);
        Log.e(APP_TAG, "img64:"+img64.charAt(2)+" id= "+ID+" name: "+name);
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
                        Log.w(APP_TAG, "onResponse| response: " + "Result: " + RESULT+" msg: " + msg);

                        RESULT_FROM_POST = POST_PHOTO.getRESULT();
                        MSG_FROM_POST = POST_PHOTO.getMsg();

                        runOnUiThread(() -> {
                            if(!RESULT_FROM_POST.equals("SUCCESS")){
                                RESULT_TV.setTextColor(Color.parseColor("green"));
                                Handler handler_new_view = new Handler();
                                handler_new_view.postDelayed(new Runnable() {
                                    public void run() {
                                        goNewView();
                                    }
                                }, 3000);
                            }
                            else {

                                RESULT_TV.setTextColor(Color.parseColor("red"));
                                btn_take_photo.setClickable(true);
                                btn_home.setClickable(true);
                            }
                            RESULT_TV.setText(MSG_FROM_POST);


                        });

                    } else {
                        Log.e(APP_TAG, "onResponse | status: " + statusCode);
                        runOnUiThread(() -> {
                            RESULT_FROM_POST = "ERROR";
                            MSG_FROM_POST = "ОШИБКА СЕРВЕРА";
                            RESULT_TV.setText(MSG_FROM_POST);
                            RESULT_TV.setTextColor(Color.parseColor("#eb4034"));

                            btn_take_photo.setClickable(true);
                            btn_home.setClickable(true);


                        });
                    }
                } catch (Exception e) {
                    Log.e(APP_TAG, "onResponse | exception", e);
                    runOnUiThread(() -> {
                        RESULT_FROM_POST = "ERROR";
                        MSG_FROM_POST = "ВНУТРЕННЯЯ ОШИБКА";

                        RESULT_TV.setTextColor(Color.parseColor("#eb4034"));
                        RESULT_TV.setText(MSG_FROM_POST);
                        btn_take_photo.setClickable(true);
                        btn_home.setClickable(true);

                    });
                }
            }

            @Override
            public void onFailure(Call<POST_PHOTO> call, Throwable t) {
                Log.e(APP_TAG, "onFailure", t);
                runOnUiThread(() -> {
                    RESULT_FROM_POST = "ERROR";
                    MSG_FROM_POST = "ОШИБКА СЕРВЕРА";

                    RESULT_TV.setTextColor(Color.parseColor("#eb4034"));
                    RESULT_TV.setText(MSG_FROM_POST);
                    btn_take_photo.setClickable(true);
                    btn_home.setClickable(true);

                });
            }
        });
    } //POST

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

    private class ImageSaver implements Runnable {

        private final File mFile;
        private final Image mImage;
        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {

            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
                Bitmap bm = BitmapFactory.decodeFile(mFile.getPath());
                rotateBitmap(bm, mFile.getPath());
                //TODO//////////FACE///////////////
                InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");
                CascadeClassifier cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                FaceRtspUtil mFaceUtili = new FaceRtspUtil(cascadeClassifier, 1920, 1080);
                Rect[] hasFace = mFaceUtili.detectFrame2(bm);
                if(hasFace==null){
                    runOnUiThread(() -> {
                        RESULT_TV.setTextColor(Color.parseColor("#eb4034"));
                        RESULT_TV.setText("На фото нет лица, или попало более одного лица на фото");

                        btn_take_photo.setClickable(true);
                        btn_home.setClickable(true);
                    });


                }
               else {
                   Log.e(APP_TAG,"x:"+hasFace[0].x+" y:"+ hasFace[0].y +" width:"+  hasFace[0].width+" height"+  hasFace[0].height);
                    int height_face_1_3=hasFace[0].height/3;
                    int height_face_1_2=hasFace[0].height/2;
                    int height_face_1_6 = hasFace[0].height / 6;
                    int weight_face_1_6=hasFace[0].width/6;
                    if(hasFace[0].y-height_face_1_3 + hasFace[0].height+height_face_1_2 <=bm.getHeight()){
                        bm = Bitmap.createBitmap(bm, hasFace[0].x-weight_face_1_6,hasFace[0].y-height_face_1_3 , hasFace[0].width+weight_face_1_6, hasFace[0].height+height_face_1_2);
                    }

                    ByteArrayOutputStream bOut = new ByteArrayOutputStream();

                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bOut);

                     File mFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "test2.jpg");
                    FileOutputStream fos = null;
                    fos = new FileOutputStream(mFile2);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                    String base64Image = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);//////////////Вот тут теперь ифыу 64 фотка
                    Log.i(LOG_TAG, "---------Base64________"+base64Image);
                    runOnUiThread(() -> {
                        RESULT_TV.setText("Отправляю фото ");
                        img64 = base64Image;
                        POST_img64(id, img64, name);



                    });
                }


                ////////////////CLOSE FACE////////////////





            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    RESULT_TV.setTextColor(Color.parseColor("#eb4034"));
                    RESULT_TV.setText("ОШИБКА ПРИЛОЖЕНИЯ");
                    btn_take_photo.setClickable(true);
                    btn_home.setClickable(true);
                });
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            RESULT_TV.setTextColor(Color.parseColor("#eb4034"));
                            RESULT_TV.setText("ОШИБКА ПРИЛОЖЕНИЯ");
                            btn_take_photo.setClickable(true);
                            btn_home.setClickable(true);
                        });
                    }
                }
            }
        }
    }

    public class CameraService {
        private String mCameraID;
        private CameraDevice mCameraDevice = null;
        private CameraCaptureSession mCaptureSession;

        public CameraService(CameraManager cameraManager, String cameraID) {

            mCameraManager = cameraManager;
            mCameraID = cameraID;
        }
        private File mFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "test1.jpg");;
        public boolean isOpen() {

            if (mCameraDevice == null) {
                return false;
            } else {
                return true;
            }
        }
        private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {

            @Override
            public void onOpened(CameraDevice camera) {
                mCameraDevice = camera;
                Log.i(LOG_TAG, "Open camera  with id:"+mCameraDevice.getId());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        createCameraPreviewSession();
                    }
                }, 500);


            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                mCameraDevice.close();

                Log.i(LOG_TAG, "disconnect camera  with id:"+mCameraDevice.getId());
                mCameraDevice = null;
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                Log.i(LOG_TAG, "error! camera id:"+camera.getId()+" error:"+error);
            }
        };


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void openCamera() {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        mCameraManager.openCamera(mCameraID,mCameraCallback,null);
                    }
                }
            }
            catch (CameraAccessException e) {
                Log.i(LOG_TAG,e.getMessage());
            }
        }


        public void closeCamera() {

            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
        private ImageReader mImageReader;

        private void createCameraPreviewSession() {

            mImageReader = ImageReader.newInstance(1920,1080, ImageFormat.JPEG,1); //размер фотки
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);
            camera_view = findViewById(R.id.camera_viev);
            SurfaceTexture texture = camera_view.getSurfaceTexture();
            Surface surface = new Surface(texture);

            try {
                final CaptureRequest.Builder builder =
                        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                builder.addTarget(surface);

                mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                        new CameraCaptureSession.StateCallback() {

                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                mCaptureSession = session;
                                try {
                                    mCaptureSession.setRepeatingRequest(builder.build(),null,null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) { }}, null );
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }


        public void makePhoto (){
            try {


                final CaptureRequest.Builder captureBuilder =
                        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(mImageReader.getSurface());
                CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {

                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                                   @NonNull CaptureRequest request,
                                                   @NonNull TotalCaptureResult result) {
                    }
                };

//                mCaptureSession.stopRepeating();
//                mCaptureSession.abortCaptures();
                mCaptureSession.capture(captureBuilder.build(), CaptureCallback, mBackgroundHandler);
            }
            catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }
        private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
                = new ImageReader.OnImageAvailableListener() {

            @Override
            public void onImageAvailable(ImageReader reader) {

                 mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
//                    Log.i(LOG_TAG, "Path"+mFile.getPath());
//                    Toast.makeText(Photo.this,""+mFile.length(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void onPause() {
        super.onPause();
        if(myCameras[CAMERA2].isOpen()){myCameras[CAMERA2].closeCamera();}
        stopBackgroundThread();
    }


    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback);
        startBackgroundThread();
    }


    public void goNewView(){

        // Говорим между какими Activity будет происходить связь
        Intent intent = new Intent(this, MainActivity.class);

        // показываем новое Activity
        startActivity(intent);
    }
}












