package me.eddielee.escapethecave.game;

import me.eddielee.escapethecave.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;

public class Wizard extends DrawableObject {
	
	private Paint _bitmapPaint;
	private Bitmap[] _spriteStates;
	private int _spriteState = 0;
	
	private Boolean running = false;
	private static final int SPRITE_FRAME_RATE = 600;
	
	private final Handler _animationHandler = new Handler();

	public Wizard(Context context, float width, float height) {
		super(width, height);
		
		setVisible(false);
		
		_bitmapPaint = new Paint();
		_bitmapPaint.setAntiAlias(true);
		_bitmapPaint.setFilterBitmap(true);
		_bitmapPaint.setDither(true);
		
		_spriteStates = new Bitmap[2];
		_spriteStates[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_wizard_state2), (int)width, (int)height, false);
		_spriteStates[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_wizard_state3), (int)width, (int)height, false);
	}
	
	public void start() {
		super.setVisible(true);
		running = true;
		
		_spriteState = 0;
		_animationHandler.post(new Runnable()
	    {
	        @Override
	        public void run()
	        {
	        	if(++_spriteState > _spriteStates.length - 1) {
	        		_spriteState = 0;
	        	}
	        	if(running) {
	        		_animationHandler.postDelayed(this, SPRITE_FRAME_RATE);
	        	}
	        }
	    });
	}
	
	public void stop() {
		super.setVisible(false);
		running = false;
	}

	@Override
	public void doDraw(Canvas canvas) {
		canvas.drawBitmap(_spriteStates[_spriteState], getX(), getY(), _bitmapPaint);
	}
}