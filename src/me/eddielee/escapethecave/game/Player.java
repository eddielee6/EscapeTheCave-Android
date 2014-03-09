package me.eddielee.escapethecave.game;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class Player implements Serializable, DiceRoleCompleteListener {

	private static final long serialVersionUID = 6383742080007182898L;
	
	private int _colour;
	private boolean _hasGeneratedDisplayPicture = false;
	private byte[] _displayPictureData;
	private boolean _isActive = false;
	private String _name;
	
	private WeakReference<GamePiece> _gamePiece;
	private static WeakReference<EscapeTheCaveGameView> _gameBoard;
	
	public final EscapeTheCaveGameView getGameBoard() {
		if(_gameBoard != null) {
			return _gameBoard.get();
		} else {
			return null;
		}
	}
	
	public void setGamePiece(GamePiece gamePiece) {
		_gamePiece = new WeakReference<GamePiece>(gamePiece);
	}
	
	public GamePiece getGamePiece() {
		if(_gamePiece != null) {
			return _gamePiece.get();
		} else {
			return null;
		}
	}
	
	public void moveGamePiece(int value) {
		int newValue = getGamePiece().getCurrentCellValue() + value;
		newValue = newValue > 80 ? 80 : newValue;
		newValue = newValue < 0 ? 0 : newValue;
		getGamePiece().setCorrectCellValue(newValue);
		
		EscapeTheCaveGameView gameView = getGameBoard();
		ArrayList<float[]> aniamteToPoints = new ArrayList<float[]>();
		
		if(value > 0) {
			for(int i = getGamePiece().getCurrentCellValue(); i <= getGamePiece().getCorrectCellValue(); i++) {
				float posX = ((gameView.getCellSize() * gameView.getGameBoard().getGridSquareAtIndex(i).GetX()) + gameView.getCenterOffset()) + (gameView.getCellSize() / 2) - (getGamePiece().getWidth() / 2);
				float posY = ((gameView.getCellSize() * gameView.getGameBoard().getGridSquareAtIndex(i).GetY()) + gameView.getCenterOffset()) + (gameView.getCellSize() / 2) - (getGamePiece().getHeight() / 2);
				aniamteToPoints.add(new float[]{posX, posY});
			}
		} else {
			for(int i = getGamePiece().getCurrentCellValue(); i >= getGamePiece().getCorrectCellValue(); i--) {
				float posX = ((gameView.getCellSize() * gameView.getGameBoard().getGridSquareAtIndex(i).GetX()) + gameView.getCenterOffset()) + (gameView.getCellSize() / 2) - (getGamePiece().getWidth() / 2);
				float posY = ((gameView.getCellSize() * gameView.getGameBoard().getGridSquareAtIndex(i).GetY()) + gameView.getCenterOffset()) + (gameView.getCellSize() / 2) - (getGamePiece().getHeight() / 2);
				aniamteToPoints.add(new float[]{posX, posY});
			}
		}
		
		getGamePiece().animateDrag(gameView, aniamteToPoints);
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public boolean isActive() {
		return _isActive;
	}

	//Override this in base class
	public void deactivate() {
		_isActive = false;
	}
	
	//Override this in base class
	public void activate(EscapeTheCaveGameView view) {
		_gameBoard = new WeakReference<EscapeTheCaveGameView>(view);
		_isActive = true;
	}
		
	public void setDisplayPicture(Bitmap displayPicture) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		displayPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
		_displayPictureData = stream.toByteArray();
	}
	
	public boolean hasGeneratedDisplayPicture() {
		return _hasGeneratedDisplayPicture;
	}
	
	public void setHasGeneratedDisplayPicture(Boolean hasGeneratedDisplayPicture) {
		_hasGeneratedDisplayPicture = hasGeneratedDisplayPicture;
	}
	
	public Bitmap getDisplayPicture() {
		return BitmapFactory.decodeByteArray(_displayPictureData, 0, _displayPictureData.length);
	}
	
	public void setColour(int colour) {
		_colour = colour;
	}

	public int getColour() {
		return _colour;
	}
}
