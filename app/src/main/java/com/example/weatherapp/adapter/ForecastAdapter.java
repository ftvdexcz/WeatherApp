package com.example.weatherapp.adapter;

import static com.example.weatherapp.utils.Utils.formatDate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.model.Forecast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private ArrayList<Forecast> mForecast;
    private Context mContext;

    public ForecastAdapter(ArrayList<Forecast> mForecast, Context mContext) {
        this.mForecast = mForecast;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View forecastView =
                inflater.inflate(R.layout.forecast_item, parent, false);

        ForecastViewHolder viewHolder = new ForecastViewHolder(forecastView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        Forecast f = mForecast.get(position);

        if(position == 0){
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.rounded);
            holder.layout.setBackground(drawable);
        }

        holder.date.setText(formatDate(f.getDate()));
        holder.status.setText(f.getStatus());
        holder.maxTemp.setText(f.getMaxTemp() + "°");
        holder.minTemp.setText(f.getMinTemp() + "°");
        holder.wind.setText("💨"+ f.getWind() + " km/h");
        Picasso.get().load("https:" + f.getImageUrl()).fit().into(holder.imageIcon);
    }

    @Override
    public int getItemCount() {
        return mForecast.size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder{
        private View itemView;
        public TextView date, status, maxTemp, minTemp, wind;
        public ImageView imageIcon;

        public LinearLayout layout;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView = itemView;
            layout = itemView.findViewById(R.id.forecastLayout);
            date = itemView.findViewById(R.id.tvDateFC);
            status = itemView.findViewById(R.id.tvStatusFC);
            maxTemp = itemView.findViewById(R.id.tvMaxTempFC);
            minTemp = itemView.findViewById(R.id.tvMinTempFC);
            wind = itemView.findViewById(R.id.tvWindFC);
            imageIcon = itemView.findViewById(R.id.imageIconFC);
        }
    }
}
