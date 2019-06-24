package com.fujica.firstgl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.fujica.firstgl.util.LoggerConfig;
import com.fujica.firstgl.util.ShaderHelper;
import com.fujica.firstgl.util.TextResourceLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by zzp on 2019/6/24.
 */
public class FirstGLRender implements GLSurfaceView.Renderer {
    private final Context context;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BITS_PER_FLOAT = 4;
    private static final String A_POSIYION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BITS_PER_FLOAT;
    private final FloatBuffer vertexData;
    private int program;
    private int aPositionLocation;
    private int aColorLocation;

    public FirstGLRender(Context contextIn){
        this.context = contextIn;
        float[] tableVertices = {
                0f,     0f,     1.0f, 1.0f, 1.0f,
                -0.5f,  -0.5f,  0.7f, 0.7f, 0.7f,
                0.5f,   -0.5f,  0.7f, 0.7f, 0.7f,
                0.5f,   0.5f,   0.7f, 0.7f, 0.7f,
                -0.5f,  0.5f,   0.7f, 0.7f, 0.7f,
                -0.5f,  -0.5f,  0.7f, 0.7f, 0.7f,

                -0.5f,  0f,     1.0f, 0f, 0f,
                0.5f,   0f,     0f, 1.0f, 0f,

                0f, -0.25f,     0f,   0f, 1.0f,
                0f, 0.25f,      1.0f, 0f, 0f,
        };

        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BITS_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceLoader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceLoader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if(LoggerConfig.ON){
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSIYION);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        // 画桌面，两个三角形
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
        // 画中间分割线
        glDrawArrays(GL_LINES, 6, 2);
        // 画点
        glDrawArrays(GL_POINTS, 8, 1);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
