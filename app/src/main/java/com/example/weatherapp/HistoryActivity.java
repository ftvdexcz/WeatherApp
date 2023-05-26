package com.example.weatherapp;

import static com.example.weatherapp.utils.Utils.convertLocalTimeEpoch;
import static com.example.weatherapp.utils.Utils.convertStringDoubleToStringInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.adapter.HistoryAdapter;
import com.example.weatherapp.model.Forecast;
import com.example.weatherapp.model.LocationHis;
import com.example.weatherapp.model.LocationHisFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.ClickItemListener {
    ImageView imageBack;
    RecyclerView rvHistory;
    HistoryAdapter historyAdapter;
    ArrayList<LocationHis> histories;

    ArrayList<LocationHisFirebase> locationHisFirebases;

    int completedRequests = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        init();
        Intent intent = getIntent();
        getHistory();
    }

    private void init() {
        imageBack = findViewById(R.id.imageBack2);
        rvHistory = findViewById(R.id.rvHistory);

        histories = new ArrayList<>();
        historyAdapter = new HistoryAdapter(histories);
        historyAdapter.setClickItemListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rvHistory.setAdapter(historyAdapter);
        rvHistory.setLayoutManager(linearLayoutManager);

        // Event bấm nút quay lại gd chính
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(400, i);
                finish(); // quay lại activity trước
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // k xử lý
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Xử lý khi item được vuốt sang phải thì xóa item
                if (direction == ItemTouchHelper.RIGHT) {
                    int position = viewHolder.getAdapterPosition();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://weather-android-2ffbb-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference().child("locations").child(histories.get(position).getLocation());
                    databaseReference.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    histories.remove(position);
                                    historyAdapter.notifyItemRemoved(position);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(rvHistory);
    }

    private void getHistory() {

        locationHisFirebases = new ArrayList<LocationHisFirebase>();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://weather-android-2ffbb-default-rtdb.asia-southeast1.firebasedatabase.app").
                getReference().child("locations");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    locationHisFirebases.add(new LocationHisFirebase(dataSnapshot.getKey(),
                            dataSnapshot.getValue(String.class)));
                }

                // get api và trả vào list cho adapter
                for (LocationHisFirebase l : locationHisFirebases) {
                    getCurrentWeatherByLocation(l.getLocation(), l.getTimestampMs());
                }

                // (đoạn code này k bị block ở trên gọi api async)
                Log.i("logging", "logggingg");
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void parseDataFromResponseObject(String response, String timestampMs) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONObject locationObj = new JSONObject(jsonObj.getString("location"));
            JSONObject currentObj = new JSONObject(jsonObj.getString("current"));

            String location = locationObj.getString("name");
            String country = locationObj.getString("country");
            String temperature = convertStringDoubleToStringInt(currentObj.getString("temp_c"));

            LocationHis l = new LocationHis(location + "--" + timestampMs, country, temperature);
            histories.add(l);
            Log.i("logging", timestampMs);
            completedRequests++;

            if(completedRequests == locationHisFirebases.size()){
                // sort theo thời gian tìm kiếm location
                Collections.sort(histories, new Comparator<LocationHis>() {
                    @Override
                    public int compare(LocationHis o1, LocationHis o2) {
                        long l1 = Long.valueOf(o1.getLocation().split("--")[1]);
                        long l2 = Long.valueOf(o2.getLocation().split("--")[1]);
                        return Long.compare(l2, l1);
                    }
                });

                for(LocationHis lh: histories){
                    lh.setLocation(lh.getLocation().split("--")[0]);
                }

                historyAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void getCurrentWeatherByLocation(String locationInput, String timestampMs) {
        String apiKey = "398a6a2569394f8695e170734232205";
        String url = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + locationInput;

        RequestQueue requestQueue = Volley.newRequestQueue(HistoryActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseDataFromResponseObject(response, timestampMs);
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
    public void onItemClick(View view, int position) {
        LocationHis l = histories.get(position);
        Intent i = new Intent();
        i.putExtra("location", l.getLocation());
        setResult(200, i);
        finish();
    }
}