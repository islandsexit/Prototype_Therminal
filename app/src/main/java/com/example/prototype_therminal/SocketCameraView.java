package com.example.prototype_therminal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class SocketCameraView extends myCameraView{

    public Socket clientSocket;

    public SocketCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void takePicture(String fileName) {
        this.mCamera.startPreview();
        super.takePicture(fileName);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        String mPictureFileName = UUID.randomUUID().toString()+"_"+".jpg";
        File mFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), mPictureFileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mFile2);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bm = BitmapFactory.decodeFile(mFile2.getPath());
        bm = getResizedBitmap(bm, 1280,720 );
              ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bOut);
        String img64 = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);
        String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: ";
        String OUTPUT_END_OF_HEADERS = "\r\n\r\n";

        final DataOutputStream[] out = {null};
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        out[0] = new DataOutputStream(clientSocket.getOutputStream());
                        out[0].writeBytes(OUTPUT_HEADERS+ img64.length()+OUTPUT_END_OF_HEADERS);
                        out[0].writeBytes(img64);
                        out[0].close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();


        mFile2.delete();
    }
}
