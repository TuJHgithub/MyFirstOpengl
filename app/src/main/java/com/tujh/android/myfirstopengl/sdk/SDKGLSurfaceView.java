package com.tujh.android.myfirstopengl.sdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.faceunity.wrapper.faceunity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tujh on 2017/5/12.
 */

public class SDKGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener, Camera.PreviewCallback {
    private static final String TAG = "SDKGLSurfaceView";

    private Camera camera;
    private int cameraId;
    private VideoRect videoRect;

    private SurfaceTexture mSurface;
    private int mTextureID = -1;

    private int mFacebeautyItem = 0; //美颜道具
    public final static String[] FILTERS_NAME = {"nature", "delta", "electric", "slowlived", "tokyo", "warm"};

    public static final String[] EFFECT_ITEM_FILE_NAME = {"none", "tiara.mp3", "item0208.mp3",
            "YellowEar.mp3", "PrincessCrown.mp3", "Mood.mp3", "Deer.mp3", "BeagleDog.mp3", "item0501.mp3",
            "ColorCrown.mp3", "item0210.mp3", "HappyRabbi.mp3", "item0204.mp3", "hartshorn.mp3"};

    private int mEffectItem = 0; //道具

    private float mFacebeautyColorLevel = 0.5f;
    private float mFacebeautyBlurLevel = 6.0f;
    private float mFacebeautyCheeckThin = 1.0f;
    private float mFacebeautyEnlargeEye = 1.0f;
    private String mFilterName = FILTERS_NAME[0];
    private String mEffectFileName = EFFECT_ITEM_FILE_NAME[0];
    private byte[] itemData;

    private int mFrameId = 0;
    private byte[] mCameraNV21Byte;

    private int cameraWidth = 1280;
    private int cameraHeight = 720;

    private int fuIsTrackingStatus = 0;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    public SDKGLSurfaceView(Context context) {
        this(context, null);
    }

    public SDKGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

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
        videoRect = new VideoRect();

        openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) height * cameraHeight / (width * cameraWidth);
        Matrix.frustumM(mProjectionMatrix, 0, -1f, 1f, -ratio, ratio, 3f, 10f);
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 3.01f, 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

//        videoRect.setTextureVertices((float) height * cameraHeight / (width * cameraWidth));

        int[] temp = new int[1];
//generate fbo id
        GLES20.glGenFramebuffers(1, temp, 0);
        fboId = temp[0];
//generate texture
        GLES20.glGenTextures(1, temp, 0);
        fboTex = temp[0];
//generate render buffer
        GLES20.glGenRenderbuffers(1, temp, 0);
        renderBufferId = temp[0];
