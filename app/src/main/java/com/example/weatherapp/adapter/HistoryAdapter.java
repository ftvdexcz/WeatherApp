package com.example.weatherapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.model.LocationHis;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{
    private ArrayList<LocationHis> mHistory;

    private ClickItemListener clickItemListener;

    public HistoryAdapter(ArrayList<LocationHis> mHistory) {
        this.mHistory = mHistory;
    }

    public void setClickItemListener(ClickItemListener clickItemListener) {
        this.clickItemListener = clickItemListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View historyView =
                inflater.inflate(R.layout.history_item, parent, false);

        HistoryAdapter.HistoryViewHolder viewHolder = new HistoryAdapter.HistoryViewHolder(historyView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        LocationHis l = mHistory.get(position);

        holder.tvLocationHis.setText(l.getLocation());
        holder.tvCountryHis.setText(l.getCountry());
        holder.tvTempHis.setText(l.getTemperature());
    }

    @Override
    public int getItemCount() {
        return mHistory.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        private View itemView;
        public TextView tvLocationHis, tvCountryHis, tvTempHis;
        public LinearLayout layout;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView = itemView;
            layout = itemView.findViewById(R.id.historyLayout);
            tvLocationHis = itemView.findViewById(R.id.tvLocationHis);
            tvCountryHis = itemView.findViewById(R.id.tvCountryHis);
            tvTempHis = itemView.findViewById(R.id.tvTempHis);

            // Event click vào item chuyển về Main Activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickItemListener != null){
                        clickItemListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface ClickItemListener{
        public void onItemClick(View view, int position);
    }
}
