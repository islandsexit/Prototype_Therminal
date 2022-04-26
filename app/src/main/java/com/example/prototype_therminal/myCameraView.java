package com.example.prototype_therminal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Rect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

public class myCameraView extends JavaCameraView implements PictureCallback {

    private static final String TAG = "myCameraView";
    private String mPictureFileName;
    private static Rect[] face_array;
    private String img64;
    public Post_class POST = new Post_class();
    private String id;
    private String name;
    public boolean photo_taken = false;


    public void setPhoto_taken(boolean photo_taken) {
        this.photo_taken = photo_taken;
    }

    public myCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImg64(String img64set){
        this.img64 = img64set;
    }

    public String getImg64(){
        return img64;
    }

    public void setFace_array(Rect[] face_arrayset){
        face_array = face_arrayset;
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(final String fileName) {
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }


    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        try {
            File mFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), mPictureFileName);
            FileOutputStream fos = new FileOutputStream(mFile2);
            fos.write(data);
            fos.close();
            Bitmap bm = BitmapFactory.decodeFile(mFile2.getPath());
            bm = getResizedBitmap(bm, 1280,720 );
            Log.i("APP_LOG", "width:"+bm.getWidth()+"\n height:"+bm.getHeight()+" \n x:"+face_array[0].x+" \n "+" y:"+face_array[0].y+"\n width:"+face_array[0].width+"\n height:"+face_array[0].height);
            int height_face_1_3=face_array[0].height/3;
            int height_face_1_2=face_array[0].height/2;
            int height_face_1_6 = face_array[0].height / 6;
            int weight_face_1_6=face_array[0].width/6;
            try{
                if(face_array[0].y-height_face_1_3 + face_array[0].height+height_face_1_3 <=1200){
                    bm = Bitmap.createBitmap(bm, face_array[0].x-weight_face_1_6,face_array[0].y-height_face_1_6 , face_array[0].width+weight_face_1_6, face_array[0].height+height_face_1_6);
                }else{
                    bm = Bitmap.createBitmap(bm,face_array[0].x, face_array[0].y, face_array[0].width, face_array[0].height );
                }
            }catch (Exception e ){
                bm = Bitmap.createBitmap(bm,face_array[0].x, face_array[0].y, face_array[0].width, face_array[0].height );
            }
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            String URL = sp.getString("ippost", "");
            File mFile3 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), UUID.randomUUID().toString()+"_"+".jpg");
            FileOutputStream fos2 = null;
            fos2 = new FileOutputStream(mFile3);
            Log.i("APP_LOG", mFile3.getAbsolutePath());
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bOut);
            img64 = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);
            POST.setRESULT_FROM_POST(null);
            POST.POST_img64(id, img64, name, URL);
            photo_taken = true;
            mFile2.delete();
            mFile3.delete();




        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }




}

