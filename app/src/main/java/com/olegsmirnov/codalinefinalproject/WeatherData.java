package com.olegsmirnov.codalinefinalproject;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class WeatherData {

    private List<WeatherList> list;

    public List<WeatherList> getWeather() {
        return list;
    }

    public void setWeather(List<WeatherList> weather) {
        this.list = weather;
    }

    class WeatherList {

        private Temperature temp;

        private List<Weather> weather;

        @SerializedName("dt")
        private long date;

        @SerializedName("speed")
        private double windSpeed;

        public Temperature getTemperature() {
            return temp;
        }

        public void setTemperature(Temperature temperature) {
            this.temp = temperature;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public void setWeather(List<Weather> weather) {
            this.weather = weather;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public int getWindSpeed() {
            return (int) windSpeed;
        }

        public void setWindSpeed(double windSpeed) {
            this.windSpeed = windSpeed;
        }

        class Temperature {

            @SerializedName("morn")
            private double tempMorning;

            @SerializedName("day")
            private double tempDay;

            @SerializedName("eve")
            private double tempEven;

            @SerializedName("night")
            private double tempNight;

            public int getTempMorning() {
                return (int) tempMorning;
            }

            public void setTempMorning(double tempMorning) {
                this.tempMorning = tempMorning;
            }

            public int getTempDay() {
                return (int) tempDay;
            }

            public void setTempDay(double tempDay) {
                this.tempDay = tempDay;
            }

            public int getTempEven() {
                return (int) tempEven;
            }

            public void setTempEven(double tempEven) {
                this.tempEven = tempEven;
            }

            public int getTempNight() {
                return (int) tempNight;
            }

            public void setTempNight(double tempNight) {
                this.tempNight = tempNight;
            }
        }

        class Weather {

            private String description;

            private String icon;

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }
        }
    }
}