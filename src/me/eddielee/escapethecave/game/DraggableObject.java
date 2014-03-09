package me.eddielee.escapethecave.game;

import java.util.ArrayList;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DraggableObject extends DrawableObject implements OnTouchListener {
	
	private boolean _touchEventStarted = false;
	private float _touchStartX;
	private float _touchStartY;
	private float _dragOffsetX;
	private float _dragOffsetY;
	
	private boolean _dragBoundsSet = false;
	private Float _minXBound;
	private Float _maxXBound;
	private Float _minYBound;
	private Float _maxYBound;
	
	private boolean _isDraggable = true;
	
	public DraggableObject(float width, float height) {
		super(width, height);
	}
	
	public final void setIsDraggable(boolean isDraggable) {
		_isDraggable = isDraggable;
	}
	
	public final boolean isDraggable() {
		return _isDraggable;
	}
	
	public void setDragBounds(float minX, float maxX, float minY, float maxY) {
		_dragBoundsSet = true;
		_minXBound = minX;
		_maxXBound = maxX;
		_minYBound = minY;
		_maxYBound = maxY;
	}
	
	public void clearDragBounds() {
		_dragBoundsSet = false;
		_minXBound = null;
		_maxXBound = null;
		_minYBound = null;
		_maxYBound = null;
	}
	
	public void animateDrag(final View view, final ArrayList<float[]> points) {
		final Handler positionUpdateHandler = new Handler();
		positionUpdateHandler.post(new Runnable() {
			int point = 0;
			final static int POINTS_PER_TRANSITION = 10;
			int pointTransition = 0;
			
	        @Override
	        public void run() {
	        	float addX = ((points.get(point)[0] - getX()) / POINTS_PER_TRANSITION) * pointTransition;
	        	float addY = ((points.get(point)[1] - getY()) / POINTS_PER_TRANSITION) * pointTransition;
	        	setX(getX() + addX);
				setY(getY() + addY);
				
				if(pointTransition < POINTS_PER_TRANSITION && point < points.size()) {
					pointTransition++;
					positionUpdateHandler.postDelayed(this, 30);
				} else if(point < (points.size() - 1)) {
					point++;
					pointTransition = 0;
					positionUpdateHandler.postDelayed(this, 200);
				} else {
					onDragEnd(view);
				}
	        }
	    });
	}
	
	//Override this to set drag end event
	public void onDragEnd(View v) {
		
	}

	@Override
	public final boolean onTouch(View v, MotionEvent event) {
		if(_isDraggable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(!_touchEventStarted) {
					if(doesPositionCollidesWithObject(event.getX(), event.getY())) {
						_touchEventStarted = true;
						_touchStartX = super.getX();
						_touchStartY = super.getY();
						_dragOffsetX = event.getX();
						_dragOffsetY = event.getY();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if(_touchEventStarted) {
					_touchEventStarted = false;
					onDragEnd(v);
				}
				//Don't break
			case MotionEvent.ACTION_MOVE:
				if(_touchEventStarted) {
					float newX = _touchStartX + event.getX() - _dragOffsetX;
					float newY = _touchStartY + event.getY() - _dragOffsetY;
					if(_dragBoundsSet) {
						if(newX < _minXBound) {
							newX = _minXBound;
						}
						if(newY < _minYBound) {
							newY = _minYBound;
						}
						if(newX + super.getWidth() > _maxXBound) {
							newX = _maxXBound - super.getWidth();
						}
						if(newY + super.getHeight() > _maxYBound) {
							newY = _maxYBound - super.getHeight();
						}
					}
					super.setX(newX);
					super.setY(newY);
				}
				break;
			}
		}
		return true;
	}
}