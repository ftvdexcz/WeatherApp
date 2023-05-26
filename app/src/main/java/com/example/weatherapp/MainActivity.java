package com.example.weatherapp;

import static com.example.weatherapp.utils.Utils.convertLocalTimeEpoch;
import static com.example.weatherapp.utils.Utils.convertStringDoubleToStringInt;
import static com.example.weatherapp.utils.Utils.getCurrentTimestampMs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.model.LocationHisFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    Button btnHistory, btnNextDay;
    EditText etSearchLocation;
    TextView tvLocation, tvCountryName, tvTemperature, tvStatus, tvHumidity, tvCloud, tvWind, tvLocaltime;
    ImageView imageIcon;

    String location = "Dong Da", country, localtime, localtime_epoch, humidity, cloud, wind, temperature,
            conditionText, conditionIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void parseDataFromResponseObject(String response){
        Log.d("result", response);
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONObject locationObj =  new JSONObject(jsonObj.getString("location"));
            JSONObject currentObj = new JSONObject(jsonObj.getString("current"));
            JSONObject conditiontObj = new JSONObject(currentObj.getString("condition"));

            location = locationObj.getString("name");
            country = locationObj.getString("country");
            localtime = locationObj.getString("localtime");
            localtime_epoch = convertLocalTimeEpoch(locationObj.getString("localtime_epoch"));
            humidity = currentObj.getString("humidity");
            cloud = currentObj.getString("cloud");
            temperature = convertStringDoubleToStringInt(currentObj.getString("temp_c"));
            wind = currentObj.getString("wind_kph");

            conditionText = conditiontObj.getString("text");
            conditionIcon = conditiontObj.getString("icon");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void getCurrentWeatherByLocation(String locationInput){
        String apiKey = "398a6a2569394f8695e170734232205";
        String url = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + locationInput;

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseDataFromResponseObject(response);

                        // render
                        Log.i("location", location);
                        tvLocation.setText(location);
                        tvCountryName.setText(country);
                        tvLocaltime.setText(localtime_epoch);
                        // render ảnh bằng picasso: https://stackoverflow.com/questions/5776851/load-image-from-url
                        Picasso.get().load("https:" + conditionIcon).fit().into(imageIcon);
                        tvStatus.setText(conditionText);
                        tvTemperature.setText(temperature);
                        tvHumidity.setText("Humidity:" + humidity + "%");
                        tvCloud.setText("Cloud:" + cloud + "%");
                        tvWind.setText("Wind:" + wind + "km/h");

                        // lưu location vào history
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://weather-android-2ffbb-default-rtdb.asia-southeast1.firebasedatabase.app");
                        DatabaseReference myRef = database.getReference();

                        LocationHisFirebase l = new LocationHisFirebase(location, getCurrentTimestampMs());

                        myRef.child("locations").child(l.getLocation()).setValue(l.getTimestampMs());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }

    private void init(){
        etSearchLocation = findViewById(R.id.etSearchLocation);
        btnNextDay = findViewById(R.id.btnNextDay);
        btnHistory = findViewById(R.id.btnHistory);
        tvLocation = findViewById(R.id.tvLocation);
        tvCountryName = findViewById(R.id.tvCountryName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvStatus = findViewById(R.id.tvStatus);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvCloud = findViewById(R.id.tvCloud);
        tvWind = findViewById(R.id.tvWind);
        tvLocaltime = findViewById(R.id.tvLocaltime);
        imageIcon = findViewById(R.id.imageIcon);
        imageIcon.setAlpha(0.7f); // transparent

        // Khởi tạo ban đầu cho app tại 1 thành phố
        getCurrentWeatherByLocation(location);


//        etSearchLocation.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    String locationName = etSearchLocation.getText().toString();
//                    getCurrentWeatherByLocation(locationName);
//
//                    return true;
//                }
//                return false;
//            }
//        });

        // Event search theo tên thành phố
        etSearchLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String locationName = etSearchLocation.getText().toString();
                    getCurrentWeatherByLocation(locationName);
                    return true;
                }

                return false;
            }
        });

        // Event dự báo 5 ngày tiếp
        btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NextDayActivity.class);
                intent.putExtra("location", location);
                startActivity(intent);
            }
        });

        // Event xem các thành phố đã search
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            // Nếu resultCode = 200 thì hiển thị lại thông tin location mới
            // Nếu resultCode = 400 tức là chỉ bấm nút back nên không làm gì
            if(resultCode == 200){
                String l = data.getStringExtra("location");
                location = l;
                getCurrentWeatherByLocation(location);
//                Toast.makeText(this, l, Toast.LENGTH_SHORT).show();
            }else if(resultCode == 400){
//                Toast.makeText(this, "nothing", Toast.LENGTH_SHORT).show();
            }
        }
    }
}