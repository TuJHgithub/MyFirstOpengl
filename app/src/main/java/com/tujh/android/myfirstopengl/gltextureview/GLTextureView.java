package com.tujh.android.myfirstopengl.gltextureview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by tujh on 2017/12/19.
 */

public class GLTextureView extends TextureView implements TextureView.SurfaceTextureListener {


    public GLTextureView(Context context) {
        this(context, null);
    }

    public GLTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GLTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
