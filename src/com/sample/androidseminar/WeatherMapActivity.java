package com.sample.androidseminar;

import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class WeatherMapActivity extends MapActivity {
	@SuppressWarnings("unused")
	private final String TAG = "WeatherMapActivity";
	public static final int MENU_ID_ABOUT = Menu.FIRST;
	public static final int MENU_ID_SETTING = Menu.FIRST + 1;
	public static final int MENU_ID_QUIT = Menu.FIRST + 2;

	
	// 地図の初期値
	private static final int DEFAULT_ZOOM_LEVEL = 7;
	private static final int DEFAULT_LATITUDE = 35156807;
	private static final int DEFAULT_LONGITUDE = 136925412;

	enum IntentKey {
		GEO_LATIUDE, GEO_LONGITUDE
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		MapView mapView = (MapView) findViewById(R.id.map);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);

		// 位置とズームレベルの初期状態を設定する
		MapController controller = mapView.getController();
		GeoPoint point = new GeoPoint(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
		controller.setCenter(point);
		controller.setZoom(DEFAULT_ZOOM_LEVEL);

		ConcreateOverlay concreateOverlay = new ConcreateOverlay();
		List<Overlay> overlayList = mapView.getOverlays();
		overlayList.add(concreateOverlay);
	}

	private class ConcreateOverlay extends Overlay {
		private GeoPoint mGeoPoint;
		private final Bitmap marker;

		public ConcreateOverlay() {
			mGeoPoint = null;
			marker = BitmapFactory.decodeResource(getResources(),
					R.drawable.marker);
		}

		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
			Toast.makeText(WeatherMapActivity.this, p.toString(),
					Toast.LENGTH_SHORT).show();
			mGeoPoint = p;
			Intent intent = new Intent();
			intent.setClass(WeatherMapActivity.this, WeatherInfoActivity.class);
			intent.putExtra(IntentKey.GEO_LATIUDE.name(), p.getLatitudeE6());
			intent.putExtra(IntentKey.GEO_LONGITUDE.name(), p.getLongitudeE6());
			WeatherMapActivity.this.startActivity(intent);
			return super.onTap(p, mapView);
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			if (!shadow) {
				if (mGeoPoint != null) {
					// Mapと画面の位置を計算するオブジェクト
					Projection projection = mapView.getProjection();
					Point point = new Point();
					// ロケーションから、表示する位置を計算する
					projection.toPixels(mGeoPoint, point);
					// 表示する場所へ画像を配置する。
					canvas.drawBitmap(marker, point.x, point.y, null);
				}
			}
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, OptionSelector.MENU_ID_ABOUT, Menu.NONE, R.string.about).setIcon(
				android.R.drawable.ic_menu_info_details);
		menu.add(0, OptionSelector.MENU_ID_SETTING, Menu.NONE, R.string.preferences).setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(0, OptionSelector.MENU_ID_QUIT, Menu.NONE, R.string.quit).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ID_ABOUT:
			Intent intent = new Intent();
			intent.setClass(WeatherMapActivity.this, AboutActivity.class);
			WeatherMapActivity.this.startActivity(intent);
			break;
		case MENU_ID_SETTING:
			break;
		case MENU_ID_QUIT:
			finishWithDialog();
			break;
		}
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (e.getAction() == KeyEvent.ACTION_DOWN) {
				// 戻るボタン押下時
				finishWithDialog();
				return false;
			}
		}
		return super.dispatchKeyEvent(e);
	}

	private void finishWithDialog() {
		showDialog(DialogManager.EXIT);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return DialogManager.create(id, WeatherMapActivity.this);
	}
}