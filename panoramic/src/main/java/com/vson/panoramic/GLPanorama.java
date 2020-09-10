package com.vson.panoramic;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vson.panoramic.bean.SensorInfo;
import com.vson.panoramic.utils.Ball;


/**
 * @author vson
 */
public class GLPanorama extends RelativeLayout implements SensorEventListener {
    private Context mContext;
    private PanoramaSurfaceView mGlSurfaceView;
    private ImageView img;
    private float mPreviousY;
    private float mPreviousYs;
    private float mPreviousX;
    private float mPreviousXs;
    private float predegrees = 0.0F;
    private Ball mBall;
    private SensorManager mSensorManager;
    private Sensor mGyroscopeSensor;
    private static final float NS2S = 1.0E-9F;
    private float timestamp;
    private float[] angle = new float[3];
    private Handler mHandler;
    private Handler mHandlers;
    int yy;

    public GLPanorama(Context context) {
        this(context, null);
    }

    public GLPanorama(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public GLPanorama(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mHandler = new RotateHandler();
        this.mHandlers = new Handler();
        this.yy = 0;
        this.mContext = context;
        this.init();
    }

    private void init() {
        this.initView();
    }

    private void initView() {
        LayoutInflater.from(this.mContext).inflate(R.layout.layout_gl_panorama, this);
        this.mGlSurfaceView = this.findViewById(R.id.mIViews);
        this.img = this.findViewById(R.id.img);
        this.img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                GLPanorama.this.zero();
            }
        });
    }

    private void initSensor() {
        this.mSensorManager = (SensorManager) this.mContext.getSystemService(Context.SENSOR_SERVICE);
        this.mGyroscopeSensor = this.mSensorManager.getDefaultSensor(4);
        this.mSensorManager.registerListener(this, this.mGyroscopeSensor, 0);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 4) {
            if (this.timestamp != 0.0F) {
                float dT = ((float) sensorEvent.timestamp - this.timestamp) * 1.0E-9F;
                float[] mAngle = this.angle;
                mAngle[0] += sensorEvent.values[0] * dT;
                mAngle = this.angle;
                mAngle[1] += sensorEvent.values[1] * dT;
                mAngle = this.angle;
                mAngle[2] += sensorEvent.values[2] * dT;
                float angleY = (float) Math.toDegrees(this.angle[0]);
                float angleX = (float) Math.toDegrees(this.angle[1]);
                float angleZ = (float) Math.toDegrees(this.angle[2]);
                SensorInfo info = new SensorInfo();
                info.setSensorX(angleX);
                info.setSensorY(angleY);
                info.setSensorZ(angleZ);
                Message msg = new Message();
                msg.what = 101;
                msg.obj = info;
                this.mHandler.sendMessage(msg);
            }
            this.timestamp = (float) sensorEvent.timestamp;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mSensorManager.unregisterListener(this);
        float y = event.getY();
        float x = event.getX();
        switch (event.getAction()) {
            case 1:
                this.mSensorManager.registerListener(this, this.mGyroscopeSensor, 0);
                break;
            case 2:
                float dy = y - this.mPreviousYs;
                float dx = x - this.mPreviousXs;
                Ball mBall = this.mBall;
                mBall.yAngle += dx * 0.3F;
                mBall = this.mBall;
                mBall.xAngle += dy * 0.3F;
                if (this.mBall.xAngle < -50.0F) {
                    this.mBall.xAngle = -50.0F;
                } else if (this.mBall.xAngle > 50.0F) {
                    this.mBall.xAngle = 50.0F;
                }
                this.rotate();
                break;
            default:
                break;
        }

        this.mPreviousYs = y;
        this.mPreviousXs = x;
        return true;
    }

    public void setGLPanorama(int imgId) {
        this.mGlSurfaceView.setEGLContextClientVersion(2);
        this.mBall = new Ball(this.mContext, imgId);
        this.mGlSurfaceView.setRenderer(this.mBall);
        this.initSensor();
    }

    private void rotate() {
        RotateAnimation anim = new RotateAnimation(this.predegrees, -this.mBall.yAngle, 1, 0.5F, 1, 0.5F);
        anim.setDuration(200L);
        this.img.startAnimation(anim);
        this.predegrees = -this.mBall.yAngle;
    }

    private void zero() {
        this.yy = (int) ((this.mBall.yAngle - 90.0F) / 10.0F);
        this.mHandlers.post(new Runnable() {
            @Override
            public void run() {
                if (GLPanorama.this.yy != 0) {
                    if (GLPanorama.this.yy > 0) {
                        GLPanorama.this.mBall.yAngle -= 10.0F;
                        GLPanorama.this.mHandlers.postDelayed(this, 16L);
                        --GLPanorama.this.yy;
                    }

                    if (GLPanorama.this.yy < 0) {
                        GLPanorama.this.mBall.yAngle += 10.0F;
                        GLPanorama.this.mHandlers.postDelayed(this, 16L);
                        ++GLPanorama.this.yy;
                    }
                } else {
                    GLPanorama.this.mBall.yAngle = 90.0F;
                }

                GLPanorama.this.mBall.xAngle = 0.0F;
            }
        });
    }


    class RotateHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
                    SensorInfo info = (SensorInfo) msg.obj;
                    float y = info.getSensorY();
                    float x = info.getSensorX();
                    float dy = y - GLPanorama.this.mPreviousY;
                    float dx = x - GLPanorama.this.mPreviousX;
                    Ball mBall = GLPanorama.this.mBall;
                    mBall.yAngle += dx * 2.0F;
                    mBall = GLPanorama.this.mBall;
                    mBall.xAngle += dy * 0.5F;
                    if (GLPanorama.this.mBall.xAngle < -50.0F) {
                        GLPanorama.this.mBall.xAngle = -50.0F;
                    } else if (GLPanorama.this.mBall.xAngle > 50.0F) {
                        GLPanorama.this.mBall.xAngle = 50.0F;
                    }

                    GLPanorama.this.mPreviousY = y;
                    GLPanorama.this.mPreviousX = x;
                    GLPanorama.this.rotate();
                default:
            }
        }
    }
}
