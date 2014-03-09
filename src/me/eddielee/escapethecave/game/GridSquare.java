package me.eddielee.escapethecave.game;

public class GridSquare {
	private int _x;
	private int _y;
	private Boolean _isMagic;
	
	public GridSquare(int x, int y, Boolean isMagic) {
		this._x = x;
		this._y = y;
		this._isMagic = isMagic;
	}
	
	public GridSquare(int x, int y) {
		this._x = x;
		this._y = y;
		this._isMagic = false;
	}
	
	public int GetX() {
		return this._x;
	}
	
	public int GetY() {
		return this._y;
	}
	
	public Boolean IsMagic() {
		return this._isMagic;
	}
}