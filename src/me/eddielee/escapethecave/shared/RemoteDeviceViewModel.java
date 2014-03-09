package me.eddielee.escapethecave.shared;

public class RemoteDeviceViewModel {
	public static final int STATUS_DEFAULT = 1;
	public static final int STATUS_CONNECTING = 2;
	public static final int STATUS_CONNECTED = 3;
	public static final int STATUS_FAILED = 4;
	
	private String _deviceName;
	private int _connectionStatus;
	
	public RemoteDeviceViewModel(String deviceName) {
		_deviceName = deviceName;
		_connectionStatus = STATUS_DEFAULT;
	}
	
	public String getDeviceName() {
		return _deviceName;
	}
	
	public int getConnectionStatus() {
		return _connectionStatus;
	}
	
	public void setConnectionStatus(int connectionStatus) {
		_connectionStatus = connectionStatus;
	}
}