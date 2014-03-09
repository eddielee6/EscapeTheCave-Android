package me.eddielee.escapethecave.game;

public class RemotePlayer extends Player {

	private static final long serialVersionUID = -6129406376551213821L;
	
	private final String _macAddress;
	
	public RemotePlayer(String macAddress) {
		_macAddress = macAddress;
	}
	
	public String getMacAddress() {
		return _macAddress;
	}

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
