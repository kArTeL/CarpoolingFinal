package Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertUtil {
	public static void showDialog(String message, final Activity activity) {
		 final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

	        builder.setMessage(message);
	        builder.setIcon(android.R.drawable.ic_dialog_alert);
	        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {

	            }
	        });
		activity.runOnUiThread(new Runnable() {
			  public void run() {
				  builder.create().show();
			  }
			});
		
		
	      
	}
	

}
