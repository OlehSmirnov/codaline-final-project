package com.olegsmirnov.codalinefinalproject.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.olegsmirnov.codalinefinalproject.R;
import com.olegsmirnov.codalinefinalproject.entities.WeatherData;
import com.olegsmirnov.codalinefinalproject.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;

public class WeatherAdapterAuto extends RecyclerView.Adapter<WeatherAdapterAuto.MyViewHolder> {

    private ArrayList<WeatherData.WeatherList> mList;

    private Context context;

    private SharedPreferences sharedPreferences;

    private String BASE_IMG_URL;

    public WeatherAdapterAuto(ArrayList<WeatherData.WeatherList> mList, Context context) {
        this.mList = new ArrayList<>(mList);
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        BASE_IMG_URL = context.getString(R.string.base_img_url);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_auto, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        bindHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_auto_item_date)
        TextView tvDate;

        @BindView(R.id.rv_auto_item_icon)
        ImageView ivIcon;

        @BindView(R.id.rv_auto_item_temp)
        TextView tvTemp;

        @BindView(R.id.rv_auto_item_wind)
        TextView tvWind;

        MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.rv_auto_item_date);
            ivIcon = (ImageView) view.findViewById(R.id.rv_auto_item_icon);
            tvTemp = (TextView) view.findViewById(R.id.rv_auto_item_temp);
            tvWind = (TextView) view.findViewById(R.id.rv_auto_item_wind);
        }
    }

    private void bindHolder(MyViewHolder holder, int position) {
        Glide.with(context).load(BASE_IMG_URL + (mList.get(position).getWeather().get(0).getIcon()) + ".png").into(holder.ivIcon);
        int temp = mList.get(position).getTemperature().getTempDay();
        String postfix = Utils.detectUnitsPostfix(sharedPreferences.getString(context.getString(R.string.prefs_units), "metric"));
        if (temp > 0) {
            holder.tvTemp.setText("+" + String.valueOf(temp) + postfix);
        }
        else {
            holder.tvTemp.setText(String.valueOf(temp) + postfix);
        }
        String formattedDate = new Date(mList.get(position).getDate() * 1000).toString();
        holder.tvDate.setText(formattedDate.substring(0, 4) + formattedDate.substring(8, 10));
        holder.tvWind.setText(String.valueOf(mList.get(position).getWindSpeed()));
    }

}