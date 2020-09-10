package com.vson.panoramic;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

/**
 * @author vson
 */
public class GoogleVRActivity extends AppCompatActivity {


    private VrPanoramaView vrPanoramaView;
    private VrPanoramaView.Options paNormalOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_v_r);
        initView();
    }

    private void initView() {
        vrPanoramaView = findViewById(R.id.vr);
        paNormalOptions = new VrPanoramaView.Options();

        paNormalOptions.inputType = VrPanoramaView.Options.TYPE_MONO;

        //隐藏全屏模式按钮
        vrPanoramaView.setFullscreenButtonEnabled (false);
        //设置隐藏最左边信息的按钮
        vrPanoramaView.setInfoButtonEnabled(false);
        //设置隐藏立体模型的按钮
        vrPanoramaView.setStereoModeButtonEnabled(false);
        //设置监听
        vrPanoramaView.setEventListener(new ActivityEventListener());
        vrPanoramaView.loadImageFromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test2), paNormalOptions);
    }

    private class ActivityEventListener extends VrPanoramaEventListener {
        @Override
        public void onLoadSuccess() {

        }


        @Override
        public void onLoadError(String errorMessage) {
        }

        @Override
        public void onClick() {
            super.onClick();
        }

        @Override
        public void onDisplayModeChanged(int newDisplayMode) {
            //改变显示模式时候出发（全屏模式和纸板模式）
            super.onDisplayModeChanged(newDisplayMode);
        }
    }
}