package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.weatherapp.utils.CustomLineChartRenderer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Detail_Activity extends AppCompatActivity  {
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        chart = (LineChart) findViewById(R.id.chart);

//        mChart.setOnChartGestureListener(Detail_Activity.this);
//        mChart.setOnChartValueSelectedListener(Detail_Activity.this);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(0, 31f));
        values.add(new Entry(1, 31f));
        values.add(new Entry(2, 32f));
        values.add(new Entry(3, 32f));
        values.add(new Entry(4, 34f));
        values.add(new Entry(5, 29f));
        values.add(new Entry(6, 29f));

        // đối tượng này cho phép tạo kiểu cho data
        LineDataSet set1 = new LineDataSet(values, "Dự báo 24 giờ");

        set1.setLineWidth(2f);
        set1.setColor(0xFFFA9C1B);
        set1.setDrawCircleHole(true);
        set1.setCircleColor( Color.WHITE );
        set1.setCircleRadius(2f);
        set1.setValueTextSize(12f);
        set1.setValueTextColor(0xFFFFFFFF);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // cho giá trị của circle cao lên trên so với line
        CustomLineChartRenderer customRenderer = new CustomLineChartRenderer(chart, chart.getAnimator(), chart.getViewPortHandler());
        chart.setRenderer(customRenderer);

        // tắt description trục x
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // format lại giá trị hiện trên circle
                return ((int) value) + "°";
            }
        });

        LineData data = new LineData(set1);

        chart.setData(data);

        String[] hours = new String[]{"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(hours));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false); // Hiển thị đường trục X
        xAxis.setDrawGridLines(false); // Tắt lưới trục X
        xAxis.setDrawLabels(true);
        xAxis.setTextColor(0xFFEEEEEE);

        YAxis yAxisLeft = chart.getAxisLeft(); // Hoặc chart.getAxisRight() nếu bạn muốn chỉnh trục Y bên phải
        yAxisLeft.setEnabled(false);
        yAxisLeft.setAxisMinimum(20f);
        yAxisLeft.setAxisMaximum(40f);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false); // Tắt trục Y bên phải

        chart.getLegend().setEnabled(false);

        chart.setVisibleXRangeMaximum(3); // Số lượng điểm muốn hiển thị trên màn hình

        // Di chuyển biểu đồ đến vị trí cuối cùng (dữ liệu mới nhất)
        chart.moveViewToX(set1.getEntryCount() - 1);

        // Tắt chế độ zoom bằng cử chỉ pinch
        chart.setPinchZoom(false);

        // Tắt chế độ zoom bằng cử chỉ double tap
        chart.setDoubleTapToZoomEnabled(false);

        // Tắt chế độ hiển thị line khi ấn vào biểu đồ
        chart.setHighlightPerTapEnabled(false);

        // Đặt padding cho biểu đồ (left, top, right, bottom)
        chart.setExtraOffsets(20f, 30f, 20f, 20f);


    }
}