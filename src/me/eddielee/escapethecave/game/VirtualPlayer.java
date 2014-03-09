package me.eddielee.escapethecave.game;

public class VirtualPlayer extends Player {

	private static final long serialVersionUID = -6129406376551213821L;

	@Override
	public void activate(EscapeTheCaveGameView view) {
		super.activate(view);
		
		view.getDice().rollDiceForResult(this);
	}
	
	@Override
	public void setGamePiece(GamePiece gamePiece) {
		super.setGamePiece(gamePiece);
		gamePiece.setIsDraggable(false);
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
	}
	
	@Override
	public void onDiceRoleComplete(int value) {
		moveGamePiece(value);
	}	
}
