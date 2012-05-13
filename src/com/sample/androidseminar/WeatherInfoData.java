package com.sample.androidseminar;

import android.graphics.Bitmap;

public class WeatherInfoData {
	private String forecast_date = null;
	private String day_of_week = null;
	private String low = null;
	private String high = null;
	private String condition = null;
	private Bitmap weather_icon = null;

	public String getDayOfWeek() {
		return day_of_week;
	}

	public void setDayOfWeek(String day_of_week) {
		this.day_of_week = day_of_week;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Bitmap getWeatherIcon() {
		return weather_icon;
	}

	public void setWeatherIcon(Bitmap weather_icon) {
		this.weather_icon = weather_icon;
	}

	public String getForecastDate() {
		return forecast_date;
	}

	public void setForecastDate(String forecast_date) {
		this.forecast_date = forecast_date;
	}
}
