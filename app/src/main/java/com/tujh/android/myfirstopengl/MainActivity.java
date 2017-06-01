package com.tujh.android.myfirstopengl;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tujh.android.myfirstopengl.photo.MainPhotoActivity;
import com.tujh.android.myfirstopengl.sdk.MainSDKActivity;
import com.tujh.android.myfirstopengl.surfaceview.MainSurfaceViewActivity;
import com.tujh.android.myfirstopengl.textureview.MainTextureViewActivity;
import com.tujh.android.myfirstopengl.triangle.MainTriangleActivity;
import com.tujh.android.myfirstopengl.video.MainVideoActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CAMERA = 1;

    private Button triangleBtn;
    private Button photoBtn;
    private Button surfaceViewBtn;
    private Button textureViewBtn;
    private Button videoBtn;
    private Button sdkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        triangleBtn = (Button) findViewById(R.id.triangle);
        photoBtn = (Button) findViewById(R.id.photo);
        surfaceViewBtn = (Button) findViewById(R.id.surfaceview);
        textureViewBtn = (Button) findViewById(R.id.textureview);
        videoBtn = (Button) findViewById(R.id.video);
        sdkBtn = (Button) findViewById(R.id.sdk);

        triangleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainTriangleActivity.class);
                startActivity(intent);
            }
        });

        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainPhotoActivity.class);
                startActivity(intent);
            }
        });

        surfaceViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCameraPermission()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, MainSurfaceViewActivity.class);
                startActivity(intent);
            }
        });

        textureViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCameraPermission()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, MainTextureViewActivity.class);
                startActivity(intent);
            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCameraPermission()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, MainVideoActivity.class);
                startActivity(intent);
            }
        });

        sdkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCameraPermission()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, MainSDKActivity.class);
                startActivity(intent);
            }
        });
    }


    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(MainActivity.this, "抱歉，该功能必须有camera权限才能使用。", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "申请权限成功，请再点击一次使用该功能。", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "申请权限已被拒绝。", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
