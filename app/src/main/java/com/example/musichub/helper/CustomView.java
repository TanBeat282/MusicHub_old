package com.example.musichub.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class CustomView extends View {
    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Màu gradient
        int startColor = Color.parseColor("#07f49e");
        int endColor = Color.parseColor("#42047e");
        int centerColor = Color.parseColor("#00000000");

        // Tạo gradient từ trung tâm lên top và trung tâm xuống bottom
        Shader shaderTop = new RadialGradient(width / 2, height / 2, height / 2, centerColor, startColor, Shader.TileMode.CLAMP);
        Shader shaderBottom = new RadialGradient(width / 2, height / 2, height / 2, centerColor, endColor, Shader.TileMode.CLAMP);

        Paint paint = new Paint();

        // Vẽ gradient từ trung tâm lên top
        paint.setShader(shaderTop);
        canvas.drawRect(0, 0, width, height / 2, paint);

        // Vẽ gradient từ trung tâm xuống bottom
        paint.setShader(shaderBottom);
        canvas.drawRect(0, height / 2, width, height, paint);
    }
}