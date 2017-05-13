package com.olegsmirnov.codalinefinalproject.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class WeatherData {

    private List<WeatherList> list;

    public List<WeatherList> getWeather() {
        return list;
    }

    private City city;

    public City getCity() {
        return city;
    }

    public static class City {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class WeatherList implements Parcelable {

        private Temperature temp;

        private ArrayList<Weather> weather;

        @SerializedName("dt")
        private long date;

        @SerializedName("speed")
        private double windSpeed;

        @SerializedName("deg")
        private double windDegree;

        private double pressure;

        WeatherList(Parcel in) {
            weather = in.readArrayList(Weather.class.getClassLoader());
            temp = in.readParcelable(Temperature.class.getClassLoader());
            date = in.readLong();
            windSpeed = in.readDouble();
            pressure = in.readDouble();
            windDegree = in.readDouble();
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
            parcel.writeDouble(pressure);
            parcel.writeDouble(windDegree);
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

        public int getWindDegree() {
            return (int) windDegree;
        }

        public int getPressure() {
            return (int) pressure;
        }

        public int getWindSpeed() {
            return (int) windSpeed;
        }

        public static class Temperature implements Parcelable {

            @SerializedName("day")
            private double tempDay;

            Temperature(Parcel in) {
                tempDay = in.readDouble();
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
                parcel.writeDouble(tempDay);
            }

            public int getTempDay() {
                return (int) tempDay;
            }

        }

        public static class Weather implements Parcelable {

            private String description;

            private String icon;

            Weather(Parcel in) {
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