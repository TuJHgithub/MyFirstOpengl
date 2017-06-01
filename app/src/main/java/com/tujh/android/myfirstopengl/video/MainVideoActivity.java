package com.tujh.android.myfirstopengl.video;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainVideoActivity extends AppCompatActivity {

    private VideoGLSurfaceView videoGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoGLSurfaceView = new VideoGLSurfaceView(this);
        setContentView(videoGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoGLSurfaceView.onPause();
    }
}
