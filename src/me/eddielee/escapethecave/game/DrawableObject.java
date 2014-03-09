package me.eddielee.escapethecave.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DrawableObject {
	
	private float _x = 0;
	private float _y = 0;
	private float _width;
	private float _height;
	private Boolean _visible = true;
	
	private Paint _defaultPaint;
	
	public DrawableObject(float width, float height) {
		_width = width;
		_height = height;
		
		_defaultPaint = new Paint();
		_defaultPaint.setColor(Color.RED);
	}
	
	public final Boolean doesPositionCollidesWithObject(float objectX, float objectY) {
		return doesPositionCollidesWithObject(objectX, objectY, 1, 1);
	}
	
	public final Boolean doesPositionCollidesWithObject(float objectX, float objectY, float objectWidth, float objectHeight) {
		return !(
				(objectY + objectHeight < this.getY()) ||
				(objectY 				> this.getY() + this.getHeight()) ||
				(objectX 				> this.getX() + this.getWidth()) ||
				(objectX + objectWidth 	< this.getX()));
	}
	
	public final void setVisible(Boolean visible) {
		_visible = visible;
	}
	
	public final Boolean isVisible() {
		return _visible;
	}
	
	public final float getXCenter() {
		return this.getX() + (this.getWidth() / 2);
	}
	
	public final float getYCenter() {
		return this.getY() + (this.getHeight() / 2);
	}
	
	public final float getX() {
		return _x;
	}

	public final void setX(float newX) {
		_x = newX;
	}

	public final float getY() {
		return _y;
	}

	public final void setY(float newY) {
		_y = newY;
	}

	public final float getWidth() {
		return _width;
	}

	public final float getHeight() {
		return _height;
	}
	
	public void setPosition(float x, float y) {
		this.setX(x);
		this.setY(y);
	}
	
	public final void draw(Canvas canvas) {
		if(this.isVisible()) {
			this.doDraw(canvas);
		}
	}
	
	//Override this to draw
	protected void doDraw(Canvas canvas) {
		canvas.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), _defaultPaint);
	}
}