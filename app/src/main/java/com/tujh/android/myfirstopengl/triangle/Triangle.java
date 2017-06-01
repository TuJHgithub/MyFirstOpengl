package com.tujh.android.myfirstopengl.triangle;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zxcv on 2017/5/8.
 */

public class Triangle {

    // GLSL语言定义的vertex shader
    private final String vertexShaderCode =
            // vec4 4个浮点成分的向量
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";
    // GLSL语言定义的fragment shader
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;

    // 在数组中顶点的坐标数
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            0.0f, 0.5f, 0.0f,   // top
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f    // bottom right
    };
    // 顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    // 每个顶点的坐标字节数
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    float color[] = {1.0f, 0.0f, 0.0f, 0.0f};

    public Triangle() {
        // 创建用于存储顶点坐标数据的Buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // float存储的大小为4字节
                triangleCoords.length * 4);
        // 把buffer里的数据存储为底层对应的字节序（大端、小端）
        bb.order(ByteOrder.nativeOrder());

        // 创建FloatBuffer
        vertexBuffer = bb.asFloatBuffer();
        // 添加顶点坐标数据
        vertexBuffer.put(triangleCoords);
        // 定位到第一个顶点
        vertexBuffer.position(0);

        // 着色器
        int vertexShader = TriangleGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = TriangleGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // 创建一个 OpenGL Program 并关联 shader
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    public void draw() {
        // 把初始化的OpenGL Program添加到环境中
        GLES20.glUseProgram(mProgram);

        // 获得vertex shader的vPosition变量句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // 启用vPosition
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // 给vPosition变量赋值
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // 获取Fragment shader的vColor变量句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // 给vColor变量赋值
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // 画三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // 禁用vPosition
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}
