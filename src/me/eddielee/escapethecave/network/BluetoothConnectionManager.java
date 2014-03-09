package me.eddielee.escapethecave.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BluetoothConnectionManager {

    // Threads
    private ListeningThread _listeningThread;
    private EstablishConnectionThread _establishConnectionThread;
    private ConnectionThread _connectionThread;

    // Connection states
    public static final int STATE_DEFAULT = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    //Handler messages
    public static final int ACTION_STATE_CHANGE = 1;
    public static final int ACTION_MESSAGE_RECEIVED = 2;
    public static final int ACTION_MESSAGE_SENT = 3;
    public static final int ACTION_CONNECTION_FAILED = 4;
    public static final int ACTION_CONNECTION_LOST = 5;
    
    // Members
    private final Handler _handler;
    private final UUID _uuid;
    private final String _serviceName;
    private final BluetoothAdapter _bluetoothAdapter;
    
    private int _currentState;

    public BluetoothConnectionManager(Handler handler, String uuid, String serviceName) {
    	_handler = handler;
    	_uuid = UUID.fromString(uuid);
    	_serviceName = serviceName;
    	_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    	_currentState = STATE_DEFAULT;
    }

    private synchronized void setState(int state) {
    	_currentState = state;
        _handler.obtainMessage(ACTION_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return _currentState;
    }
    
    protected final String getServiceName() {
        return _serviceName;
    }
    
    protected final UUID getUUID() {
        return _uuid;
    }

    public synchronized void start() {
        if (_establishConnectionThread != null) {
        	_establishConnectionThread.cancel();
        	_establishConnectionThread = null;
        }

        if (_connectionThread != null) {
        	_connectionThread.cancel();
        	_connectionThread = null;
        }

        setState(STATE_LISTEN);

        if (_listeningThread == null) {
            _listeningThread = new ListeningThread();
            _listeningThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device) {
        if (getState() == STATE_CONNECTING) {
            if (_establishConnectionThread != null) {
            	_establishConnectionThread.cancel();
            	_establishConnectionThread = null;
            }
        }

        if (_connectionThread != null) {
        	_connectionThread.cancel();
        	_connectionThread = null;
        }

        _establishConnectionThread = new EstablishConnectionThread(device);
        _establishConnectionThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.i("BluetoothConnectionManager", "Connected");

        if (_establishConnectionThread != null) {
        	_establishConnectionThread.cancel();
        	_establishConnectionThread = null;
        }

        if (_connectionThread != null) {
        	_connectionThread.cancel();
        	_connectionThread = null;
        }

        if (_listeningThread != null) {
            _listeningThread.cancel();
            _listeningThread = null;
        }

        _connectionThread = new ConnectionThread(socket);
        _connectionThread.start();

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (_establishConnectionThread != null) {
            _establishConnectionThread.cancel();
            _establishConnectionThread = null;
        }

        if (_connectionThread != null) {
            _connectionThread.cancel();
            _connectionThread = null;
        }

        if (_listeningThread != null) {
            _listeningThread.cancel();
            _listeningThread = null;
        }
        setState(STATE_DEFAULT);
    }

    public void write(byte[] out) {
        ConnectionThread r;
        synchronized (this) {
            if (getState() != STATE_CONNECTED) {
            	return;
            }
            r = _connectionThread;
        }
        r.write(out);
    }

    private void connectionFailed() {
    	_handler.obtainMessage(ACTION_CONNECTION_FAILED).sendToTarget();
    	Log.i("BluetoothConnectionManager", "Connection failed.");
        BluetoothConnectionManager.this.start();
    }

    private void connectionLost() {
    	_handler.obtainMessage(ACTION_CONNECTION_LOST).sendToTarget();
    	Log.i("BluetoothConnectionManager", "Connection lost.");
        BluetoothConnectionManager.this.start();
    }
    
    private class ListeningThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public ListeningThread() {
            BluetoothServerSocket tmp = null;
            try {
            	tmp = _bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(getServiceName(), getUUID());
            } catch (IOException e) {
                Log.e("BluetoothConnectionManager:ListeningThread", "Failed to create listening thread: ", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.i("BluetoothConnectionManager:ListeningThread", "Starting listening thread");

            BluetoothSocket socket = null;

            while (BluetoothConnectionManager.this.getState() != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("BluetoothConnectionManager:ListeningThread", "Socket accept() failed", e);
                    break;
                }

                if (socket != null) {
                    synchronized (BluetoothConnectionManager.this) {
                        switch (BluetoothConnectionManager.this.getState()) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_DEFAULT:
                        case STATE_CONNECTED:
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e("BluetoothConnectionManager:ListeningThread", "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("BluetoothConnectionManager:ListeningThread", "Socket close() of listening thread failed: " + e.getLocalizedMessage());
            }
        }
    }

    private class EstablishConnectionThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public EstablishConnectionThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            try {

                    tmp = device.createInsecureRfcommSocketToServiceRecord(BluetoothConnectionManager.this.getUUID());

            } catch (IOException e) {
                Log.e("BluetoothConnectionManager:EstablishConnectionThread", "Socket create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i("BluetoothConnectionManager:EstablishConnectionThread", "BEGIN _establishConnectionThread");

            _bluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e("BluetoothConnectionManager:EstablishConnectionThread", "unable to close() socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            synchronized (BluetoothConnectionManager.this) {
                _establishConnectionThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("BluetoothConnectionManager:EstablishConnectionThread", "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectionThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectionThread(BluetoothSocket socket) {
            Log.d("BluetoothConnectionManager:ConnectionThread", "create ConnectionThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("BluetoothConnectionManager:ConnectionThread", "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i("BluetoothConnectionManager:ConnectionThread", "BEGIN mConnectedThread");
            byte[] buffer = new byte[65536];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    _handler.obtainMessage(ACTION_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e("BluetoothConnectionManager:ConnectionThread", "disconnected", e);
                    connectionLost();
                    BluetoothConnectionManager.this.start();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                _handler.obtainMessage(ACTION_MESSAGE_SENT, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e("BluetoothConnectionManager:ConnectionThread", "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("BluetoothConnectionManager:ConnectionThread", "close() of connect socket failed", e);
            }
        }
    }
}
