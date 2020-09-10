package com.vson.panoramic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * @author vson
 */
public class GLPanoramaActivity extends AppCompatActivity {

    private GLPanorama glPanorama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_l_panorama);


        glPanorama = findViewById(R.id.gl_panorama);
        glPanorama.setGLPanorama(R.drawable.test2);

    }
}