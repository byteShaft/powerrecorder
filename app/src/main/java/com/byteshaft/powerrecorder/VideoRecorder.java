package com.byteshaft.powerrecorder;


import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.byteshaft.ezflashlight.CameraStateChangeListener;
import com.byteshaft.ezflashlight.Flashlight;

import java.io.IOException;


public class VideoRecorder extends MediaRecorder implements CameraStateChangeListener {

    private Flashlight flashlight;
    private int mRecordTime;
    private Helpers mHelpers;
    private String mPath;
    private static boolean sIsRecording;
    public static boolean isRecording() {
        return sIsRecording;
    }

    void start(android.hardware.Camera camera, SurfaceHolder holder, int time) {
        mHelpers = new Helpers();
        Camera.Parameters parameters = camera.getParameters();
        Helpers.setOrientation(parameters);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
        camera.unlock();
        setCamera(camera);
        setAudioSource(MediaRecorder.AudioSource.MIC);
        setVideoSource(MediaRecorder.VideoSource.CAMERA);
        setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        setVideoEncodingBitRate(Helpers.getBitRateForResolution(
                AppConstants.VIDEO_WIDTH, AppConstants.VIDEO_HEIGHT));
        setOrientation();
        setVideoSize(AppConstants.VIDEO_WIDTH, AppConstants.VIDEO_HEIGHT);
        setPreviewDisplay(holder.getSurface());
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.mp4";
        setOutputFile(mPath);
        try {
            prepare();
            start();
            AppGlobals.videoRecordingInProgress(true);
        } catch (IOException e) {
            e.printStackTrace();
            AppGlobals.videoRecordingInProgress(false);
            return;
        }

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRecording()) {
                    stopRecording();
                }
            }
        }, time);
    }

    public void start(int time) {
        mRecordTime = time;
        flashlight = new Flashlight(AppGlobals.getContext());
        flashlight.setCameraStateChangedListener(this);
        flashlight.setupCameraPreview();
        sIsRecording = true;
    }

    public void stopRecording() {
        System.out.println("Recording Stopped...");
        stop();
        reset();
        release();
        flashlight.releaseAllResources();
        sIsRecording = false;
    }

    private void setOrientation() {
        Display display = ((WindowManager) AppGlobals.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                Log.i("SPY", "0");
                setOrientationHint(90);
                break;
            case Surface.ROTATION_90:
                Log.i("SPY", "90");
                break;
            case Surface.ROTATION_180:
                Log.i("SPY", "180");
                break;
            case Surface.ROTATION_270:
                Log.i("SPY", "270");
                setOrientationHint(180);
        }
    }

    @Override
    public void onCameraInitialized() {

    }

    @Override
    public void onCameraViewSetup(Camera camera, SurfaceHolder surfaceHolder) {
        System.out.print("Recording Started...");
        start(camera, surfaceHolder, mRecordTime);
    }

    @Override
    public void onCameraBusy() {
        System.out.print("Camera Busy..");

    }
}
