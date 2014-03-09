package me.eddielee.escapethecave;

import me.eddielee.escapethecave.dataaccess.UserDetailsRepository;
import me.eddielee.escapethecave.network.BluetoothConnectionManager;
import me.eddielee.escapethecave.network.BluetoothMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class JoinGameActivity extends AppActivity {
	
	private static final int SET_DEVICE_DISCOVERABLE = 1;
	
	private ProgressBar gameInviteWaitingSpinner;
	private ImageView joinGameErrorIcon;
	private Button makeDeviceDiscoverableButton;
	private TextView loadingMessage;
	private LinearLayout gameDetailsSection;
	
	private static AlertDialog _joinGameDialog;
	
	private static UserDetailsRepository _userDetailsRepository;
	
	private static BluetoothConnectionManager _bluetoothConnectionManager;
	private static BluetoothAdapter _bluetoothAdapter;
	private boolean _userSaidNo = false;
	
	private static Context _contextHack;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_game_screen);
        
        _userDetailsRepository = new UserDetailsRepository(this);
        
        gameInviteWaitingSpinner = (ProgressBar)findViewById(R.id.joinGameWaitingSpinner);
    	joinGameErrorIcon = (ImageView)findViewById(R.id.joinGameErrorIcon);
    	makeDeviceDiscoverableButton = (Button)findViewById(R.id.makeDeviceDiscoverableButton);
    	loadingMessage = (TextView)findViewById(R.id.loading_message);
    	gameDetailsSection = (LinearLayout)findViewById(R.id.gameDetailsSection);
    	gameDetailsSection.setVisibility(View.GONE);
        
    	_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	
    	_bluetoothConnectionManager = new BluetoothConnectionManager(_bluetoothHandler, BLUETOOTH_UUID, BLUETOOTH_SERVICE_NAME);
    	
    	_contextHack = this;
    	
    	setupGameRequestReceivedDialog();
    }
    
	@Override
	public void onResume() {
		super.onResume();
		if(!_userSaidNo) {
			startAdvertising();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		stopAdvertising();
	}
	
	private static Handler _bluetoothHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
        	switch(message.what) {
        	case BluetoothConnectionManager.ACTION_STATE_CHANGE:
        		Log.i("JoinGameActivity:Handler", "State changed: " + message.arg1);
        		break;
        	case BluetoothConnectionManager.ACTION_MESSAGE_RECEIVED:
        		BluetoothMessage receivedMessage = BluetoothMessage.deserialize((byte[])message.obj);
                Log.i("JoinGameActivity:Handler", "Received Message: " + receivedMessage.getMessageType());
                if(receivedMessage.getMessageType().equals(BluetoothMessage.CONNECTION_REQUEST)) {
                	_joinGameDialog.setMessage("You have received a game request from " + receivedMessage.getArg1());
                	_joinGameDialog.show();
                }
                if(receivedMessage.getMessageType().equals(BluetoothMessage.GAME_STARTED)) {
                	Intent intent = new Intent(_contextHack, RemoteGameActivity.class);
                	intent.putExtra("macAddress", receivedMessage.getSenderMacAddress());
                	intent.putExtra("mapFileName", receivedMessage.getArg1());
                	_contextHack.startActivity(intent);
                }
                Log.i("JoinGameActivity:Handler", message.obj.toString());
        		break;
        	case BluetoothConnectionManager.ACTION_MESSAGE_SENT:
        		Log.i("JoinGameActivity:Handler", "Sent Message");
        		break;
        	case BluetoothConnectionManager.ACTION_CONNECTION_FAILED:
        		Log.i("JoinGameActivity:Handler", "Connection Failed");
        		break;
        	case BluetoothConnectionManager.ACTION_CONNECTION_LOST:
        		Log.i("JoinGameActivity:Handler", "Connection Lost");
        		break;
        	} 
        }
    };
	
	public void startAdvertising() {
		if(_bluetoothAdapter != null) {
			setScreenStateWaiting();
			if(_bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
		    	Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				startActivityForResult(discoverableIntent, SET_DEVICE_DISCOVERABLE);
	    	} else {
	    		_bluetoothConnectionManager.start();
	    	}
		} else {
			setScreenStateError();
		}
	}
	
	public void stopAdvertising() {
		_bluetoothConnectionManager.stop();
	}
    
    public void makeDeviceDiscoverableButtonClicked(View view) {
    	startAdvertising();
    }
    
    private void setupGameRequestReceivedDialog() {
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    	alertDialog.setTitle("Join game?");
    	alertDialog.setMessage("You have received a game request.");

    	alertDialog.setPositiveButton("Accept request", new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int whichButton) {
	        	BluetoothMessage message = new BluetoothMessage(_bluetoothAdapter.getAddress());
	        	message.setMessageType(BluetoothMessage.CONNECTION_REQUEST_ACCEPTED);
//	        	Bitmap displayPicture;
//	        	File displayPictureFile = new File(getFilesDir(), DISPLAY_PICTURE_FILE_NAME);
//	        	if(displayPictureFile.exists()) {
//	        		String displayPicturePath = getFilesDir() + "/" + DISPLAY_PICTURE_FILE_NAME;
//	            	displayPicture = BitmapFactory.decodeFile(displayPicturePath);
//	        	} else {
//	        		InputStream is = getResources().openRawResource(R.raw.display_picture_1);
//	        		displayPicture = BitmapFactory.decodeStream(is);
//	        	}
	        	message.setArg1(_userDetailsRepository.GetNameForUser(USER_ID));
//	        	ByteArrayOutputStream stream = new ByteArrayOutputStream();
//	        	displayPicture.compress(Bitmap.CompressFormat.JPEG, 1, stream);
//	        	byte[] byteArray = stream.toByteArray();
//	        	message.setObject(byteArray);
	        	_bluetoothConnectionManager.write(message.serialize());
        		dialog.cancel();
        		setScreenStateGameStarting();
        	}
        });

    	alertDialog.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
    		@Override
			public void onClick(DialogInterface dialog, int whichButton) {
    			BluetoothMessage message = new BluetoothMessage(_bluetoothAdapter.getAddress());
	        	message.setMessageType(BluetoothMessage.CONNECTION_REQUEST_REFUSED);
	        	_bluetoothConnectionManager.write(message.serialize());
    			dialog.cancel();
    		}
        });
    	_joinGameDialog = alertDialog.create();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == SET_DEVICE_DISCOVERABLE) {
			if(resultCode == Activity.RESULT_CANCELED) {
				_userSaidNo = true;
				setScreenStateError();
			} else {
				_userSaidNo = false;
				startAdvertising();
			}
		}
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void setScreenStateGameStarting() {
    	gameInviteWaitingSpinner.setVisibility(View.VISIBLE);
		joinGameErrorIcon.setVisibility(View.GONE);
		makeDeviceDiscoverableButton.setVisibility(View.GONE);
		loadingMessage.setText(R.string.join_game_waiting_start);
    }
    
    private void setScreenStateWaiting() {
		gameInviteWaitingSpinner.setVisibility(View.VISIBLE);
		joinGameErrorIcon.setVisibility(View.GONE);
		makeDeviceDiscoverableButton.setVisibility(View.GONE);
		loadingMessage.setText(R.string.join_game_waiting_invites);
    }
    
    private void setScreenStateError() {
		gameInviteWaitingSpinner.setVisibility(View.GONE);
		joinGameErrorIcon.setVisibility(View.VISIBLE);
		makeDeviceDiscoverableButton.setVisibility(View.VISIBLE);
		loadingMessage.setText(R.string.join_game_error);
    }
}