package me.eddielee.escapethecave.game;

import me.eddielee.escapethecave.MainMenuActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameOverView extends DrawableObject implements OnTouchListener {
	
	private static final Paint _backgroundPaint = new Paint();
	private static final Paint _shadowPaint = new Paint();
	private static final Paint _titlePaint = new Paint();
	private static final Paint _messageTextPaint = new Paint();
	private static final Paint _buttonPaint = new Paint();
	private static final Paint _buttonTextPaint = new Paint();
	private static final Paint _buttonPaintActive = new Paint();
	
	private static int _gameId;
	private static String _message;
	
	private static float _buttonHeight;
	private static float _buttonWidth;
	
	private static boolean _buttonState = false;
	
	private static Context _context;

	public GameOverView(Context context, float width, float height) {
		super(width, height);
		
		_context = context;
		
		setVisible(false);
		_backgroundPaint.setColor(Color.WHITE);
		
		_buttonPaint.setColor(Color.DKGRAY);
		_buttonPaintActive.setColor(Color.LTGRAY);
		
		_shadowPaint.setColor(Color.argb(125, 0, 0, 0)); 
		
		_titlePaint.setColor(Color.BLACK);
		_titlePaint.setTextAlign(Align.CENTER);
		_titlePaint.setTextSize(80);
		_titlePaint.setTypeface(Typeface.DEFAULT_BOLD); 
		
		_messageTextPaint.setColor(Color.DKGRAY);
		_messageTextPaint.setTextAlign(Align.CENTER);
		_messageTextPaint.setTextSize(40);
		
		_buttonTextPaint.setColor(Color.BLACK);
		_buttonTextPaint.setTextAlign(Align.CENTER);
		_buttonTextPaint.setTextSize(40);
		_buttonTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

		_buttonHeight = (getHeight() / 5);
		_buttonWidth = (getWidth() / 2);
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
	}
	
	public void showWithMessageForGameId(String message, int gameId) {
		_gameId = gameId;
		_message = message;
		super.setVisible(true);
	}
	
	public void hide() {
		super.setVisible(false);
	}

	@Override
	public void doDraw(Canvas canvas) {
		//Background
		canvas.drawRoundRect(new RectF(getX() - 10, getY() - 10, (getX() + getWidth()) + 10, (getY() + getHeight()) + 10), 15, 15, _shadowPaint);
		canvas.drawRoundRect(new RectF(getX(), getY(), getX() + getWidth(), getY() + getHeight()), 15, 15, _backgroundPaint);
		
		//Text
		canvas.drawText("Game Over!", getX() + (getWidth() / 2), getY() + 80, _titlePaint);
		canvas.drawText(_message, getX() + (getWidth() / 2), getY() + 160, _messageTextPaint);
		
		//Button
		float baseX = (getX() + (getWidth() / 2)) - (_buttonWidth / 2);
		float textY = ((getY() + getHeight()) - _buttonHeight) - ((_buttonTextPaint.descent() + _buttonTextPaint.ascent()) / 2);
		canvas.drawRoundRect(new RectF(baseX - 3, ((getY() + getHeight()) - _buttonHeight) - 3 - 30, (baseX + _buttonWidth) + 3, ((getY() + getHeight())) + 3 - 30), 6, 6, _buttonPaint);
		canvas.drawRoundRect(new RectF(baseX, ((getY() + getHeight()) - _buttonHeight) - 30, baseX + _buttonWidth, (getY() + getHeight()) - 30), 6, 6, _buttonState ? _buttonPaintActive : _backgroundPaint);
		
		canvas.drawText("End game", baseX + (_buttonWidth / 2), textY, _buttonTextPaint);
	}
	
	public void buttonPressed() {
		Intent mainMenuIntent = new Intent(_context, MainMenuActivity.class);
		mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mainMenuIntent.putExtra("gameId", _gameId);
		_context.startActivity(mainMenuIntent);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(!super.isVisible()) {
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			for(int i = 0; i < 3; i++) {
				float startX = (getX() + (getWidth() / 2)) - (_buttonWidth / 2);
				float endX = startX + _buttonWidth;
				float startY = ((getY() + getHeight()) - _buttonHeight) - 30;
				float endY = startY + _buttonHeight;
				if(event.getX() > startX && event.getX() < endX && event.getY() > startY && event.getY() < endY) {
					_buttonState = true;
					return true;
				}
			}
			
		case MotionEvent.ACTION_MOVE:
			return true;
		
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			float startX = (getX() + (getWidth() / 2)) - (_buttonWidth / 2) - 3;
			float endX = startX + _buttonWidth + 3;
			float startY = ((getY() + getHeight()) - _buttonHeight) - 3;
			float endY = startY + _buttonHeight + 3;
			if(event.getX() > startX && event.getX() < endX && event.getY() > startY && event.getY() < endY) {
				if(_buttonState == true) {
					_buttonState = false;
					buttonPressed();
				}
			} else {
				_buttonState = false;
			}

			return true;
		}

		_buttonState = false;
		
		return true;
	}
}