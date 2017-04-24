package com.olegsmirnov.codalinefinalproject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

class WeatherData {

    private List<WeatherList> list;

    public List<WeatherList> getWeather() {
        return list;
    }

    static class WeatherList implements Parcelable {

        private Temperature temp;

        private ArrayList<Weather> weather;

        @SerializedName("dt")
        private long date;

        @SerializedName("speed")
        private double windSpeed;

        WeatherList(Parcel in) {
            weather = in.readArrayList(Weather.class.getClassLoader());
            temp = in.readParcelable(Temperature.class.getClassLoader());
            date = in.readLong();
            windSpeed = in.readDouble();
        }

        public static final Creator<WeatherList> CREATOR = new Creator<WeatherList>() {
            @Override
            public WeatherList createFromParcel(Parcel in) {
                return new WeatherList(in);
            }

            @Override
            public WeatherList[] newArray(int size) {
                return new WeatherList[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeList(weather);
            parcel.writeParcelable(temp, 0);
            parcel.writeLong(date);
            parcel.writeDouble(windSpeed);
        }

        public Temperature getTemperature() {
            return temp;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public long getDate() {
            return date;
        }

        public int getWindSpeed() {
            return (int) windSpeed;
        }

        static class Temperature implements Parcelable {

            @SerializedName("morn")
            private double tempMorning;

            @SerializedName("day")
            private double tempDay;

            @SerializedName("eve")
            private double tempEven;

            @SerializedName("night")
            private double tempNight;

            protected Temperature(Parcel in) {
                tempMorning = in.readDouble();
                tempDay = in.readDouble();
                tempEven = in.readDouble();
                tempNight = in.readDouble();
            }

            public static final Creator<Temperature> CREATOR = new Creator<Temperature>() {
                @Override
                public Temperature createFromParcel(Parcel in) {
                    return new Temperature(in);
                }

                @Override
                public Temperature[] newArray(int size) {
                    return new Temperature[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeDouble(tempMorning);
                parcel.writeDouble(tempDay);
                parcel.writeDouble(tempEven);
                parcel.writeDouble(tempNight);
            }

            public int getTempMorning() {
                return (int) tempMorning;
            }

            public int getTempDay() {
                return (int) tempDay;
            }

            public int getTempEven() {
                return (int) tempEven;
            }

            public int getTempNight() {
                return (int) tempNight;
            }

        }

        static class Weather implements Parcelable {

            private String description;

            private String icon;

            protected Weather(Parcel in) {
                description = in.readString();
                icon = in.readString();
            }

            public static final Creator<Weather> CREATOR = new Creator<Weather>() {
                @Override
                public Weather createFromParcel(Parcel in) {
                    return new Weather(in);
                }

                @Override
                public Weather[] newArray(int size) {
                    return new Weather[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeString(description);
                parcel.writeString(icon);
            }

            public String getDescription() {
                return description;
            }

            public String getIcon() {
                return icon;
            }

        }
    }
}