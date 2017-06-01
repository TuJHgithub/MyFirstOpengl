package com.tujh.android.myfirstopengl.triangle;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zxcv on 2017/5/8.
 */

public class TriangleGLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "TriangleGLRenderer";

    private Triangle triangle;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");

        GLES20.glClearColor(0f, 0f, 1f, 1f);

        triangle = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged");

        GLES20.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d(TAG, "onDrawFrame");

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        triangle.draw();
    }

    public static int loadShader(int type, String shaderCode) {

        // 创建着色器
        //  vertex shader type (GLES20.GL_VERTEX_SHADER)
        //  fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // 添加着色器代码
        GLES20.glShaderSource(shader, shaderCode);
        // 编译着色器代码
        GLES20.glCompileShader(shader);

        return shader;
    }
}