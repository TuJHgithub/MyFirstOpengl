package com.tujh.android.myfirstopengl.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tujh on 2017/5/9.
 */

public class VideoGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "VideoGLSurfaceView";

    private Camera camera;

    private VideoSquare videoSquare;

    private SurfaceTexture mSurface;
    private int mTextureID = -1;

    public VideoGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");

        GLES20.glClearColor(0f, 0f, 0f, 1f);
        mTextureID = createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        videoSquare = new VideoSquare(mTextureID);

        if (camera == null) {
            camera = Camera.open();
        }
        try {
            camera.setPreviewTexture(mSurface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.setDisplayOrientation(90);
        camera.startPreview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged");

        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d(TAG, "onDrawFrame");

        //把最新一帧的图像流转换成纹理并扔给GL_TEXTURE_EXTERNAL_OES
        mSurface.updateTexImage();

        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);

        //绘制纹理
        videoSquare.draw(mtx);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        //camera产生新一帧图像流的回调方法
        Log.d(TAG, "onFrameAvailable");

        //触发GLSurfaceView.Renderer的重绘方法onDrawFrame
        requestRender();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private int createTextureID() {
        int[] texture = new int[1];

        //创建纹理ID
        GLES20.glGenTextures(1, texture, 0);
        //把创建的纹理ID 与 GL_TEXTURE_EXTERNAL_OES 绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        //过滤模式
        // - GL_NEAREST————最近邻过滤
        // - GL_NEAREST_MIPMAP_NEAREST————使用MIP贴图的最近邻过滤
        // - GL_NEAREST_MIPMAP_LINEAR————使用MIP贴图级别之间插值的最近邻过滤
        // - GL_LINEAR————双线性过滤
        // - GL_LINEAR_MIPMAP_NEAREST————使用MIP贴图的双线性过滤
        // - GL_LINEAR_MIPMAP_LINEAR————三线性过滤（使用MIP贴图级别之间插值的双线性过滤）
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        //超过纹理边界，超出部分的显示模式
        // - GL_REPEAT————重复边界的纹理
        // - GL_CLAMP————opengl就在一个2X2的加权纹理单元数组中使用取自边框的纹理单元。这时候的边框如果没有设置的话，应该就是原纹理的边界的像素值
        // - GL_CLAMP_TO_EDGE————边框始终被忽略。位于纹理边缘或者靠近纹理边缘的纹理单元将用于纹理计算，但不使用纹理边框上的纹理单元
        // - GL_CLAMP_TO_BORDER————如果纹理坐标位于范围[0,1]之外，那么只用边框纹理单元（如果没有边框，则使用常量边框颜色，我想常量边框颜色就是黑色）用于纹理
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }


}
