package me.eddielee.escapethecave.game;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerManager implements Serializable {

	private static final long serialVersionUID = 4288980478858237838L;
	public static final int MAX_PLAYERS = 4;
	
	private Player player1 = null;
	private Player player2 = null;
	private Player player3 = null;
	private Player player4 = null;
	
	private int _currentActivePlayerIndex;
	
	public PlayerManager() {
		_currentActivePlayerIndex = 1;
	}
	
	public int getActivePlayerIndex() {
		return _currentActivePlayerIndex;
	}
	
	public void setPlayer(int playerNumber, Player player) {
		switch(playerNumber) {
		case 1:
			player1 = player;
			break;
		case 2:
			player2 = player;
			break;
		case 3:
			player3 = player;
			break;
		case 4:
			player4 = player;
			break;
		}
	}
	
	public Player getPlayer(int playerNumber) {
		switch(playerNumber) {
		case 1:
			return player1;
		case 2:
			return player2;
		case 3:
			return player3;
		case 4:
			return player4;
		default:
			return null;
		}
	}
	
	public ArrayList<RemotePlayer> getRemotePlayers() {
		ArrayList<RemotePlayer> remotePlayers = new ArrayList<RemotePlayer>();
		for(int i = 0; i < MAX_PLAYERS; i++) {
			if(getPlayer(i) != null && getPlayer(i).getClass() == RemotePlayer.class) {
				remotePlayers.add((RemotePlayer)getPlayer(i));
			}
		}
		return remotePlayers;
	}
	
	public void activateNextPlayer(EscapeTheCaveGameView view) {
		_currentActivePlayerIndex ++;
		if(_currentActivePlayerIndex > MAX_PLAYERS) {
			_currentActivePlayerIndex = 1;
		}
		
		Boolean foundNextPlayer = false;
		while(!foundNextPlayer) {
			for(int i = 1; i <= MAX_PLAYERS; i++) {
				if(i == _currentActivePlayerIndex) {
					if(getPlayer(i) != null) {
						getPlayer(i).activate(view);
						foundNextPlayer = true;
					} else {
						_currentActivePlayerIndex++;
						if(_currentActivePlayerIndex > MAX_PLAYERS) {
							_currentActivePlayerIndex = 1;
						}
					}
				} else {
					if(getPlayer(i) != null) {
						getPlayer(i).deactivate();
					}
				}
			}
		}
	}
	
	public int numberOfPlayers() {
		int numberOfPlayers = 0;
		
		if(player1 != null) numberOfPlayers++;
		if(player2 != null) numberOfPlayers++;
		if(player3 != null) numberOfPlayers++;
		if(player4 != null) numberOfPlayers++;
		
		return numberOfPlayers;
	}
}
