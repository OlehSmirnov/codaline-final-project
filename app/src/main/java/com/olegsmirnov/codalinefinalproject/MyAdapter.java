package com.olegsmirnov.codalinefinalproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<WeatherData.WeatherList> mList;

    private Context context;

    public MyAdapter(ArrayList<WeatherData.WeatherList> mList, Context context) {
        this.mList = new ArrayList<>(mList);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(context).load("http://openweathermap.org/img/w/" + (mList.get(position).getWeather().get(0).getIcon()) + ".png").into(holder.ivIcon);
        holder.tvTemperature.setText(String.valueOf(mList.get(position).getTemperature().getTempDay() + "°C"));
        holder.tvDate.setText(new Date(mList.get(position).getDate() * 1000).toString().substring(0, 10));
        holder.tvWindSpeed.setText(String.valueOf(mList.get(position).getWindSpeed() + "m/s"));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvWindSpeed;
        private TextView tvTemperature;
        private ImageView ivIcon;

        MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.rv_item_date);
            ivIcon = (ImageView) view.findViewById(R.id.rv_item_icon);
            tvTemperature = (TextView) view.findViewById(R.id.rv_item_temp);
            tvWindSpeed = (TextView) view.findViewById(R.id.rv_item_wind);
        }
    }

}