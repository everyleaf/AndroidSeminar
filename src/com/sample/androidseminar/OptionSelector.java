package com.sample.androidseminar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class OptionSelector {
	public static final int MENU_ID_ABOUT = Menu.FIRST;
	public static final int MENU_ID_SETTING = Menu.FIRST + 1;
	public static final int MENU_ID_QUIT = Menu.FIRST + 2;

	public static boolean select(MenuItem item, Activity activity) {
		switch (item.getItemId()) {
		case MENU_ID_ABOUT:
			Intent intent = new Intent();
			intent.setClass(activity, AboutActivity.class);
			activity.startActivity(intent);
			break;
		case MENU_ID_SETTING:
			break;
		case MENU_ID_QUIT:
			finish(activity);
//			finishWithDialog();
			break;
		}
		return false;
	}

	private static void finish(Activity activity) {
		Method method;
		try {
			method = activity.getClass().getMethod("finishWithDialog", new Class[]{});
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		@SuppressWarnings("unused")
		Object ret; //戻り値
		try {
			ret = method.invoke(activity, new Object[]{});
			//  = object.メソッド名(); と同じ
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}		
	}
}
