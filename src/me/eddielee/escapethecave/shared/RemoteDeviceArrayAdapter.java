package me.eddielee.escapethecave.shared;

import java.util.ArrayList;

import me.eddielee.escapethecave.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RemoteDeviceArrayAdapter extends ArrayAdapter<RemoteDeviceViewModel> {
    private final Activity _context;
    private final ArrayList<RemoteDeviceViewModel> _remoteDevices;
    private final int _backgroundColour;

    public RemoteDeviceArrayAdapter(Activity context, int colour, ArrayList<RemoteDeviceViewModel> remoteDevices) {
        super(context, R.layout.device_list_view, remoteDevices);
        _context = context;
        _backgroundColour = colour;
        _remoteDevices = remoteDevices;
    }

    static class ViewContainer {
        public ImageView icon;
        public TextView deviceName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	View rowView;
    	
    	RemoteDeviceViewModel device = _remoteDevices.get(position);

        if (convertView == null) {
            LayoutInflater inflater = _context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.device_list_view, null, true);
        } else {
        	rowView = convertView;
        }
        
        ImageView deviceIcon = (ImageView)rowView.findViewById(R.id.deviceIcon);
        TextView deviceName = (TextView)rowView.findViewById(R.id.deviceName);
        TextView deviceStatus = (TextView)rowView.findViewById(R.id.connectionStatus);
        ProgressBar connectionStatusSpinner = (ProgressBar)rowView.findViewById(R.id.connectingSpinner);
        
        deviceIcon.setBackgroundColor(_backgroundColour);
        deviceName.setText(device.getDeviceName());
        
        switch(device.getConnectionStatus()) {
        case RemoteDeviceViewModel.STATUS_DEFAULT:
        	connectionStatusSpinner.setVisibility(View.GONE);
        	deviceStatus.setVisibility(View.GONE);
        	break;
        case RemoteDeviceViewModel.STATUS_CONNECTING:
        	connectionStatusSpinner.setVisibility(View.VISIBLE);
        	deviceStatus.setVisibility(View.VISIBLE);
        	deviceStatus.setText(rowView.getContext().getString(R.string.bluetooth_device_connection_status_connecting));
        	break;
        case RemoteDeviceViewModel.STATUS_CONNECTED:
        	connectionStatusSpinner.setVisibility(View.GONE);
        	deviceStatus.setVisibility(View.VISIBLE);
        	deviceStatus.setText(rowView.getContext().getString(R.string.bluetooth_device_connection_status_connected));
        	break;
        case RemoteDeviceViewModel.STATUS_FAILED:
        	connectionStatusSpinner.setVisibility(View.GONE);
        	deviceStatus.setVisibility(View.VISIBLE);
        	deviceStatus.setText(rowView.getContext().getString(R.string.bluetooth_device_connection_status_failed));
        	break;
        }

        return rowView;
    }
}