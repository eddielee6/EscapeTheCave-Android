package me.eddielee.escapethecave.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public abstract class GameView extends ImageView {
	
	private static Boolean _isRunning = false;
	private static int _onDrawDelay = 30;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public final Boolean isRunning() {
		return _isRunning;
	}
	
	public final void setIsRunning(Boolean isRunning) {
		_isRunning = isRunning;
	}
	
	public final int getOnDrawDelay() {
		return _onDrawDelay;
	}
	
	public final void setOnDrawDelay(int onDrawDelay) {
		_onDrawDelay = onDrawDelay;
	}
	
	@Override
	protected final void onDraw(Canvas canvas) {
		if(_isRunning) {
			doDraw(canvas);
		}
		postInvalidateDelayed(_onDrawDelay);
	}
	
	//Override this to draw
	public void doDraw(Canvas canvas) {
		
	}
}