package com.tujh.android.myfirstopengl.sdk;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by tujh on 2017/5/12.
 */

public class VideoRect {

    public static final float[] IDENTITY_MATRIX;

    static {
        IDENTITY_MATRIX = new float[16];
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }

    //顶点坐标
    private float vertices[] = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };
    //纹理坐标（由于android图像坐标系统与Opengl es 坐标系统不一致，可以认为纹理坐标以坐上为（0，0））
    private float textureVertices[] = {
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
    };
    private FloatBuffer verticesBuffer;
    private FloatBuffer textureBuffer;
    private final String vertexShaderCode =
            "attribute vec4 aPosition;" +
                    "attribute vec4 aTexPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "uniform mat4 uTexMatrix;" +
                    "varying vec2 vTexPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * aPosition;" +
                    "  vTexPosition = (uTexMatrix * aTexPosition).xy;" +
                    "}";

    private final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;" +
                    //使用公司SDK，不需要使用外部纹理，直接使用sampler2D即可
                    "uniform sampler2D uTexture;" +
                    "varying vec2 vTexPosition;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(uTexture, vTexPosition);" +
                    "}";

    private int vertexShader;
    private int fragmentShader;
    private int program;


    public VideoRect() {
        initializeBuffers();
        initializeProgram();
    }

    private void initializeBuffers() {
        ByteBuffer buff = ByteBuffer.allocateDirect(vertices.length * 4);
        buff.order(ByteOrder.nativeOrder());
        verticesBuffer = buff.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        buff = ByteBuffer.allocateDirect(textureVertices.length * 4);
        buff.order(ByteOrder.nativeOrder());
        textureBuffer = buff.asFloatBuffer();
        textureBuffer.put(textureVertices);
        textureBuffer.position(0);

    }

    private void initializeProgram() {
        vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glCompileShader(vertexShader);

        fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShader);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glLinkProgram(program);
    }

    public void draw(int texture, float[] mvpMatrix, float[] mtx) {
        GLES20.glUseProgram(program);

        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        int texturePositionHandle = GLES20.glGetAttribLocation(program, "aTexPosition");
        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);

        int textureHandle = GLES20.glGetUniformLocation(program, "uTexture");
        // 把采样器设置为相应的纹理单元
        GLES20.glUniform1i(textureHandle, 0);

        // 指定哪一个纹理单元被置为活动状态
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        //单元矩阵
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        int texMatrixHandle = GLES20.glGetUniformLocation(program, "uTexMatrix");
        GLES20.glUniformMatrix4fv(texMatrixHandle, 1, false, mtx, 0);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texturePositionHandle);
    }

    public void setTextureVertices(float ratio) {
        textureVertices = new float[]{
                0f, 0.5f - ratio / 2,
                1f, 0.5f - ratio / 2,
                0f, ratio / 2 + 0.5f,
                1f, ratio / 2 + 0.5f
        };

        textureBuffer.clear();
        textureBuffer.put(textureVertices);
        textureBuffer.position(0);
    }
}
