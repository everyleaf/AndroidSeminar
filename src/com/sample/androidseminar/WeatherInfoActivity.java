package com.sample.androidseminar;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sample.androidseminar.util.HttpClient;

public class WeatherInfoActivity extends Activity {
	private static String TAG = "WeatherInfoActivity";
	String location = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.weather_info);
		// プログレスオン
		setProgressBarIndeterminateVisibility(true);

		Intent i = getIntent();
		int lat = i.getIntExtra(
				WeatherMapActivity.IntentKey.GEO_LATIUDE.name(), -1);
		int lon = i.getIntExtra(
				WeatherMapActivity.IntentKey.GEO_LONGITUDE.name(), -1);
		Log.d(TAG, "lat:" + lat + ", lon:" + lon);

		location = getLocation(lat, lon);

		new AsyncTask<String, Void, ArrayList<WeatherInfoData>>() {
			@Override
			protected ArrayList<WeatherInfoData> doInBackground(String... arg0) {
				return getWeatherXML(arg0[0]);// googleWeatherAPI解析
			}

			@Override
			protected void onPostExecute(ArrayList<WeatherInfoData> result) {
				int groupIds[] = { R.id.group1, R.id.group2, R.id.group3,
						R.id.group4 };
				// ロケーション
				TextView locationView = (TextView) findViewById(R.id.location);
				locationView.setText(WeatherInfoActivity.this.location);

				for (int i = 0; i < result.size(); i++) {
					WeatherInfoData data = (WeatherInfoData) result.get(i);
					if (data.getDayOfWeek() != null) {
						LinearLayout ll = (LinearLayout) findViewById(groupIds[i]);
						// 日付
						TextView tv = (TextView) ll.findViewById(R.id.date);
						if (data.getForecastDate() != null) {
							tv.setText(data.getForecastDate() + "("
									+ data.getDayOfWeek() + ")");
						} else {
							tv.setText(data.getDayOfWeek());
						}

						// 天気画像
						ImageView iv = (ImageView) ll
								.findViewById(R.id.weather_img);
						iv.setImageBitmap(data.getWeatherIcon());
						iv.invalidate();

						// 天気情報
						tv = (TextView) ll.findViewById(R.id.condition);
						tv.setText(data.getCondition());

						// 最高気温
						tv = (TextView) ll.findViewById(R.id.low);
						tv.setText(getString(R.string.maximum_temperature)
								+ ":" + data.getHigh());

						// 最低気温
						tv = (TextView) ll.findViewById(R.id.high);
						tv.setText(getString(R.string.minimum_temperature)
								+ ":" + data.getLow());
					}

				}
				// プログレスオフ
				setProgressBarIndeterminateVisibility(false);
			};
		}.execute(getWeatherHTML(lat, lon));
	}

	private String getLocation(int lat, int lon) {
		String country = "";
		String admin = "";
		String locality = "";

		Geocoder mGeocoder = new Geocoder(this, Locale.JAPAN);
		try {
			List<Address> addressList = mGeocoder.getFromLocation(lat / 1E6,
					lon / 1E6, 5);
			for (Iterator<Address> it = addressList.iterator(); it.hasNext();) {
				Address address = it.next();
				if (address.getCountryName() != null)
					country = address.getCountryName();
				if (address.getAdminArea() != null)
					admin = address.getAdminArea();
				if (address.getLocality() != null)
					locality = address.getLocality();
				Log.d(TAG, country + admin + locality);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return country + admin + locality;
	}

	private String getWeatherHTML(int lat, int lon) {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("0");
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
		return "http://www.google.com/ig/api?weather=,,," + lat + "," + lon
				+ "&hl=ja";
	}

	private ArrayList<WeatherInfoData> getWeatherXML(String url) {
		byte[] xml_byte = null;
		try {
			xml_byte = HttpClient.getByteArrayFromURL(url);
		} catch (Exception e) {
		}

		if (xml_byte == null)
			return null;
		return parseXml(xml_byte);
	}

	private ArrayList<WeatherInfoData> parseXml(byte[] xml_byte) {
		XmlPullParser parser = Xml.newPullParser();
		String weather_icon_url = null;
		ArrayList<WeatherInfoData> weatherInfoDatas = new ArrayList<WeatherInfoData>();

		try {
			parser.setInput(new StringReader(new String(xml_byte, "Shift_JIS")));
			int eventType = parser.getEventType();
			boolean isInForecastConditions = false;
			WeatherInfoData data = new WeatherInfoData();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("forecast_conditions")) {
						isInForecastConditions = true;
					}
					// 日付
					if (parser.getName().equals("forecast_date")) {
						data.setForecastDate(parser.getAttributeValue(null,
								"data"));
					}

					if (isInForecastConditions) {
						// day_of_weekタグ（月）
						if (parser.getName().equals("day_of_week")) {
							data.setDayOfWeek(parser.getAttributeValue(null,
									"data"));
							// lowタグ
						} else if (parser.getName().equals("low")) {
							data.setLow(parser.getAttributeValue(null, "data"));
							// highタグ
						} else if (parser.getName().equals("high")) {
							data.setHigh(parser.getAttributeValue(null, "data"));
							// iconタグ
						} else if (parser.getName().equals("icon")) {
							weather_icon_url = parser.getAttributeValue(null,
									"data");
							if (weather_icon_url != null) {
								byte[] byteArray = HttpClient
										.getByteArrayFromURL("http://www.google.com"
												+ weather_icon_url);
								data.setWeatherIcon(BitmapFactory
										.decodeByteArray(byteArray, 0,
												byteArray.length));
							}
							// conditionタグ
						} else if (parser.getName().equals("condition")) {
							data.setCondition(parser.getAttributeValue(null,
									"data"));
						}
					}

					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("forecast_conditions")) {
						weatherInfoDatas.add(data);
						isInForecastConditions = false;
						data = new WeatherInfoData();
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
		}
		return weatherInfoDatas;
	}
}
