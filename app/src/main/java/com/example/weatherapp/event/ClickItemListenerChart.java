package com.example.weatherapp.event;

import android.view.View;

import com.example.weatherapp.adapter.ForecastAdapter;

public interface ClickItemListenerChart {
    public ForecastAdapter.ForecastViewHolder onItemClick(View view, int position,
                            ForecastAdapter.ForecastViewHolder selectedHolder, ForecastAdapter.ForecastViewHolder viewHolder);
}
