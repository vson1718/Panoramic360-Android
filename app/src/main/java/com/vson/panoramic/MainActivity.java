package com.vson.panoramic;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * @author vson
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OpenGl360(View view) {
        startActivity(new Intent(this, GLPanoramaActivity.class));
    }

    public void OpenAR360(View view) {
        startActivity(new Intent(this, GoogleVRActivity.class));
    }

    public void OpenWeb360(View view) {
        startActivity(new Intent(this, WebViewActivity.class));
    }
}