package carpooling.gui;

import java.util.logging.Level;

import Util.AlertUtil;
import jade.core.MicroRuntime;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.O2AException;
import jade.wrapper.StaleProxyException;

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
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import carpooling.agent.CarInterface;

/**
 * This activity implement the chat interface.
 * 
 * @authors
 */

public class DriverActivity extends AppCompatActivity implements CarResponse {
	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	int ORIGIN_PICKER_REQUEST = 0;
	int DESTINY_PICKER_REQUEST = 1;

    
	private String nickname;
	private CarInterface carInterface;

	EditText destinyEditText;
	EditText originEditText;
	EditText departureTimeEditText;
	EditText arrivalTimeEditText;
	EditText priceEditText;
	EditText freeSeatsEditText;


	//String message = destinyField.getText().toString();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			nickname = extras.getString("nickname");
		}

		try {
			carInterface = MicroRuntime.getAgent(nickname)
					.getO2AInterface(CarInterface.class);
			if (carInterface == null)
			{
				AgentController controller = MicroRuntime.getAgent(nickname);
				logger.println("skljfsf");
				
			}
		} catch (StaleProxyException e) {
			AlertUtil.showDialog(getString(R.string.msg_interface_exc),this);
			//showAlertDialog(getString(R.string.msg_interface_exc), true);
		} catch (ControllerException e) {
			AlertUtil.showDialog(getString(R.string.msg_interface_exc),this);
			//showAlertDialog(getString(R.string.msg_controller_exc), true);
		}

		carInterface.setDelegate(this);

		setContentView(R.layout.driver);
		this.initUI();
		Button button = (Button) findViewById(R.id.button_send);
		button.setOnClickListener(buttonSendListener);
		//getActionBar().show();
	}

	private void initUI()
	{
		destinyEditText =  (EditText) findViewById(R.id.editDestiny);
		originEditText =  (EditText) findViewById(R.id.editOrigin);
		departureTimeEditText =  (EditText) findViewById(R.id.editDeparture);
		arrivalTimeEditText =  (EditText) findViewById(R.id.editArrival);
		priceEditText =  (EditText) findViewById(R.id.editPrice);
		freeSeatsEditText = (EditText) findViewById(R.id.editFreeSeats);
		originEditText.setOnClickListener(buttonOriginLocation);
		destinyEditText.setOnClickListener(buttonDestinyLocation);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		logger.log(Level.INFO, "Destroy activity!");
	}


	private OnClickListener buttonOriginLocation = new OnClickListener() {
		public void onClick(View v) {

			DriverActivity.this.presentLocationPicker(ORIGIN_PICKER_REQUEST);
		}
	};
	private OnClickListener buttonDestinyLocation = new OnClickListener() {
		public void onClick(View v) {

			DriverActivity.this.presentLocationPicker(DESTINY_PICKER_REQUEST);
		}
	};
	private OnClickListener buttonSendListener = new OnClickListener() {
		public void onClick(View v) {
			if (DriverActivity.this.validateData() == true)
			{
				
				try {
					carInterface.addRide(DriverActivity.this.originEditText.getText().toString(),
							DriverActivity.this.destinyEditText.getText().toString(),
							DriverActivity.this.departureTimeEditText.getText().toString(),
							DriverActivity.this.arrivalTimeEditText.getText().toString(),
							Integer.parseInt(DriverActivity.this.freeSeatsEditText.getText().toString()), 
							Integer.parseInt(DriverActivity.this.priceEditText.getText().toString()));
				} catch (O2AException e) {
					AlertUtil.showDialog(e.getMessage(), DriverActivity.this);
				}
			}

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

	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}



	@Override
	public void onCarStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdateRides(String msg) {
		// TODO Auto-generated method stub
		AlertUtil.showDialog(msg,this);
	}

	@Override
	public void onCarResponse(String msg) {
		// TODO Auto-generated method stub
		AlertUtil.showDialog(msg,this);
	}
	
	public boolean validateData()
	{
		boolean returnValue = true;
		if (this.destinyEditText.getText().toString().isEmpty())
		{
			AlertUtil.showDialog("Por favor ingrese el lugar de destino",this);
			returnValue = false;
		}
		else if (this.originEditText.getText().toString().isEmpty())
		{
			AlertUtil.showDialog("Por favor ingrese el lugar de origen",this);
			returnValue = false;
		}
		else if (this.departureTimeEditText.getText().toString().isEmpty())
		{
			AlertUtil.showDialog("Por favor ingrese la hora de partida",this);
			returnValue = false;
		}
		else if (this.arrivalTimeEditText.getText().toString().isEmpty())
		{
			AlertUtil.showDialog("Por favor ingrese la hora de llegada",this);
			returnValue = false;
		}
		else if (this.priceEditText.getText().toString().isEmpty())
		{
			AlertUtil.showDialog("Por favor ingrese el precio",this);
			returnValue = false;
		}
		else if (this.freeSeatsEditText.getText().toString().isEmpty())
		{
			AlertUtil.showDialog("Por favor ingrese los asientos disponibles",this);
			returnValue = false;
		}
		return returnValue;
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
