/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

package carpooling.gui;

import java.util.logging.Level;

import Util.AlertUtil;
import jade.core.MicroRuntime;
import jade.util.Logger;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
//import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import carpooling.agent.PassengerInterface;


public class PassengerActivity extends AppCompatActivity implements PassengerResponse {
	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	int ORIGIN_PICKER_REQUEST = 0;
	int DESTINY_PICKER_REQUEST = 1;

	EditText destinyEditText;
	EditText originEditText;
	EditText arrivalTimeEditText;
	Button button;
	private String nickname;
	private PassengerInterface passengerInterface;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			nickname = extras.getString("nickname");
		}

		try {
			passengerInterface = MicroRuntime.getAgent(nickname)
					.getO2AInterface(PassengerInterface.class);
		} catch (StaleProxyException e) {
			AlertUtil.showDialog(getString(R.string.msg_controller_exc), this);
		} catch (ControllerException e) {
			
			AlertUtil.showDialog(getString(R.string.msg_controller_exc), this);
		}

		passengerInterface.setDelegate(this);
		setContentView(R.layout.passenger);

		this.initUI();
	}

	private void initUI()
	{

		destinyEditText =  (EditText) findViewById(R.id.editDestiny);
		originEditText =  (EditText) findViewById(R.id.editOrigin);
		arrivalTimeEditText =  (EditText) findViewById(R.id.editArrival);
		originEditText.setOnClickListener(buttonOriginLocation);
		destinyEditText.setOnClickListener(buttonDestinyLocation);
		button = (Button) findViewById(R.id.button_send);
		button.setOnClickListener(buttonSendListener);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		logger.log(Level.INFO, "Destroy activity!");
	}

	private OnClickListener buttonSendListener = new OnClickListener() {
		public void onClick(View v) {
			String destiny = destinyEditText.getText().toString();
			String origin = originEditText.getText().toString();
			String arrival = arrivalTimeEditText.getText().toString();

			if (destiny != null && !destiny.equals("")) {
				try {
					 if(null == passengerInterface) {
						 
					 }
					button.setVisibility(View.INVISIBLE);
					passengerInterface.askForRide(origin,destiny,arrival);
				} catch (Exception e) {
					AlertUtil.showDialog(e.getMessage(),PassengerActivity.this);
				}
			}

		}
	};
	private OnClickListener buttonOriginLocation = new OnClickListener() {
		public void onClick(View v) {

			PassengerActivity.this.presentLocationPicker(ORIGIN_PICKER_REQUEST);
		}
	};
	private OnClickListener buttonDestinyLocation = new OnClickListener() {
		public void onClick(View v) {

			PassengerActivity.this.presentLocationPicker(DESTINY_PICKER_REQUEST);
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chat_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ORIGIN_PICKER_REQUEST) {
			if (resultCode == RESULT_OK) {
				Place place = PlacePicker.getPlace(data, this);
				this.originEditText.setText(place.getName());
			}
		}
		else if (requestCode == DESTINY_PICKER_REQUEST)
		{
			if (resultCode == RESULT_OK) {
				Place place = PlacePicker.getPlace(data, this);
				this.destinyEditText.setText(place.getName());
			}
		}
	}

	/*private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			logger.log(Level.INFO, "Received intent " + action);
		}
	}*/


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onPassengerStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPassengerResponse(String msg) {
		// TODO Auto-generated method stub
//
		AlertUtil.showDialog(msg, this);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				button.setVisibility(View.VISIBLE);
			}
		});

	}
	private void presentLocationPicker(int pickerRequest)
	{

		PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

		try {
			startActivityForResult(builder.build(this), pickerRequest);
		} catch (GooglePlayServicesRepairableException e) {
			e.printStackTrace();
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
	}
}