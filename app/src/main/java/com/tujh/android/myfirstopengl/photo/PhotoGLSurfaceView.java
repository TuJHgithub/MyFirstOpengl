package com.tujh.android.myfirstopengl.photo;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by tujh on 2017/5/9.
 */

public class PhotoGLSurfaceView extends GLSurfaceView {

    private PhotoGLRenderer photoGLRenderer;

    public PhotoGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        photoGLRenderer = new PhotoGLRenderer(getContext());
        setRenderer(photoGLRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