//Bind Frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
//Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex);
//Define texture parameters
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferId);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
//Attach texture FBO color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTex, 0);
//Attach render buffer to depth attachment
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBufferId);
//we are done, reset
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }


    private int fboId;
    private int fboTex;
    private int renderBufferId;
    private float[] mtx = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        Log.d(TAG, "onDrawFrame start = " + System.currentTimeMillis());
        mSurface.updateTexImage();
        mSurface.getTransformMatrix(mtx);

        // 人脸识别
        final int fuIsTracking = faceunity.fuIsTracking();
        if (fuIsTracking != fuIsTrackingStatus) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (fuIsTracking == 0) {
                        Toast.makeText(getContext(), "人脸识别失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "人脸识别成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            fuIsTrackingStatus = fuIsTracking;
        }

        if (mEffectItem == 0 && !EFFECT_ITEM_FILE_NAME[0].equals(mEffectFileName)) {
            //加载道具
            try {
                InputStream is = getContext().getAssets().open(mEffectFileName);
                itemData = new byte[is.available()];
                is.read(itemData);
                is.close();
                mEffectItem = faceunity.fuCreateItemFromPackage(itemData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 设置道具
        faceunity.fuItemSetParam(mEffectItem, "isAndroid", 1.0);
        // 设置各种参数
        faceunity.fuItemSetParam(mFacebeautyItem, "color_level", mFacebeautyColorLevel);
        faceunity.fuItemSetParam(mFacebeautyItem, "blur_level", mFacebeautyBlurLevel);
        faceunity.fuItemSetParam(mFacebeautyItem, "filter_name", mFilterName);
        faceunity.fuItemSetParam(mFacebeautyItem, "cheek_thinning", mFacebeautyCheeckThin);
        faceunity.fuItemSetParam(mFacebeautyItem, "eye_enlarging", mFacebeautyEnlargeEye);

        if (mCameraNV21Byte == null || mCameraNV21Byte.length == 0) {
            //faceunity.fuRenderToNV21Image 判空
            Log.e(TAG, "camera nv21 bytes null");
            return;
        }
        // 通过onPreviewFrame回调获取的byte[]类型的图片数据来添加道具与美颜，并返回纹理
//        int fuTex = faceunity.fuRenderToNV21Image(mCameraNV21Byte,
//                cameraWidth, cameraHeight, mFrameId++, new int[]{mEffectItem, mFacebeautyItem});

        // 通过onPreviewFrame回调获取的byte[]类型的图片数据和SurfaceTexture对应的纹理，来添加道具与美颜，并返回纹理
        int fuTex = faceunity.fuDualInputToTexture(mCameraNV21Byte, mTextureID, faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE,
                cameraWidth, cameraHeight, mFrameId++, new int[]{mEffectItem, mFacebeautyItem});

        videoRect.draw(fuTex, mMVPMatrix, mtx);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        videoRect.draw(fboTex, VideoRect.IDENTITY_MATRIX, VideoRect.IDENTITY_MATRIX);

    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "onFrameAvailable");

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d(TAG, "onPreviewFrame = " + System.currentTimeMillis());
        camera.addCallbackBuffer(data);
        mCameraNV21Byte = data;
        requestRender();
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            // 加载核心算法数据
            InputStream isV3 = getContext().getAssets().open("v3.mp3");
            byte[] v3data = new byte[isV3.available()];
            isV3.read(v3data);
            isV3.close();
            faceunity.fuSetup(v3data, null, authpack.A());

            //加载美颜算法数据
            InputStream isFace = getContext().getAssets().open("face_beautification.mp3");
            byte[] item_data = new byte[isFace.available()];
            isFace.read(item_data);
            isFace.close();
            mFacebeautyItem = faceunity.fuCreateItemFromPackage(item_data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        releaseCamera();
        mFrameId = 0;

        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuDestroyItem(mEffectItem);
                mEffectItem = 0;
                faceunity.fuDestroyItem(mFacebeautyItem);
                mFacebeautyItem = 0;
                faceunity.fuOnDeviceLost();
            }
        });
    }

    private void openCamera(int cameraFaceType) {
        if (camera == null) {
            int num = Camera.getNumberOfCameras();
            Camera.CameraInfo info = new Camera.CameraInfo();
            for (int i = 0; i < num; i++) {
                Camera.getCameraInfo(i, info);
                if (cameraFaceType == info.facing) {
                    camera = Camera.open(i);
                    cameraId = i;
                    //屏幕方向
                    int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
                    int degrees = 0;
                    switch (rotation) {
                        case Surface.ROTATION_0:
                            degrees = 0;
                            break;
                        case Surface.ROTATION_90:
                            degrees = 90;
                            break;
                        case Surface.ROTATION_180:
                            degrees = 180;
                            break;
                        case Surface.ROTATION_270:
                            degrees = 270;
                            break;
                    }

                    int result;
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        result = (info.orientation + degrees) % 360;
                        result = (360 - result) % 360;  // compensate the mirror
                    } else {  // back-facing
                        result = (info.orientation - degrees + 360) % 360;
                    }
                    Log.e(TAG, "degrees = " + degrees + " ; info.orientation = " + info.orientation);
                    camera.setDisplayOrientation(result);
                    break;
                }
            }
        }
        camera.setPreviewCallbackWithBuffer(this);
        try {
            camera.setPreviewTexture(mSurface);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Camera.Parameters parameters = camera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width == cameraWidth && size.height == cameraHeight) {
                Log.d(TAG, "size.width = " + size.width + " ; size.height = " + size.height);
                parameters.setPreviewSize(cameraWidth, cameraHeight);
                break;
            }
        }
        Camera.Size size = parameters.getPreviewSize();
        if (size.width != cameraWidth || size.height != cameraHeight) {
            Camera.Size ppsfv = parameters.getPreferredPreviewSizeForVideo();
            cameraWidth = ppsfv.width;
            cameraHeight = ppsfv.height;
            parameters.setPreviewSize(cameraWidth, cameraHeight);
        }
        camera.setParameters(parameters);

        camera.addCallbackBuffer(new byte[((cameraWidth * cameraHeight) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8]);

        camera.startPreview();
    }

    private void releaseCamera() {
        if (null != camera) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    private int createTextureID() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

    public void changeCamera() {
        if (camera == null) {
            return;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            releaseCamera();
            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            releaseCamera();
            openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
    }

    public void setmFacebeautyColorLevel(float mFacebeautyColorLevel) {
        this.mFacebeautyColorLevel = mFacebeautyColorLevel;
    }

    public void setmFacebeautyBlurLevel(float mFacebeautyBlurLevel) {
        this.mFacebeautyBlurLevel = mFacebeautyBlurLevel;
    }

    public void setmFacebeautyCheeckThin(float mFacebeautyCheeckThin) {
        this.mFacebeautyCheeckThin = mFacebeautyCheeckThin;
    }

    public void setmFacebeautyEnlargeEye(float mFacebeautyEnlargeEye) {
        this.mFacebeautyEnlargeEye = mFacebeautyEnlargeEye;
    }

    public void setmFilterName(int mFilterName) {
        this.mFilterName = FILTERS_NAME[mFilterName];
    }

    public void setmEffectFileName(int mEffectFileName) {
        if (mEffectItem != 0) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuDestroyItem(mEffectItem);
                }
            });
        }
        this.mEffectFileName = EFFECT_ITEM_FILE_NAME[mEffectFileName];
        mEffectItem = 0;
    }
}
