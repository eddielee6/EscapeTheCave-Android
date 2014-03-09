package me.eddielee.escapethecave.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.util.Log;

public class BluetoothMessage implements Serializable {

	private static final long serialVersionUID = -908221190213345219L;
	
	public static final String CONNECTION_REQUEST = "CONNECTION_REQUEST";
	public static final String CONNECTION_REQUEST_ACCEPTED = "CONNECTION_REQUEST_ACCEPTED";
	public static final String CONNECTION_REQUEST_REFUSED = "CONNECTION_REQUEST_REFUSED";
	public static final String GAME_STARTED = "GAME_STARTED";
	public static final String GAME_CHAT = "GAME_CHAT";
	
	private String _senderMacAddress;
	private String _messageType;
	private final String _recipientMacAddress;
	
//	private byte[] _object;
	private String _arg1;
	private String _arg2;
	
	public String getArg1() {
		return _arg1;
	}
	
	public String getArg2() {
		return _arg2;
	}
	
	public void setArg1(String arg1) {
		_arg1 = arg1;
	}
	
	public void setArg2(String arg2) {
		_arg2 = arg2;
	}
	
	public void setSenderMacAddress(String senderMacAddress) {
		_senderMacAddress = senderMacAddress;
	}
	
	public String getSenderMacAddress() {
		return _senderMacAddress;
	}
	
//	public byte[] getObject() {
//		return _object;
//	}
//	
//	public void setObject(byte[] object) {
//		_object = object;
//	}
	
	public BluetoothMessage(String recipientMacAddress) {
		_recipientMacAddress = recipientMacAddress;
	}
	
	public String getMessageType() {
		return _messageType;
	}
	
	public void setMessageType(String messageType) {
		_messageType = messageType;
	}
	
	public String getRecipientMacAddress() {
		return _recipientMacAddress;
	}
	
	public byte[] serialize() {
		try {
		    ByteArrayOutputStream b = new ByteArrayOutputStream();
		    ObjectOutputStream o = new ObjectOutputStream(b);
		    o.writeObject(this);
	   	 	return b.toByteArray();
		} catch (Exception e) {
			Log.e("BluetoothMessage", "Failed to serialize: ", e);
			return null;
		}
	}
	
	public static BluetoothMessage deserialize(byte[] bytes) {
		try {
			ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		    ObjectInputStream o = new ObjectInputStream(b);
		    return (BluetoothMessage)o.readObject();
		} catch (Exception e) {
			Log.e("BluetoothMessage", "Failed to deserialize: ", e);
			return null;
		}
	}
}
