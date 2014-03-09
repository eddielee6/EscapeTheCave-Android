package me.eddielee.escapethecave.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.util.Log;
import android.view.View;

public class GamePiece extends DraggableObject {
	
	private static final Paint GLOW_COLOUR = new Paint();
	private static final Paint OUTLINE_COLOUR = new Paint();
	private static final float GAME_PIECE_RADIUS = 25;
	private static final float GAME_PIECE_RADIUS_OUTLINE = 27;
	private static final float GLOW_RADIUS = 40;
	private final Paint _gamePieceColour = new Paint();
	
	private int _correctCellValue = 0;
	private int _currentCellValue = 0;
	
	public void setCorrectCellValue(int correctCellValue) {
		_correctCellValue = correctCellValue;
	}
	
	public int getCorrectCellValue() {
		return _correctCellValue;
	}
	
	public int getCurrentCellValue() {
		return _currentCellValue;
	}

	public GamePiece(int colour) {
		super(GAME_PIECE_RADIUS * 2, GAME_PIECE_RADIUS * 2);
		_gamePieceColour.setColor(colour);
		OUTLINE_COLOUR.setColor(Color.BLACK);
		OUTLINE_COLOUR.setAntiAlias(true);

		GLOW_COLOUR.setStyle(Paint.Style.FILL);
		GLOW_COLOUR.setDither(true);
		GLOW_COLOUR.setAntiAlias(true);
	}

	@Override
	public void doDraw(Canvas canvas) {
		if(this.isDraggable()) {
			float glowOffset = GLOW_RADIUS - GAME_PIECE_RADIUS;
			float posX = (getX() + GLOW_RADIUS) - glowOffset;
			float posY = (getY() + GLOW_RADIUS) - glowOffset;
			GLOW_COLOUR.setShader(new RadialGradient(posX, posY, GLOW_RADIUS, Color.YELLOW, Color.TRANSPARENT, TileMode.CLAMP));
			canvas.drawCircle(posX, posY, GLOW_RADIUS, GLOW_COLOUR);
		}
		float outlineOffset = GAME_PIECE_RADIUS_OUTLINE - GAME_PIECE_RADIUS;
		canvas.drawCircle((getX() + GAME_PIECE_RADIUS_OUTLINE) - outlineOffset, (getY() + GAME_PIECE_RADIUS_OUTLINE) - outlineOffset, GAME_PIECE_RADIUS_OUTLINE, OUTLINE_COLOUR);
		canvas.drawCircle(getX() + GAME_PIECE_RADIUS, getY() + GAME_PIECE_RADIUS, GAME_PIECE_RADIUS, _gamePieceColour);
	}
	
	public Paint getPaint() {
		return _gamePieceColour;
	}
	
	@Override
	public void onDragEnd(View v) {
		EscapeTheCaveGameView gameView = (EscapeTheCaveGameView)v;
		if(gameView != null) {
			int gridX = (int)Math.floor(((getX() + (getWidth() / 2)) - gameView.getCenterOffset()) / gameView.getCellSize());
			int gridY = (int)Math.floor(((getY() + (getHeight() / 2)) - gameView.getCenterOffset()) / gameView.getCellSize());
			_currentCellValue = gameView.getGameBoard().getIndexForGridReference(gridX, gridY);
			Log.i("GamePiece", "Dropped on value: " + _currentCellValue);
			
			if(_currentCellValue == _correctCellValue) {
				if(_correctCellValue == (gameView.getGameBoard().gridSquareCount() - 1)) {
					super.setIsDraggable(false);
					gameView.winner();
				} else {
					if(gameView.getGameBoard().getGridSquareAtIndex(_correctCellValue).IsMagic()) {
						super.setIsDraggable(false);
						gameView.wakeUpWizard();
					} else {
						gameView.nextPlayer();
					}
				}
			}
		}

	}
}