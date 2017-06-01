package com.tujh.android.myfirstopengl.triangle;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by zxcv on 2017/5/8.
 */

public class TriangleGLSurfaceView extends GLSurfaceView {

    private final Renderer myRenderer;

    public TriangleGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        myRenderer = new TriangleGLRenderer();
        setRenderer(myRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}