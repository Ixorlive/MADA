package com.example.mada_2;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import android.util.Size;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("PhotoDebug", "Start onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.previewView);
        imageCapture = new ImageCapture.Builder().build();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        findViewById(R.id.takePhoto).setOnClickListener(l -> { takePhoto(); });
        try {
            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
            bindImageAnalysis(cameraProvider);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        /*cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindImageAnalysis(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));*/
    }

    private void takePhoto() {
        Log.d("PhotoDebug", "Start takePhoto");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, 0);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Log.d("PhotoDebug", "OutputDir: " + getOutputDir().getAbsolutePath());
                        Log.d("PhotoDebug", "Photo was created");
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        exception.printStackTrace();
                    }
                });
        finishActivity();
    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        /*ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), ImageProxy::close);*/
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector,
                imageCapture, preview);
        //Intent intent = new Intent("com.android.camera.action.CROP");
    }

    private java.io.File getOutputDir() {
        return getFilesDir();
    }

    private void finishActivity() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 1);
        intent.putExtra("meter", "1234567");
        intent.putExtra("id", id);
        setResult(RESULT_OK, intent);
        finish();
    }
}
