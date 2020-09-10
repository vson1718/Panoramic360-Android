package com.vson.panoramic.utils;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.opengl.GLSurfaceView.Renderer;

/**
 * @author vson
 */
public class Ball implements Renderer {
    Context mContext;
    private int mProgram;
    private int mAPositionHandler;
    private int mUProjectMatrixHandler;
    private int mATextureCoordHandler;
    private final float[] projectMatrix = new float[16];
    private int mSize;
    private FloatBuffer vertexBuff;
    private FloatBuffer textureBuff;
    private int textTrueID;
    private int mImgId;
    public float xAngle = 0.0F;
    public float yAngle = 90.0F;
    public float zAngle;
    final float[] mCurrMatrix = new float[16];
    final float[] mMVPMatrix = new float[16];

    public Ball(Context context, int drawableId) {
        this.mContext = context;
        this.mImgId = drawableId;
    }

    public void initData() {
        int perVertex = 36;
        double perRadius = 6.283185307179586D / (double) ((float) perVertex);
        double perW = (1.0F / (float) perVertex);
        double perH = (1.0F / (float) perVertex);
        ArrayList<Float> vertexList = new ArrayList();
        ArrayList<Float> textureList = new ArrayList();
        initVertexTextureList(perVertex, perRadius, perW, perH, vertexList, textureList);
        this.mSize = vertexList.size() / 3;
        float[] texture = new float[this.mSize * 2];
        for (int i = 0; i < texture.length; ++i) {
            texture[i] = textureList.get(i);
        }
        this.textureBuff = ByteBuffer.allocateDirect(texture.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        this.textureBuff.put(texture);
        this.textureBuff.position(0);
        float[] vertex = new float[this.mSize * 3];
        for (int i = 0; i < vertex.length; ++i) {
            vertex[i] = vertexList.get(i);
        }
        this.vertexBuff = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        this.vertexBuff.put(vertex);
        this.vertexBuff.position(0);
    }

    /**
     * 初始化着色器使用数据
     *
     * @param perVertex
     * @param perRadius
     * @param perW
     * @param perH
     * @param vertexList
     * @param textureList
     */
    private void initVertexTextureList(int perVertex, double perRadius, double perW, double perH, ArrayList<Float> vertexList, ArrayList<Float> textureList) {
        for (int a = 0; a < perVertex; ++a) {
            for (int i = 0; i < perVertex; ++i) {
                float w1 = (float) ((double) a * perH);
                float h1 = (float) ((double) i * perW);

                float w2 = (float) ((double) (a + 1) * perH);
                float h2 = (float) ((double) i * perW);

                float w3 = (float) ((double) (a + 1) * perH);
                float h3 = (float) ((double) (i + 1) * perW);

                float w4 = (float) ((double) a * perH);
                float h4 = (float) ((double) (i + 1) * perW);
                textureList.add(h1);
                textureList.add(w1);
                textureList.add(h2);
                textureList.add(w2);
                textureList.add(h3);
                textureList.add(w3);
                textureList.add(h3);
                textureList.add(w3);
                textureList.add(h4);
                textureList.add(w4);
                textureList.add(h1);
                textureList.add(w1);

                float x1 = (float) (Math.sin((double) a * perRadius / 2.0D) * Math.cos((double) i * perRadius));
                float z1 = (float) (Math.sin((double) a * perRadius / 2.0D) * Math.sin((double) i * perRadius));
                float y1 = (float) Math.cos((double) a * perRadius / 2.0D);

                float x2 = (float) (Math.sin((double) (a + 1) * perRadius / 2.0D) * Math.cos((double) i * perRadius));
                float z2 = (float) (Math.sin((double) (a + 1) * perRadius / 2.0D) * Math.sin((double) i * perRadius));
                float y2 = (float) Math.cos((double) (a + 1) * perRadius / 2.0D);

                float x3 = (float) (Math.sin((double) (a + 1) * perRadius / 2.0D) * Math.cos((double) (i + 1) * perRadius));
                float z3 = (float) (Math.sin((double) (a + 1) * perRadius / 2.0D) * Math.sin((double) (i + 1) * perRadius));
                float y3 = (float) Math.cos((double) (a + 1) * perRadius / 2.0D);

                float x4 = (float) (Math.sin((double) a * perRadius / 2.0D) * Math.cos((double) (i + 1) * perRadius));
                float z4 = (float) (Math.sin((double) a * perRadius / 2.0D) * Math.sin((double) (i + 1) * perRadius));
                float y4 = (float) Math.cos((double) a * perRadius / 2.0D);

                vertexList.add(x1);
                vertexList.add(y1);
                vertexList.add(z1);
                vertexList.add(x2);
                vertexList.add(y2);
                vertexList.add(z2);
                vertexList.add(x3);
                vertexList.add(y3);
                vertexList.add(z3);
                vertexList.add(x3);
                vertexList.add(y3);
                vertexList.add(z3);
                vertexList.add(x4);
                vertexList.add(y4);
                vertexList.add(z4);
                vertexList.add(x1);
                vertexList.add(y1);
                vertexList.add(z1);
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        Matrix.rotateM(this.mCurrMatrix, 0, -this.xAngle, 1.0F, 0.0F, 0.0F);
        Matrix.rotateM(this.mCurrMatrix, 0, -this.yAngle, 0.0F, 1.0F, 0.0F);
        Matrix.rotateM(this.mCurrMatrix, 0, -this.zAngle, 0.0F, 0.0F, 1.0F);

        GLES20.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //相当于激活一个用来显示图片的画框
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.textTrueID);
        GLES20.glUniformMatrix4fv(this.mUProjectMatrixHandler, 1, false, getFinalMVPMatrix(), 0);
        //通知绘画
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.mSize);
    }

    public float[] getFinalMVPMatrix() {
        Matrix.multiplyMM(this.mMVPMatrix, 0, this.projectMatrix, 0, this.mCurrMatrix, 0);
        Matrix.setIdentityM(this.mCurrMatrix, 0);
        return this.mMVPMatrix;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        float ratio = (float) width / (float) height;


        Matrix.frustumM(this.projectMatrix, 0, -ratio, ratio, -1.0F, 1.0F, 1.0F, 20.0F);
        Matrix.setIdentityM(this.mCurrMatrix, 0);
        Matrix.setIdentityM(this.mMVPMatrix, 0);
        Matrix.translateM(this.projectMatrix, 0, 0.0F, 0.0F, -2.0F);
        Matrix.scaleM(this.projectMatrix, 0, 4.0F, 4.0F, 4.0F);

        this.mProgram = OpenGLUtils.getProgram(this.mContext);
        GLES20.glUseProgram(this.mProgram);
        this.mAPositionHandler = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
        this.mUProjectMatrixHandler = GLES20.glGetUniformLocation(this.mProgram, "uProjectMatrix");
        this.mATextureCoordHandler = GLES20.glGetAttribLocation(this.mProgram, "aTextureCoord");
        this.textTrueID = OpenGLUtils.initTexture(this.mContext, this.mImgId);
        GLES20.glVertexAttribPointer(this.mAPositionHandler, 3, GLES20.GL_FLOAT, false, 0, this.vertexBuff);
        GLES20.glVertexAttribPointer(this.mATextureCoordHandler, 2, GLES20.GL_FLOAT, false, 0, this.textureBuff);
        GLES20.glEnableVertexAttribArray(this.mAPositionHandler);
        GLES20.glEnableVertexAttribArray(this.mATextureCoordHandler);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initData();
    }
}
