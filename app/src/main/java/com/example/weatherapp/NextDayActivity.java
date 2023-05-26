package com.example.weatherapp;

import static com.example.weatherapp.utils.Utils.convertStringDoubleToStringInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.example.weatherapp.model.Forecast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NextDayActivity extends AppCompatActivity {
    ImageView imageBack;
    RecyclerView rvForecast;
    RecyclerView.Adapter forecastAdapter;

    ArrayList<Forecast> forecasts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_day);
        init();
        Intent intent = getIntent();
        String city = intent.getStringExtra("location");
        forecastNext5Days(city);
    }

    private void init() {
        imageBack = findViewById(R.id.imageBack);
        rvForecast = findViewById(R.id.rvForecast);

        forecasts = new ArrayList<Forecast>();

        forecastAdapter = new ForecastAdapter(forecasts, this);

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
                String maxTemp = convertStringDoubleToStringInt(dayObj.getString("maxtemp_c"));
                String minTemp = convertStringDoubleToStringInt(dayObj.getString("mintemp_c"));
                String wind = dayObj.getString("maxwind_kph");

                JSONObject conditionObj = new JSONObject(dayObj.getString("condition"));
                String imageUrl = conditionObj.getString("icon");
                String status = conditionObj.getString("text");

                // thêm vào list cho adapter
                forecasts.add(new Forecast(date, status, imageUrl, maxTemp, minTemp, wind));
            }

            // cập nhật adapter
            forecastAdapter.notifyDataSetChanged();

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
}