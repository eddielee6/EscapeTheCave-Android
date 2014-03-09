package me.eddielee.escapethecave;

import me.eddielee.escapethecave.dataaccess.UserDetailsRepository;
import me.eddielee.escapethecave.network.BluetoothConnectionManager;
import me.eddielee.escapethecave.network.BluetoothMessage;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RemoteGameActivity extends AppActivity {

	private static BluetoothConnectionManager _bluetoothConnectionManager;
	private static BluetoothAdapter _bluetoothAdapter;
	
	private UserDetailsRepository _userDetailsRepository;
	
	private BluetoothDevice _remoteGame;
	
	private static Button _sendButton;
	private static EditText _textBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_game_screen);
		
		_sendButton = (Button)findViewById(R.id.sendButton);
		_textBox = (EditText)findViewById(R.id.messageToSend);
		
		_userDetailsRepository = new UserDetailsRepository(this);
		
		_bluetoothConnectionManager = new BluetoothConnectionManager(_bluetoothHandler, BLUETOOTH_UUID, BLUETOOTH_SERVICE_NAME);
		_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		Intent receivedIntent = getIntent();
		_remoteGame = _bluetoothAdapter.getRemoteDevice(receivedIntent.getStringExtra("macAddress"));
		
		_bluetoothConnectionManager.connect(_remoteGame);
	}
	
	private static final Handler _bluetoothHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
        	switch(message.what) {
        	case BluetoothConnectionManager.ACTION_STATE_CHANGE:
        		if(message.arg1 == BluetoothConnectionManager.STATE_CONNECTED) {
        			_sendButton.setEnabled(true);
        			Log.i("RemoteGameActivity:Handler", "Connected");
        		}
        		break;
        	case BluetoothConnectionManager.ACTION_MESSAGE_RECEIVED:
        		BluetoothMessage receivedMessage = BluetoothMessage.deserialize((byte[])message.obj);
                Log.i("RemoteGameActivity:Handler", "Received Message: " + receivedMessage.getMessageType());
        		break;
        	case BluetoothConnectionManager.ACTION_MESSAGE_SENT:
        		Log.i("RemoteGameActivity:Handler", "Sent Message");
        		break;
        	case BluetoothConnectionManager.ACTION_CONNECTION_FAILED:
        		_sendButton.setEnabled(false);
        		Log.i("RemoteGameActivity:Handler", "Connection Failed");
        		break;
        	case BluetoothConnectionManager.ACTION_CONNECTION_LOST:
        		_sendButton.setEnabled(false);
        		Log.i("RemoteGameActivity:Handler", "Connection Lost");
        		break;
        	} 
        }
    };
    
    public void sendMessage(View view) {
    	String message = _textBox.getText().toString();
    	_textBox.setText("");
    	BluetoothMessage messageToSend = new BluetoothMessage(_remoteGame.getAddress());
		messageToSend.setSenderMacAddress(_bluetoothAdapter.getAddress());
		messageToSend.setArg1(_userDetailsRepository.GetNameForUser(USER_ID));
		messageToSend.setArg2(message);
		messageToSend.setMessageType(BluetoothMessage.GAME_CHAT);
		_bluetoothConnectionManager.write(messageToSend.serialize());
    }
	
	@Override
	public void onBackPressed() {
		Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
		mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mainMenuIntent);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		super.onBackPressed();
	}
}
