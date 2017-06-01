package com.tujh.android.myfirstopengl.sdk;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by tujh on 2017/5/25.
 */

public class Encoder {

    private static final String TAG = "Encoder";


    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int IFRAME_INTERVAL = 1;           // sync frame every second
    private static final int bitRate = 6000000;

    private EncoderThread mEncoderThread;
    private Surface mInputSurface;

    private MediaCodec mEncoder;
    private MediaFormat mEncodedFormat;
    private MediaCodec.BufferInfo mOutputBufferInfo;
    private ByteBuffer[] encoderOutputBuffers;

    private MediaMuxer mMediaMuxer;
    private int videoTrack;

    public void startEncode(final File outputFile, final int width, final int height, final int frameRate) {
        mEncoderThread.encoderHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mEncodedFormat = MediaFormat.createVideoFormat(MIME_TYPE, width, height);

                    // Set some properties.  Failing to specify some of these can cause the MediaCodec
                    // configure() call to throw an unhelpful exception.
                    mEncodedFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
                    mEncodedFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
                    mEncodedFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
                    mEncodedFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

                    // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
                    // we can use for input and wrap it with a class that handles the EGL work.

                    mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);

                    mEncoder.configure(mEncodedFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                    mInputSurface = mEncoder.createInputSurface();
                    mEncoder.start();
                    encoderOutputBuffers = mEncoder.getOutputBuffers();
                    mOutputBufferInfo = new MediaCodec.BufferInfo();

                    mMediaMuxer = new MediaMuxer(outputFile.getPath(),
                            MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    videoTrack = mMediaMuxer.addTrack(mEncodedFormat);
                    mMediaMuxer.start();

                    mEncoderThread = new EncoderThread();
                    mEncoderThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void encode() {
        mEncoderThread.encoderHandler.post(new Runnable() {
            @Override
            public void run() {
                int encoderStatus = mEncoder.dequeueOutputBuffer(mOutputBufferInfo, 0);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    return;
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // not expected for an encoder
                    encoderOutputBuffers = mEncoder.getOutputBuffers();
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // Should happen before receiving buffers, and should only happen once.
                    // The MediaFormat contains the csd-0 and csd-1 keys, which we'll need
                    // for MediaMuxer.  It's unclear what else MediaMuxer might want, so
                    // rather than extract the codec-specific data and reconstruct a new
                    // MediaFormat later, we just grab it here and keep it around.
                    mEncodedFormat = mEncoder.getOutputFormat();
                    Log.d(TAG, "encoder output format changed: " + mEncodedFormat);
                } else if (encoderStatus < 0) {
                    Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                            encoderStatus);
                    // let's ignore it
                } else {
                    ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                                " was null");
                    }

                    if ((mOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        // The codec config data was pulled out when we got the
                        // INFO_OUTPUT_FORMAT_CHANGED status.  The MediaMuxer won't accept
                        // a single big blob -- it wants separate csd-0/csd-1 chunks --
                        // so simply saving this off won't work.
                        mOutputBufferInfo.size = 0;
                    }

                    if (mOutputBufferInfo.size != 0) {
                        // adjust the ByteBuffer values to match BufferInfo (not needed?)
                        encodedData.position(mOutputBufferInfo.offset);
                        encodedData.limit(mOutputBufferInfo.offset + mOutputBufferInfo.size);

                        if (null != mMediaMuxer)
                            mMediaMuxer.writeSampleData(videoTrack, encodedData, mOutputBufferInfo);
                    }

                    mEncoder.releaseOutputBuffer(encoderStatus, false);

                }
            }
        });

    }

    public void stopEncode() {
        mEncoderThread.encoderHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMediaMuxer != null) {
                    mMediaMuxer.stop();
                    mMediaMuxer.release();
                }

                if (mEncoder != null) {
                    mEncoder.stop();
                    mEncoder.release();
                    mEncoder = null;
                }

                mEncoderThread.encoderHandler.getLooper().quitSafely();
            }
        });
    }

    class EncoderThread extends Thread {
        public Handler encoderHandler;

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            encoderHandler = new Handler();
            Looper.loop();
        }
    }

    public Surface getmInputSurface() {
        return mInputSurface;
    }
}
