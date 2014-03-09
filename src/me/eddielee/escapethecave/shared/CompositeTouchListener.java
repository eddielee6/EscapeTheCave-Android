package me.eddielee.escapethecave.shared;

import java.util.ArrayList;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class CompositeTouchListener implements OnTouchListener {
	private ArrayList<OnTouchListener> registeredListeners = new ArrayList<OnTouchListener>();
	
	public void registerListener (OnTouchListener touchListener) {
		registeredListeners.add(touchListener);
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		for(OnTouchListener touchListener : registeredListeners) {
			touchListener.onTouch(view, event);
		}
		return true;
	}
}