package com.tujh.android.myfirstopengl.photo;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainPhotoActivity extends AppCompatActivity {

    private GLSurfaceView photoGlSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoGlSurfaceView = new PhotoGLSurfaceView(this);
        setContentView(photoGlSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        photoGlSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        photoGlSurfaceView.onPause();
    }
}
