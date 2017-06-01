package com.tujh.android.myfirstopengl.sdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.tujh.android.myfirstopengl.R;

public class MainSDKActivity extends AppCompatActivity {

    private SDKGLSurfaceView sdkGLSurfaceView;
    private RelativeLayout settingLayout;

    private AppCompatSpinner propSpinner;
    private AppCompatSpinner filterSpinner;
    private AppCompatSeekBar blurSeekbar;
    private AppCompatSeekBar colorSeekbar;
    private AppCompatSeekBar cheekThinningSeekbar;
    private AppCompatSeekBar eyeSeekbar;

    private Button change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk);
        initView();
    }

    private void initView() {
        sdkGLSurfaceView = (SDKGLSurfaceView) findViewById(R.id.sdk_glsurfaceview);

        sdkGLSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingLayout.getVisibility() == View.VISIBLE) {
                    settingLayout.setVisibility(View.GONE);
                } else {
                    settingLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        settingLayout = (RelativeLayout) findViewById(R.id.setting_layout);

        propSpinner = (AppCompatSpinner) findViewById(R.id.prop_spinner);
        filterSpinner = (AppCompatSpinner) findViewById(R.id.filter_spinner);
        blurSeekbar = (AppCompatSeekBar) findViewById(R.id.blur_seekbar);
        colorSeekbar = (AppCompatSeekBar) findViewById(R.id.color_seekbar);
        cheekThinningSeekbar = (AppCompatSeekBar) findViewById(R.id.cheek_thinning_seekbar);
        eyeSeekbar = (AppCompatSeekBar) findViewById(R.id.eye_seekbar);
        change = (Button) findViewById(R.id.change);

        propSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sdkGLSurfaceView.setmEffectFileName(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sdkGLSurfaceView.setmEffectFileName(0);
            }
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sdkGLSurfaceView.setmFilterName(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sdkGLSurfaceView.setmFilterName(0);
            }
        });

        blurSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int level = 0;
                if (progress < 5) {
                    level = 0;
                } else if (progress < 15) {
                    level = 1;
                } else if (progress < 25) {
                    level = 2;
                } else if (progress < 35) {
                    level = 3;
                } else if (progress < 45) {
                    level = 4;
                } else if (progress < 55) {
                    level = 5;
                } else {
                    level = 6;
                }
                sdkGLSurfaceView.setmFacebeautyBlurLevel(level);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() < 5) {
                    seekBar.setProgress(0);
                } else if (seekBar.getProgress() < 15) {
                    seekBar.setProgress(10);
                } else if (seekBar.getProgress() < 25) {
                    seekBar.setProgress(20);
                } else if (seekBar.getProgress() < 35) {
                    seekBar.setProgress(30);
                } else if (seekBar.getProgress() < 45) {
                    seekBar.setProgress(40);
                } else if (seekBar.getProgress() < 55) {
                    seekBar.setProgress(50);
                } else {
                    seekBar.setProgress(60);
                }
            }
        });

        colorSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sdkGLSurfaceView.setmFacebeautyColorLevel(1.0f * progress / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        cheekThinningSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sdkGLSurfaceView.setmFacebeautyCheeckThin(2.0f * progress / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        eyeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sdkGLSurfaceView.setmFacebeautyEnlargeEye(4.0f * progress / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdkGLSurfaceView.changeCamera();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        sdkGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sdkGLSurfaceView.onPause();
    }

}
