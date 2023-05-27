package com.example.weatherapp;

import static com.example.weatherapp.utils.Utils.convertStringDoubleToStringInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.adapter.ForecastAdapter;
import com.example.weatherapp.event.ClickItemListener;
import com.example.weatherapp.event.ClickItemListenerChart;
import com.example.weatherapp.model.Forecast;
import com.example.weatherapp.utils.CustomLineChartRenderer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class NextDayActivity extends AppCompatActivity implements ClickItemListenerChart {
    ImageView imageBack;
    RecyclerView rvForecast;
    ForecastAdapter forecastAdapter;
    ArrayList<Forecast> forecasts;

    LineChart chart;

    ArrayList<Entry> values;

    String[] hours = new String[]{"00:00", "01:00", "02:00", "03:00", "04:00", "05:00"
            , "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
            "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00",
            "22:00", "23:00"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_day);
        init();
        Intent intent = getIntent();
        String city = intent.getStringExtra("location");
        forecastNext5Days(city);
    }

    private void setupChart() {
        chart = (LineChart) findViewById(R.id.chart2);

//        mChart.setOnChartGestureListener(Detail_Activity.this);
//        mChart.setOnChartValueSelectedListener(Detail_Activity.this);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        /*
        * Add data
        *
        * */

        // đối tượng này cho phép tạo kiểu cho data
        LineDataSet set1 = new LineDataSet(values, "Dự báo 24 giờ");

        set1.setLineWidth(2f);
        set1.setColor(0xFFFA9C1B);
        set1.setDrawCircleHole(true);
        set1.setCircleColor( Color.WHITE );
        set1.setCircleRadius(2f);
        set1.setValueTextSize(14f);
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


        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(hours));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false); // Hiển thị đường trục X
        xAxis.setDrawGridLines(false); // Tắt lưới trục X
        xAxis.setDrawLabels(true);
        xAxis.setTextColor(0xFFEEEEEE);
        xAxis.setTextSize(12f);

        YAxis yAxisLeft = chart.getAxisLeft(); // Hoặc chart.getAxisRight() nếu bạn muốn chỉnh trục Y bên phải
        yAxisLeft.setEnabled(false);
        yAxisLeft.setAxisMinimum(20f);
        yAxisLeft.setAxisMaximum(40f);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false); // Tắt trục Y bên phải

        chart.getLegend().setEnabled(false);

        chart.setVisibleXRangeMaximum(3); // Số lượng điểm muốn hiển thị trên màn hình

        // Di chuyển biểu đồ đến vị trí đầu 00:00
        chart.moveViewToX(0);

        // Tắt chế độ zoom bằng cử chỉ pinch
        chart.setPinchZoom(false);

        // Tắt chế độ zoom bằng cử chỉ double tap
        chart.setDoubleTapToZoomEnabled(false);

        // Tắt chế độ hiển thị line khi ấn vào biểu đồ
        chart.setHighlightPerTapEnabled(false);

        // Đặt padding cho biểu đồ (left, top, right, bottom)
        chart.setExtraOffsets(10f, 30f, 10f, 20f);
    }

    private void init() {
        imageBack = findViewById(R.id.imageBack);
        rvForecast = findViewById(R.id.rvForecast);

        forecasts = new ArrayList<Forecast>();

        forecastAdapter = new ForecastAdapter(forecasts, this);
        forecastAdapter.setClickItemListenerChart(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);

        rvForecast.setAdapter(forecastAdapter);
        rvForecast.setNestedScrollingEnabled(false);
        rvForecast.setLayoutManager(linearLayoutManager);

        // Event bấm nút quay lại gd chính
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // quay lại activity trước
            }
        });
    }

    private void parseDataFromResponseObject(String response) {
        Log.i("info", response);
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONObject forecastObj = new JSONObject(jsonObj.getString("forecast"));
            JSONArray forecastdayList = forecastObj.getJSONArray("forecastday");

            for(int i = 0; i < forecastdayList.length(); i++){
                JSONObject forecastdayObj = forecastdayList.getJSONObject(i);
                String date = forecastdayObj.getString("date");

                JSONObject dayObj = new JSONObject(forecastdayObj.getString("day"));
                JSONArray hourObj = forecastdayObj.getJSONArray("hour");
                String maxTemp = convertStringDoubleToStringInt(dayObj.getString("maxtemp_c"));
                String minTemp = convertStringDoubleToStringInt(dayObj.getString("mintemp_c"));
                String wind = dayObj.getString("maxwind_kph");

                JSONObject conditionObj = new JSONObject(dayObj.getString("condition"));
                String imageUrl = conditionObj.getString("icon");
                String status = conditionObj.getString("text");

                Forecast forecast = new Forecast(date, status, imageUrl, maxTemp, minTemp, wind);

                // parse giờ
                for(int j = 0; j < hourObj.length(); j++){
                    JSONObject hourDetailObj = hourObj.getJSONObject(j);
                    String tempDetail = convertStringDoubleToStringInt(hourDetailObj.getString("temp_c"));

                    forecast.getTemps().add(Integer.parseInt(tempDetail));
                }

                // thêm vào list cho adapter
                forecasts.add(forecast);
            }

            // cập nhật adapter
            forecastAdapter.notifyDataSetChanged();

            // cập nhật chart list = list nhiệt độ ngày hnay

            Log.i("logging", String.valueOf(forecasts.get(0).getTemps().size()));
            ArrayList<Integer> currentDayTemps = forecasts.get(0).getTemps();

            values = new ArrayList<>();
            for(int i = 0; i < currentDayTemps.size(); i++){
                values.add(new Entry(i, currentDayTemps.get(i)));
            }

            setupChart();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void forecastNext5Days(String locationInput) {
        String apiKey = "398a6a2569394f8695e170734232205";
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + locationInput + "&days=5";

        RequestQueue requestQueue = Volley.newRequestQueue(NextDayActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseDataFromResponseObject(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }

    @Override
    public ForecastAdapter.ForecastViewHolder onItemClick(View view, int position, ForecastAdapter.ForecastViewHolder selectedHolder, ForecastAdapter.ForecastViewHolder viewHolder) {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.rounded);
        selectedHolder.layout.setBackground(null);

        viewHolder.layout.setBackground(drawable);
        selectedHolder = viewHolder;

        // refresh chart
        ArrayList<Integer> selectedDayTemps = forecasts.get(position).getTemps();

        for(int i = 0; i < values.size(); i++){
            values.get(i).setY(selectedDayTemps.get(i));
        }

        chart.notifyDataSetChanged();
        chart.invalidate();

        return selectedHolder;
    }
}