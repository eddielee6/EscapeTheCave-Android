package me.eddielee.escapethecave;

import java.io.InputStream;
import java.util.ArrayList;

import me.eddielee.escapethecave.dataaccess.UserDetailsRepository;
import me.eddielee.escapethecave.game.EscapeTheCaveGameView;
import me.eddielee.escapethecave.game.GameBoard;
import me.eddielee.escapethecave.game.PlayerManager;
import me.eddielee.escapethecave.game.RemotePlayer;
import me.eddielee.escapethecave.network.BluetoothConnectionManager;
import me.eddielee.escapethecave.network.BluetoothMessage;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class GameActivity extends AppActivity {
	
	private EscapeTheCaveGameView escapeTheCaveGameView;
	private static BluetoothConnectionManager _bluetoothConnectionManager;
	
	private static BluetoothAdapter _bluetoothAdapter;
	private UserDetailsRepository _userDetailsRepository;
	
	private static BluetoothDevice _remotePlayer;
	private static String _mapFileName;
	
	private static Context _contextHack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_screen);
		
		_contextHack = this;
		
		_userDetailsRepository = new UserDetailsRepository(this);
		
		_bluetoothConnectionManager = new BluetoothConnectionManager(_bluetoothHandler, BLUETOOTH_UUID, BLUETOOTH_SERVICE_NAME);
		
		_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gameScreen = li.inflate(R.layout.game_screen, null);   
        escapeTheCaveGameView = (EscapeTheCaveGameView)gameScreen.findViewById(R.id.game_view);
        
        _mapFileName = getIntent().getStringExtra("mapFileName");
        int gameId = getIntent().getIntExtra("gameId", -1);
        PlayerManager playerManager = (PlayerManager)getIntent().getSerializableExtra("playerManager");
        
        ArrayList<RemotePlayer> remotePlayers = playerManager.getRemotePlayers();
		if(remotePlayers.size() == 1) {
			_remotePlayer = _bluetoothAdapter.getRemoteDevice(remotePlayers.get(0).getMacAddress());
			_bluetoothConnectionManager.connect(_bluetoothAdapter.getRemoteDevice(remotePlayers.get(0).getMacAddress()));
		}

        try {
        	AssetManager assetManager = getAssets();
	        InputStream is = assetManager.open(_mapFileName);
	        escapeTheCaveGameView.setGameBoard(new GameBoard(is));
	        escapeTheCaveGameView.setGameId(gameId);
	        escapeTheCaveGameView.setMathSkill(_userDetailsRepository.GetSkillLevelForUser(USER_ID));
	        escapeTheCaveGameView.setPlayerManager(playerManager);
	        escapeTheCaveGameView.setIsRunning(true);
        } catch (Exception e) {
        	Log.e("GameActivity", "Failed to start game: " + e.getLocalizedMessage());
        	finish();
        }
	}
	
	private static final Handler _bluetoothHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
        	switch(message.what) {
        	case BluetoothConnectionManager.ACTION_STATE_CHANGE:
        		if(message.arg1 == BluetoothConnectionManager.STATE_CONNECTED) {
        			Log.i("GameActivity:Handler", "Connected");
        			BluetoothMessage messageToSend = new BluetoothMessage(_remotePlayer.getAddress());
        			messageToSend.setSenderMacAddress(_bluetoothAdapter.getAddress());
        			messageToSend.setMessageType(BluetoothMessage.GAME_STARTED);
        			_bluetoothConnectionManager.write(messageToSend.serialize());
        		}
        		break;
        	case BluetoothConnectionManager.ACTION_MESSAGE_RECEIVED:
        		BluetoothMessage receivedMessage = BluetoothMessage.deserialize((byte[])message.obj);
        		if(receivedMessage.getMessageType().equals(BluetoothMessage.GAME_CHAT)) {
        			Log.i("GameActivity:Handler", receivedMessage.getArg1() + ": " + receivedMessage.getArg2());
        			Toast.makeText(_contextHack, receivedMessage.getArg1() + ": " + receivedMessage.getArg2(), Toast.LENGTH_SHORT).show();
        		}
                Log.i("GameActivity:Handler", "Received Message: " + receivedMessage.getMessageType());
        		break;
        	case BluetoothConnectionManager.ACTION_MESSAGE_SENT:
        		Log.i("GameActivity:Handler", "Sent Message");
        		break;
        	case BluetoothConnectionManager.ACTION_CONNECTION_FAILED:
        		Log.i("GameActivity:Handler", "Connection Failed");
        		break;
        	case BluetoothConnectionManager.ACTION_CONNECTION_LOST:
        		Log.i("GameActivity:Handler", "Connection Lost");
        		break;
        	} 
        }
    };
	
	@Override
	public void onBackPressed() {
		Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
		mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mainMenuIntent);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		super.onBackPressed();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		escapeTheCaveGameView.onResume();
	}
	
	@Override
	protected void onPause() {
		escapeTheCaveGameView.onPause();
		super.onPause();
	}
}
