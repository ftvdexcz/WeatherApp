package com.example.weatherapp.utils;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomLineChartRenderer extends LineChartRenderer {

    public CustomLineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        // Di chuyển chữ lên trên circle
        y -= 10f; // Điều chỉnh vị trí chữ theo y theo ý muốn

        super.drawValue(c, valueText, x, y, color);
    }
}
