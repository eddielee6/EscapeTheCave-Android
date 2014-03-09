package me.eddielee.escapethecave.dataaccess;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GameStatsRepository {
	
	private SQLiteDatabase _db;
	
	public GameStatsRepository(Context context) {
		 _db = new DatabaseHandler(context).getWritableDatabase();
	}
	
	public void DeleteAllStatsForUser(int userId) {
		Cursor recordset = _db.rawQuery("DELETE FROM GameHistory WHERE UserId = ?", new String[] { String.valueOf(userId) });
	    recordset.moveToFirst(); //Doesn't work without this
	    recordset.close();
	}
	
	public int AddNewGame(int userId) {
		Cursor recordset = _db.rawQuery("SELECT MAX(GameId) FROM GameHistory", null, null);
	    recordset.moveToFirst();
    	int newID = recordset.getInt(0) + 1;
		
		Cursor insert = _db.rawQuery("INSERT INTO GameHistory VALUES(?, ?, ?)", new String[] { String.valueOf(newID), String.valueOf(userId), String.valueOf(0) });
		insert.moveToFirst(); //Doesn't work without this
		insert.close();
	    return newID;
	}
	
	public void SetWinStateForGameId(int userId, int gameId, boolean didWin) {
		Cursor recordset = _db.rawQuery("UPDATE GameHistory SET DidWin = ? WHERE UserId = ? AND GameId = ?", new String[] { (didWin ? String.valueOf(1) : String.valueOf(0)), String.valueOf(userId), String.valueOf(gameId) });
	    recordset.moveToFirst(); //Doesn't work without this
	    recordset.close();
	}
	
	public int GetGamesPlayedForUser(int userId) {
		Cursor recordset = _db.rawQuery("SELECT Count(*) FROM GameHistory WHERE UserId = ?", new String[] { String.valueOf(userId) });
	    recordset.moveToFirst();
    	return recordset.getInt(0);
	}
	
	public int GetGamesWonForUser(int userId) {
		Cursor recordset = _db.rawQuery("SELECT Count(*) FROM GameHistory WHERE UserId = ? AND DidWin = 1", new String[] { String.valueOf(userId) });
	    recordset.moveToFirst();
    	return recordset.getInt(0);
	}
	
	public int GetGamesLostForUser(int userId) {
		Cursor recordset = _db.rawQuery("SELECT Count(*) FROM GameHistory WHERE UserId = ? AND DidWin = 0", new String[] { String.valueOf(userId) });
	    recordset.moveToFirst();
    	return recordset.getInt(0);
	}
}