package com.tujh.android.myfirstopengl.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.tujh.android.myfirstopengl.FPSUtil;
import com.tujh.android.myfirstopengl.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tujh on 2017/5/9.
 */

public class PhotoGLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "PhotoGLRenderer";


    private Bitmap photo;
    private int photoWidth, photoHeight;
    //textures[0]纹理ID（用于操作bitmap转换成的纹理）
    //textures[1]纹理ID，textures[0]转换成的不同效果的纹理
    private int textures[] = new int[2];
    private PhotoSquare photoSquare;

    private EffectContext effectContext;
    private Effect effect;

    public PhotoGLRenderer(Context context) {
        super();
        // 获取bitmap
        photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.pic);
        photoWidth = photo.getWidth();
        photoHeight = photo.getHeight();

    }

    /**
     * 把bitmap数据转换成纹理数据
     */
    private void generateSquare() {
        //创建纹理对象
        GLES20.glGenTextures(2, textures, 0);
        //绑定纹理对象
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        //把bitmap转换成纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, photo, 0);

        photoSquare = new PhotoSquare();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 1);
        generateSquare();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    private void grayScaleEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void documentaryEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_DOCUMENTARY);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void brightnessEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
        effect.setParameter("brightness", 2f);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        if (effectContext == null) {
//            effectContext = EffectContext.createWithCurrentGlContext();
//        }
//        if (effect != null) {
//            effect.release();
//        }
        photoSquare.draw(textures[0]);

        FPSUtil.fps();
//        brightnessEffect();
//        photoSquare.draw(textures[1]);
    }
}

