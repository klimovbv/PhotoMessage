package com.spb.kbv.messageapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.services.entities.Message;
import com.spb.kbv.messageapp.services.entities.UserDetails;
import com.spb.kbv.messageapp.views.CameraPreview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class NewMessageActivity extends BaseAuthenticatedActivity implements View.OnClickListener, Camera.PictureCallback {
    private static final String TAG = "NewMessageActivity";
    private static final String STATE_CAMERA_INDEX = "STATE_CAMERA_INDEX";
    public static final String EXTRA_CONTACT = "EXTRA_CONTACT";

    private static final int REQUEST_SEND_MESSAGE = 100;

    private Camera camera;
    private Camera.CameraInfo cameraInfo;
    private int currentCameraIndex;
    private CameraPreview cameraPreview;


    @Override
    protected void onMessageAppCreate(Bundle savedState) {
        setContentView(R.layout.activity_new_message);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(savedState != null){
            currentCameraIndex = savedState.getInt(STATE_CAMERA_INDEX);
        } else {
            currentCameraIndex = 0;
        }

        cameraPreview = new CameraPreview(this);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_new_message_frame);
        frameLayout.addView(cameraPreview, 0);

        findViewById(R.id.activity_new_message_takePicture).setOnClickListener(this);
        findViewById(R.id.activity_new_message_switchCamera).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        establishCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null){
            cameraPreview.setCamera(null, null);
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CAMERA_INDEX, currentCameraIndex);
    }

    public void establishCamera() {
        if (camera != null){
            cameraPreview.setCamera(null, null);
            camera.release();
            camera = null;
        }

        try {
            camera = Camera.open(currentCameraIndex);
        } catch (Exception e) {
            Log.e(TAG, "Could not open camera " + currentCameraIndex, e);
            Toast.makeText(this, "Error establishing camera!", Toast.LENGTH_LONG).show();
            return;
        }

        cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(currentCameraIndex, cameraInfo);
        cameraPreview.setCamera(camera, cameraInfo);

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
            Toast.makeText(this, "Using back camera", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Using front camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.activity_new_message_takePicture){
            takePicture();
        } else if (id == R.id.activity_new_message_switchCamera){
            switchCamera();
        }
    }

    private void takePicture() {
        camera.takePicture(null, null, this);

    }

    private void switchCamera() {
        currentCameraIndex = currentCameraIndex + 1 < Camera.getNumberOfCameras() ? currentCameraIndex + 1 : 0;
        Toast.makeText(this, "CURRENT CAMERA IS " + currentCameraIndex, Toast.LENGTH_SHORT).show();
        establishCamera();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Bitmap bitmap = processBitmap(data);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);

        File outputFile = new File(getCacheDir(), "temp-image");
        outputFile.delete();
        try {
            FileOutputStream fileOutput = new FileOutputStream(outputFile);
            fileOutput.write(output.toByteArray());
            fileOutput.close();
        } catch (Exception e) {
            Log.e(TAG, "Could not save bitmap", e);
            Toast.makeText(this, "Could not save image to temp directory", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra(SendMessageActivity.EXTRA_IMAGE_PATH, Uri.fromFile(outputFile));

        UserDetails user = getIntent().getParcelableExtra(EXTRA_CONTACT);
        if (user != null){
            intent.putExtra(SendMessageActivity.EXTRA_CONTACT, user);
        }

        startActivityForResult(intent, REQUEST_SEND_MESSAGE);
        bitmap.recycle();

    }

    private Bitmap processBitmap(byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        if (bitmap.getWidth() > SendMessageActivity.MAX_IMAGE_HEIGHT){
            float scale = (float) SendMessageActivity.MAX_IMAGE_HEIGHT / bitmap.getWidth();
            int finalWidth = (int) (bitmap.getHeight() * scale);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, SendMessageActivity.MAX_IMAGE_HEIGHT, finalWidth, false);

            if (resizedBitmap != bitmap) {
                bitmap.recycle();
                bitmap = resizedBitmap;
            }
        }

        Matrix matrix = new Matrix();
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Matrix matrixMirror = new Matrix();
            matrixMirror.setValues(new float[] {
                    -1, 0, 0,
                    0, 1, 0,
                    0, 0, 1
            });

            matrix.postConcat(matrixMirror);
        }

        matrix.postRotate(90);
        Bitmap processBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        if (bitmap != processBitmap) {
            bitmap.recycle();
        }

        return processBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SEND_MESSAGE && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();

            if (data != null) {
                Message message = data.getParcelableExtra(SendMessageActivity.RESULT_MESSAGE);

                Intent intent = new Intent(this, MessageActivity.class);
                intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        }
    }
}
