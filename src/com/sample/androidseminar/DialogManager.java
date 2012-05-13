package com.sample.androidseminar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class DialogManager {
	public static final int EXIT = 0;

	static public Dialog create(int id, final Activity activity){
		AlertDialog dialog = null;
		switch (id) {
		case EXIT:
			dialog = new AlertDialog.Builder(activity)
					.setMessage(R.string.exit_message)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									activity.finish();
								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).create();
			break;
		}
		return dialog;
	}
}
