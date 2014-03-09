package me.eddielee.escapethecave.game;

public class LocalPlayer extends Player {

	private static final long serialVersionUID = -2350148879579152340L;
	
	@Override
	public void activate(EscapeTheCaveGameView view) {
		super.activate(view);
		view.getDice().setDiceRoleCompleteListener(this);
		view.getDice().setShakeToRollEnabledState(true);
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
		getGamePiece().setIsDraggable(false);
	}
	
	@Override
	public void onDiceRoleComplete(int value) {
		int newValue = getGamePiece().getCurrentCellValue() + value;
		newValue = newValue > 80 ? 80 : newValue;
		newValue = newValue < 0 ? 0 : newValue;
		super.getGamePiece().setCorrectCellValue(newValue);
		super.getGameBoard().getDice().setShakeToRollEnabledState(false);
		super.getGameBoard().getDice().setDiceRoleCompleteListener(null);
		super.getGamePiece().setIsDraggable(true);
	}
}
