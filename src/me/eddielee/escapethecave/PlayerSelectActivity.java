package me.eddielee.escapethecave;

import java.util.ArrayList;

import me.eddielee.escapethecave.dataaccess.UserDetailsRepository;
import me.eddielee.escapethecave.network.BluetoothConnectionManager;
import me.eddielee.escapethecave.network.BluetoothMessage;
import me.eddielee.escapethecave.shared.RemoteDeviceArrayAdapter;
import me.eddielee.escapethecave.shared.RemoteDeviceViewModel;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class PlayerSelectActivity extends AppActivity {
	
	private BluetoothAdapter _bluetoothAdapter;
	private static int _playerNumber;
	private int _playerColour;
	
	public static BluetoothConnectionManager _bluetoothConnectionManager;
	
	private static ArrayList<BluetoothDevice> _potentialBluetoothDevices;
	private static ArrayList<BluetoothDevice> _bluetoothDevices;
	private static ArrayList<RemoteDeviceViewModel> _remoteDeviceViewModels;
	private static RemoteDeviceArrayAdapter _remoteDevicesArrayAdapter;
	
	private static UserDetailsRepository _userDetailsRepository;
	
	private ListView _remoteDevicesList;
	
	private static int _selectedDeviceIndex;
	
	private static AppActivity _activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_select_screen);
        
        _activity = this;
        
        _remoteDevicesList = (ListView)findViewById(R.id.remoteDevicesList);
        
        _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        _userDetailsRepository = new UserDetailsRepository(this);
        
        _potentialBluetoothDevices = new ArrayList<BluetoothDevice>();
		_bluetoothDevices = new ArrayList<BluetoothDevice>();
		_remoteDeviceViewModels = new ArrayList<RemoteDeviceViewModel>();
        
        _playerNumber = getIntent().getIntExtra("playerNumber", -1);
        
        View localIcon = findViewById(R.id.localIcon);
        View virtualIcon = findViewById(R.id.virtualIcon);
        if(_playerNumber == 2) {
        	_playerColour = getResources().getColor(R.color.playerTwoColour);
        } else if(_playerNumber == 3) {
        	_playerColour = getResources().getColor(R.color.playerThreeColour);
        } else if(_playerNumber == 4) {
        	_playerColour = getResources().getColor(R.color.playerFourColour);
        }
        
        _remoteDevicesArrayAdapter = new RemoteDeviceArrayAdapter(this, _playerColour, _remoteDeviceViewModels);
        _remoteDevicesList.setAdapter(_remoteDevicesArrayAdapter);
        _remoteDevicesList.setOnItemClickListener(removeDeviceSelected);
        
        localIcon.setBackgroundColor(_playerColour);
    	virtualIcon.setBackgroundColor(_playerColour);
    }
    
    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothDevice.ACTION_UUID);
        registerReceiver(_bluetoothBroardcaseReceiver, filter);
        setupRemoteDeviceSelection();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(_bluetoothBroardcaseReceiver);
        if(_bluetoothAdapter != null) {
        	_bluetoothAdapter.cancelDiscovery();
    	}
        if(_bluetoothConnectionManager != null) {
        	_bluetoothConnectionManager.stop();
    	}
        super.onPause();
    }
    
    private OnItemClickListener removeDeviceSelected = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			remoteDeviceSelected(position);
		}
    };
    
    private BroadcastReceiver _bluetoothBroardcaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
				Log.i("Bluetooth", "Starting device search...");
				
			} else if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
				BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!_potentialBluetoothDevices.contains(bluetoothDevice)) {
                	_potentialBluetoothDevices.add(bluetoothDevice);
                	Log.i("Bluetooth", "Found device: " + bluetoothDevice.getName());
                }
                
			} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
				Log.i("Bluetooth", "Found " + _potentialBluetoothDevices.size() + " potential devices");
				Log.i("Bluetooth", "Verifying service compatibility...");
				
				//Found all potential devices
				for(BluetoothDevice bluetoothDevice : _potentialBluetoothDevices) {
					bluetoothDevice.fetchUuidsWithSdp();
				}
				
			} else if(BluetoothDevice.ACTION_UUID.equals(intent.getAction())) {
				//Loop over potential devices to see which support this app
				BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Parcelable[] remoteDeviceServices = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
				if(_potentialBluetoothDevices.size() > 0) {
					_potentialBluetoothDevices.remove(bluetoothDevice);
					
					if(remoteDeviceServices != null) {
						for(Parcelable serviceUUID : remoteDeviceServices) {
							if(serviceUUID.toString().equalsIgnoreCase(BLUETOOTH_UUID)) {
								if(!_bluetoothDevices.contains(bluetoothDevice)) {
									_bluetoothDevices.add(bluetoothDevice);
									_remoteDeviceViewModels.add(new RemoteDeviceViewModel(bluetoothDevice.getName()));
									_remoteDevicesArrayAdapter.notifyDataSetChanged();
									
									Log.i("Bluetooth", "Found compatible device: " + bluetoothDevice.getName());
								}
							}
						}
					} else {
						Log.i("Bluetooth", "Null UUID for: " + bluetoothDevice.getName());
					}
					
					if(_potentialBluetoothDevices.size() == 0) {
						Log.i("Bluetooth", "Finished searching for devices");
						
						_bluetoothAdapter.cancelDiscovery();
						
						View remoteDeviceArea = findViewById(R.id.remotePlayersArea);
				    	View loadingSpinner = findViewById(R.id.remotePlayersLoadingSpinner);
				    	
				    	remoteDeviceArea.setVisibility(View.VISIBLE);
				    	loadingSpinner.setVisibility(View.GONE);
				    	_remoteDevicesList.setVisibility(View.VISIBLE);
				    	_remoteDevicesList.setEnabled(true);
					}
				}
				
				
			} else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
	        	int state = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);
	        	switch(state) {
	        	case BluetoothAdapter.STATE_ON:
	        	case BluetoothAdapter.STATE_OFF:
	        		setupRemoteDeviceSelection();
	        		break;
	        	}
			}
         }
    };
    
    private void setupRemoteDeviceSelection() {
    	View remoteDeviceArea = findViewById(R.id.remotePlayersArea);
    	View bluetoothDisabledArea = findViewById(R.id.bluetoothDisabledArea);
    	View loadingSpinner = findViewById(R.id.remotePlayersLoadingSpinner);
    	View bluetoothDevicesList = findViewById(R.id.remoteDevicesList);
    	View enableBluetoothButton = findViewById(R.id.enableBluetoothButton);
    	View bluetoothTurningOnSpinner = findViewById(R.id.bluetoothTurningOnSpinner);
    	if(_bluetoothAdapter == null) {
    		remoteDeviceArea.setVisibility(View.GONE);
    	} else {
    		remoteDeviceArea.setVisibility(View.VISIBLE);
    		if(_bluetoothAdapter.isEnabled()) {
    			bluetoothDisabledArea.setVisibility(View.GONE);
    			bluetoothDevicesList.setVisibility(View.GONE);
    			loadingSpinner.setVisibility(View.VISIBLE);
    			searchForDevices();
    		} else {
    			bluetoothDisabledArea.setVisibility(View.VISIBLE);
    			bluetoothDevicesList.setVisibility(View.GONE);
    			loadingSpinner.setVisibility(View.GONE);
    			enableBluetoothButton.setVisibility(View.VISIBLE);
    	    	bluetoothTurningOnSpinner.setVisibility(View.GONE);
    		}
    	}
    }
    
    private void searchForDevices() {
    	_potentialBluetoothDevices.clear();
    	_remoteDeviceViewModels.clear();
    	_bluetoothDevices.clear();
		_remoteDevicesArrayAdapter.notifyDataSetChanged();
    	_bluetoothAdapter.startDiscovery();
    }
    
    private static final Handler _bluetoothHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
        	switch(message.what) {
        	case BluetoothConnectionManager.ACTION_STATE_CHANGE:
        		if(message.arg1 == BluetoothConnectionManager.STATE_CONNECTED) {
        			Log.i("PlayerSelectActivity:Handler", "Connected");
        			BluetoothDevice bluetoothDevice = _bluetoothDevices.get(_selectedDeviceIndex);
        			BluetoothMessage messageToSend = new BluetoothMessage(bluetoothDevice.getAddress());
        			messageToSend.setArg1(_userDetailsRepository.GetNameForUser(USER_ID));
        			messageToSend.setMessageType(BluetoothMessage.CONNECTION_REQUEST);
        			_bluetoothConnectionManager.write(messageToSend.serialize());
        		}
        		break;
        	case BluetoothConnectionManager.ACTION_MESSAGE_RECEIVED:
        		BluetoothMessage receivedMessage = BluetoothMessage.deserialize((byte[])message.obj);
        		if(receivedMessage.getMessageType().equals(BluetoothMessage.CONNECTION_REQUEST_ACCEPTED)) {
        			_remoteDeviceViewModels.get(_selectedDeviceIndex).setConnectionStatus(RemoteDeviceViewModel.STATUS_CONNECTED);
        			String name = receivedMessage.getArg1();
        			_remoteDevicesArrayAdapter.notifyDataSetChanged();
        			remotePlayerAccepted(_bluetoothDevices.get(_selectedDeviceIndex), name);
        		} else if(receivedMessage.getMessageType().equals(BluetoothMessage.CONNECTION_REQUEST_REFUSED)) {
        			_remoteDeviceViewModels.get(_selectedDeviceIndex).setConnectionStatus(RemoteDeviceViewModel.STATUS_FAILED);
        			_remoteDevicesArrayAdapter.notifyDataSetChanged();
        		} else if(receivedMessage.getMessageType().equals(BluetoothMessage.GAME_CHAT)) {
        			Log.i("GameActivity:Handler", receivedMessage.getArg1() + ": " + receivedMessage.getArg2());
        			Toast.makeText(_activity, receivedMessage.getArg1() + ": " + receivedMessage.getArg2(), Toast.LENGTH_SHORT).show();
        		}
                Log.i("PlayerSelectActivity:Handler", "Received Message: " + receivedMessage.getMessageType());
        		break;
        	case BluetoothConnectionManager.ACTION_MESSAGE_SENT:
        		Log.i("PlayerSelectActivity:Handler", "Sent Message");
        		break;
        	case BluetoothConnectionManager.ACTION_CONNECTION_FAILED:
        		Log.i("PlayerSelectActivity:Handler", "Connection Failed");
        		break;
        	case BluetoothConnectionManager.ACTION_CONNECTION_LOST:
        		Log.i("PlayerSelectActivity:Handler", "Connection Lost");
        		break;
        	} 
        }
    };
    
    private void remoteDeviceSelected(int position) {
    	BluetoothDevice bluetoothDevice = _bluetoothDevices.get(position);
    	_remoteDevicesList.setEnabled(false);
    	_remoteDeviceViewModels.get(position).setConnectionStatus(RemoteDeviceViewModel.STATUS_CONNECTING);
		_remoteDevicesArrayAdapter.notifyDataSetChanged();
		_bluetoothConnectionManager = new BluetoothConnectionManager(_bluetoothHandler, BLUETOOTH_UUID, BLUETOOTH_SERVICE_NAME);
		_bluetoothConnectionManager.connect(bluetoothDevice);
		_selectedDeviceIndex = position;
    }
    
    private static void remotePlayerAccepted(BluetoothDevice device, String name) {
    	Intent intent = new Intent();
        intent.putExtra("playerNumber", _playerNumber);
        intent.putExtra("type", "remoteControlled");
        intent.putExtra("macAddress", device.getAddress());
        intent.putExtra("name", name);
//        intent.putExtra("displayPicture", displayPicture);
        _activity.setResult(RESULT_OK, intent);
        _activity.finish();
    }
    
    public void userControlledClicked(View view) {
    	Intent intent = new Intent();
        intent.putExtra("playerNumber", _playerNumber);
        intent.putExtra("type", "userControlled");
        setResult(RESULT_OK, intent);
    	finish();
    }
    
    public void computerControlledClicked(View view) {
    	Intent intent = new Intent();
        intent.putExtra("playerNumber", _playerNumber);
        intent.putExtra("type", "computerControlled");
        setResult(RESULT_OK, intent);
    	finish();
    }
    
    public void removeClicked(View view) {
    	Intent intent = new Intent();
        intent.putExtra("playerNumber", _playerNumber);
        intent.putExtra("type", "none");
        setResult(RESULT_OK, intent);
    	finish();
    }
    
    public void enableBluetoothClicked(View view) {
    	View enableBluetoothButton = findViewById(R.id.enableBluetoothButton);
    	View bluetoothTurningOnSpinner = findViewById(R.id.bluetoothTurningOnSpinner);
    	enableBluetoothButton.setVisibility(View.GONE);
    	bluetoothTurningOnSpinner.setVisibility(View.VISIBLE);
    	_bluetoothAdapter.enable();
    }
    
    @Override
    public void onBackPressed() {
    	finish();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float width = getWindow().getDecorView().getWidth();
        float height = getWindow().getDecorView().getHeight();
        float x = event.getX();
        float y = event.getY();
        
        //Tapped outside of modal
        if(x < 0 || x > width || y < 0 || y > height) {
        	finish();
        }
        
        return super.onTouchEvent(event);
    }
}