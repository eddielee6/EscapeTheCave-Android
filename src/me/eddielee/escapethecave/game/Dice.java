package me.eddielee.escapethecave.game;

import java.lang.ref.WeakReference;
import java.util.Random;

import me.eddielee.escapethecave.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

public class Dice extends DrawableObject implements SensorEventListener  {
	
	//Shake detection
	private SensorManager _sensorManager;
	private float _acceleration = 0.00f;
	private float _currentAcceleration = SensorManager.GRAVITY_EARTH;
	private float _lastAcceleration = SensorManager.GRAVITY_EARTH;
	
	private static WeakReference<DiceRoleCompleteListener> _roleCompleteSubscriber;
	
	//Dice values
	private static final int DELAY = 200;
	private static Random _randomGenerator;
	private static boolean _rolling = false;
	private static int _shakeCount = 0;
	private static int _value = 1;
	private static boolean _shakeToRollEnabledState = false;
	
	public void setShakeToRollEnabledState(boolean state) {
		_shakeToRollEnabledState = state;
		_shakeCount = 0;
	}
	
	private static int _rotationIndex = 0;
	private Bitmap[] _spriteStates;
	private int[] _rotations;
	
	private Paint _bitmapPaint = new Paint();
	
	public Dice(Context context, float width, float height) {
		super(width, height);
		_sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		
		_randomGenerator = new Random(System.nanoTime());
		
		_bitmapPaint.setAntiAlias(true);
		_bitmapPaint.setFilterBitmap(true);
		_bitmapPaint.setDither(true);
		
		_spriteStates = new Bitmap[7];
		_spriteStates[0] = null; //Just to make the numbering easier
		_spriteStates[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_dice_1), (int)width, (int)height, false);
		_spriteStates[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_dice_2), (int)width, (int)height, false);
		_spriteStates[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_dice_3), (int)width, (int)height, false);
		_spriteStates[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_dice_4), (int)width, (int)height, false);
		_spriteStates[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_dice_5), (int)width, (int)height, false);
		_spriteStates[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_dice_6), (int)width, (int)height, false);
		
		_rotations = new int[4];
		_rotations[0] = 0;
		_rotations[1] = 90;
		_rotations[2] = 180;
		_rotations[3] = 270;
		
		_value = (_randomGenerator.nextInt(6) + 1);
	}
	
	public int GetValue() {
		return _value;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//Don't really care...
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(_shakeToRollEnabledState) {
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			_lastAcceleration = _currentAcceleration;
			_currentAcceleration = (float)Math.sqrt((x*x + y*y + z*z));
			float delta = _currentAcceleration - _lastAcceleration;
			_acceleration = _acceleration * 0.9f + delta;
			
			if((_acceleration < -5 || _acceleration > 5)) {
				_shakeCount++;
				if(_shakeCount > 2) {
					_shakeCount = 0;
					rollDice();
				}
			}
		}
	}
	
	@Override
	public void doDraw(Canvas canvas) {
		canvas.save();
		canvas.rotate(_rotations[_rotationIndex], getXCenter(), getYCenter());
		canvas.drawBitmap(_spriteStates[GetValue()], getX(), getY(), _bitmapPaint);
		canvas.restore();
	}
	
	public void setDiceRoleCompleteListener(DiceRoleCompleteListener diceRoleCompleteListener) {
		_roleCompleteSubscriber = new WeakReference<DiceRoleCompleteListener>(diceRoleCompleteListener);
	}
	
	public void rollDiceForResult(DiceRoleCompleteListener diceRoleCompleteListener) {
		_roleCompleteSubscriber = new WeakReference<DiceRoleCompleteListener>(diceRoleCompleteListener);
		rollDice();
	}
	
	public void rollDice() {
		if(!_rolling) {
			_rolling = true;
			final Handler h = new Handler();
		    h.post(new Runnable() {
		        private long rolls = 0;
	
		        @Override
		        public void run() {
		        	_value = (_randomGenerator.nextInt(6) + 1);
	
		            if(rolls++ < 10) {
		            	_rotationIndex = _randomGenerator.nextInt(4);
	            		h.postDelayed(this, DELAY);
		            } else {
		            	_rotationIndex = 0;
		            	_rolling = false;
		            	if(_roleCompleteSubscriber != null && _roleCompleteSubscriber.get() != null) {
		            		_roleCompleteSubscriber.get().onDiceRoleComplete(_value);
		            	}
		            }
		        }
		    });
		}
	}

	public void startListening() {
		_sensorManager.registerListener(this, _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void stopListening() {
		_sensorManager.unregisterListener(this);
	}
}
