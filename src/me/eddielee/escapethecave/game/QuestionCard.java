package me.eddielee.escapethecave.game;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class QuestionCard extends DrawableObject implements OnTouchListener {
	
	private static WeakReference<QuestionAnsweredListener> _questionAnsweredSubscriber;
	
	private static final Paint _backgroundPaint = new Paint();
	private static final Paint _shadowPaint = new Paint();
	private static final Paint _titlePaint = new Paint();
	private static final Paint _questionPaint = new Paint();
	private static final Paint _smallTextPaint = new Paint();
	private static final Paint _buttonPaint = new Paint();
	private static final Paint _buttonTextPaint = new Paint();
	private static final Paint _buttonPaintActive = new Paint();
	private static Wizard _wizard;
	
	private static final float _buttonXPadding = 15;
	private static float _buttonHeight;
	private static float _buttonWidth;
	
	private static Question _question;
	
	private static boolean[] _buttonStates = new boolean[3];

	public QuestionCard(Context context, float width, float height) {
		super(width, height);
		
		setVisible(false);
		_backgroundPaint.setColor(Color.WHITE);
		
		_buttonPaint.setColor(Color.DKGRAY);
		_buttonPaintActive.setColor(Color.LTGRAY);
		
		_shadowPaint.setColor(Color.argb(125, 0, 0, 0)); 
		
		_titlePaint.setColor(Color.BLACK);
		_titlePaint.setTextAlign(Align.CENTER);
		_titlePaint.setTextSize(40);
		_titlePaint.setTypeface(Typeface.DEFAULT_BOLD); 
		
		_questionPaint.setColor(Color.DKGRAY);
		_questionPaint.setTextAlign(Align.CENTER);
		_questionPaint.setTextSize(100);
		
		_smallTextPaint.setColor(Color.DKGRAY);
		_smallTextPaint.setTextAlign(Align.CENTER);
		_smallTextPaint.setTextSize(25);
		
		_buttonTextPaint.setColor(Color.BLACK);
		_buttonTextPaint.setTextAlign(Align.CENTER);
		_buttonTextPaint.setTextSize(40);
		_buttonTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		_buttonStates[0] = false;
		_buttonStates[1] = false;
		_buttonStates[2] = false;

		_buttonHeight = (getHeight() / 5);
		_buttonWidth = (getWidth() / 3) - (_buttonXPadding * 3);
		
		_wizard = new Wizard(context, 228, 182);
	}
	
	private static String formatFloat(float value) {
		if(value == (int)value) {
	        return String.format("%d", (int)value);
		} else {
			DecimalFormat formatter = new DecimalFormat("0.##");
			return formatter.format(value);
		}
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		_wizard.setPosition(x + getWidth() - (_wizard.getWidth() * 0.70f), y + (getHeight() * 0.6f));
	}
	
	public void askQuestion(Question question) {
		super.setVisible(true);
		_question = question;
		_wizard.start();
	}
	
	public void hide() {
		super.setVisible(false);
		_wizard.stop();
	}

	@Override
	public void doDraw(Canvas canvas) {
		//Background
		canvas.drawRoundRect(new RectF(getX() - 10, getY() - 10, (getX() + getWidth()) + 10, (getY() + getHeight()) + 10), 15, 15, _shadowPaint);
		canvas.drawRoundRect(new RectF(getX(), getY(), getX() + getWidth(), getY() + getHeight()), 15, 15, _backgroundPaint);
		
		//Text
		canvas.drawText("You've awoken an evil wizard!", getX() + (getWidth() / 2), getY() + 60, _titlePaint);
		canvas.drawText("You must answer his question to pass...", getX() + (getWidth() / 2), getY() + 100, _smallTextPaint);
		
		//Question
		canvas.drawText(_question.getQuestion(), getX() + (getWidth() / 2), getY() + 220, _questionPaint);
		
		//Buttons
		for(int i = 0; i < 3; i++) {
			float baseX = getX() + (_buttonXPadding * 3) + (_buttonXPadding * i) + (_buttonWidth * i);
			float textY = ((getY() + getHeight()) - _buttonHeight) - ((_buttonTextPaint.descent() + _buttonTextPaint.ascent()) / 2) + 10;
			canvas.drawRoundRect(new RectF(baseX - 3, (((getY() + getHeight()) - _buttonHeight) - 30) - 3, (baseX + _buttonWidth) + 3, ((getY() + getHeight()) - 30) + 3), 6, 6, _buttonPaint);
			canvas.drawRoundRect(new RectF(baseX, ((getY() + getHeight()) - _buttonHeight) - 30, baseX + _buttonWidth, (getY() + getHeight()) - 30), 6, 6, _buttonStates[i] ? _buttonPaintActive : _backgroundPaint);
			canvas.drawText(formatFloat(_question.getPossibleAnswers().get(i)), baseX + (_buttonWidth / 2), textY, _buttonTextPaint);
		}
		
		//Wizard
		_wizard.doDraw(canvas);
	}
	
	public void setQuestionAnsweredListener(QuestionAnsweredListener questionAnsweredListener) {
		_questionAnsweredSubscriber = new WeakReference<QuestionAnsweredListener>(questionAnsweredListener);
	}
	
	public void buttonPressed(int buttonNumber) {
		if(_questionAnsweredSubscriber != null && _questionAnsweredSubscriber.get() != null) {
			_questionAnsweredSubscriber.get().onQuestionAnswered(_question.getAnswer() == _question.getPossibleAnswers().get(buttonNumber));
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(!super.isVisible()) {
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			for(int i = 0; i < 3; i++) {
				float startX = getX() + (_buttonXPadding * 3) + (_buttonXPadding * i) + (_buttonWidth * i);
				float endX = startX + _buttonWidth;
				float startY = (getY() + getHeight()) - _buttonHeight;
				float endY = startY + _buttonHeight;
				if(event.getX() > startX && event.getX() < endX && event.getY() > startY && event.getY() < endY) {
					_buttonStates[i] = true;
					return true;
				}
			}
			
		case MotionEvent.ACTION_MOVE:
			return true;
		
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			for(int i = 0; i < 3; i++) {
				float startX = getX() + (_buttonXPadding * 3) + (_buttonXPadding * i) + (_buttonWidth * i);
				float endX = startX + _buttonWidth;
				float startY = (getY() + getHeight()) - _buttonHeight;
				float endY = startY + _buttonHeight;
				if(event.getX() > startX && event.getX() < endX && event.getY() > startY && event.getY() < endY) {
					if(_buttonStates[i] == true) {
						_buttonStates[i] = false;
						buttonPressed(i);
						Log.i("QuestionCard", "Button " + i + " pressed.");
					}
				} else {
					_buttonStates[i] = false;
				}
			}
			return true;
		}
		
		//Else reset
		for(int i = 0; i < 3; i++) {
			_buttonStates[i] = false;
		}
		
		return true;
	}
}